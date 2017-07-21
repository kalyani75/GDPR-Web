package example.jpa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

import common.Constants;

// This class define the RESTful API to fetch all the chat transcript files.
// <basepath>/api/chatinfo

@Path("/personalinfo")
public class PersonalInfo {
	private static final String TARGET_URL = "https://vision.googleapis.com/v1/images:annotate?";
	private static final String API_KEY = "key=AIzaSyBtzn_SAfs9EoJ1JRwaaCFrxP6UYpnwYZ8";

	@GET
	public String getInformation(@QueryParam("id") String fileName) throws Exception, IOException {

		System.out
				.println("~~~~~~~~~~~~~ PersonalInfo :::::::Fetching Personal Info ");

		
		JSONObject jsonObj = new JSONObject() ;
		ClassLoader classLoader = this.getClass().getClassLoader() ;
		URL url = classLoader.getResource("input-data") ;
		String sDir = "input-data";
		
		String filepath = classLoader.getResource("input-data").getPath()+"/"+fileName;
        File f = new File(filepath);
        String mimetype= new MimetypesFileTypeMap().getContentType(f);
        String type = mimetype.split("/")[0];
        String text=" ";
        if(type.equals("image"))
        {
        	 System.out.println("It's an image");
        	  text = callImageAPI(fileName) ;
        	 jsonObj.put("text",text) ;
        }
           
        else 
        {
            System.out.println("It's NOT an image");
        
		 text = getStringFromFile(classLoader.getResource("input-data").getPath()+"/"+fileName) ;
		jsonObj.put("text",text) ;
        }
		URI comProfileURI = new URI("http://GDPRRest.mybluemix.net/GDPR/api/rest/personalInfo").normalize();
	//URI comProfileURI = new URI("http://localhost:9081/GDPR/api/rest/personalInfo").normalize();
		Request comProfileRequest = Request.Post(comProfileURI)
				.addHeader("Accept", "application/json")
				.addHeader("Content-Type","application/json")
				.bodyString(jsonObj.toString(), ContentType.TEXT_PLAIN) ;
		System.out.println("jString:"+jsonObj.toString()) ;
		Executor comEexecutor = Executor.newInstance();

		String personStr = comEexecutor.execute(comProfileRequest).returnContent().asString() ;
		JSONObject personObj = JSONObject.parse(personStr) ;
		personObj = scorer(personObj) ;
		personObj.put("text", text) ;
		
		System.out
				.println("~~~~~~~~~~~~~ Personal Info ::::::DBInfoObj.toString()::::"
						+ personObj.toString());
		return personObj.toString();

	}
	private static JSONObject  scorer(JSONObject jObj)
	{
		int PI_cnt=0;
		int SII_cnt=0;
		int II_cnt=0;
		String[] words={"PIN","Passport_Number","Driving_License","SSN","Full_Name"} ;
		List<String> PI = Arrays.asList(words); 
		String[] words1={"Bank_account_number","IP_address","Car_Number","Email_Address","Phone_number","Year_of_birth","Vehicle_registration_number","Title_of_publication","Student_ID_number","Insurance_number"} ;
		List<String> SII = Arrays.asList(words1); 
		String[] words2={"Postal_code","District","Municipality_of_residence","Region","Major_region","Municipality_type","Age","Gender","Marital_status","Household_composition","Occupation","Industry_of_employment","Employment_Status","Education","Field_of_education","Mother_tongue","Nationality","Employer","Web_page_address","Student_Id","Insurance_number","Bank_account_number","Medical_info","Ethnic_group","Crime_or_punishment","Membership_in_a_trade_union","Political_or_religious_allegiance","Other_membership","Need_for_social_welfare","Social_welfare_services_and_benefits_received","Sexual_orientation"} ;
		List<String> II = Arrays.asList(words2); 
		String[] tmp={};
		List<String> entityTypes = new ArrayList<String>();
		JSONArray newEntityArr = new JSONArray() ;
		JSONArray jArr =(JSONArray)jObj.get("entities") ;
		for (int i=0;i<jArr.size();i++)
		{
			JSONObject entityObj = (JSONObject)jArr.get(i) ;
			String type = (String)entityObj.get("type");
			if (entityTypes.contains(type))
			  continue;
			entityTypes.add(type) ;
			newEntityArr.add(entityObj) ;
			if (PI.contains(type))
				PI_cnt++;
			else if (SII.contains(type))
				SII_cnt++ ;
			else if (II.contains(type))	
				II_cnt++;
		}
		if (PI_cnt>0)
			jObj.put("Sensitivity", "RED") ;
		else if (SII_cnt+II_cnt == 0)
			jObj.put("Sensitivity", "GREEN") ;
		else if (SII_cnt+II_cnt <4)
			jObj.put("Sensitivity", "YELLOW") ;
		else 
			jObj.put("Sensitivity", "RED") ;
		
		jObj.put("entities", newEntityArr) ;
		return jObj ;
	}
	public static String callImageAPI(String fileName) throws Exception
	{
		String imageText="";
		URL serverUrl = new URL(TARGET_URL + API_KEY);
		URLConnection urlConnection = serverUrl.openConnection();
		HttpURLConnection httpConnection = (HttpURLConnection)urlConnection;
		httpConnection.setRequestMethod("POST");
		httpConnection.setRequestProperty("Content-Type", "application/json");
		httpConnection.setDoOutput(true);
		BufferedWriter httpRequestBodyWriter = new BufferedWriter(new
                OutputStreamWriter(httpConnection.getOutputStream()));
		httpRequestBodyWriter.write
		 ("{\"requests\":  [{ \"features\":  [ {\"type\": \"TEXT_DETECTION\""
		 +"}], \"image\": {\"source\": { \"gcsImageUri\":"
		 +" \"gs://kdimagebucket/"+fileName+"\"}}}]}");
		
	//	imageFile.
	/*	httpRequestBodyWriter.write
		 ("{\"requests\":  [{ \"features\":  [ {\"type\": \"TEXT_DETECTION\""
		 +"}], \"image\": {\"content\":\""+imageData+" \" }}]}");*/
		httpRequestBodyWriter.close();
		String response = httpConnection.getResponseMessage();
		if (httpConnection.getInputStream() == null) {
			   System.out.println("No stream");
			   return imageText;
	}

		Scanner httpResponseScanner = new Scanner (httpConnection.getInputStream());
		String resp = "";
		while (httpResponseScanner.hasNext()) {
		   String line = httpResponseScanner.nextLine();
		   resp += line;
		 //  System.out.println(line);  //  alternatively, print the line of response
		}
		JSONObject jObj = JSONObject.parse(resp) ;
		imageText=(String) ((JSONObject) ((JSONObject)((JSONArray)jObj.get("responses")).get(0)).get("fullTextAnnotation")).get("text");
		System.out.println(((JSONObject) ((JSONObject)((JSONArray)jObj.get("responses")).get(0)).get("fullTextAnnotation")).get("text"));
		httpResponseScanner.close();
		return imageText ;
	}
	 public static String getStringFromFile(String filename) throws Exception {
		    
		 InputStream is = new FileInputStream(filename) ;
		 BufferedReader br = null;
		    final StringBuilder sb = new StringBuilder();

		    String line;
		    try {

		      br = new BufferedReader(new InputStreamReader(is));
		      while ((line = br.readLine()) != null) {
		        sb.append(line+"\n");
		      }

		    } catch (final IOException e) {
		      e.printStackTrace();
		    } finally {
		      if (br != null) {
		        try {
		          br.close();
		        } catch (final IOException e) {
		          e.printStackTrace();
		        }
		      }
		    }
		    return sb.toString();
		  }
}
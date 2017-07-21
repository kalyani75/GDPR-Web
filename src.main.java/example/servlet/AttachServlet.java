package example.servlet;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;


/**
 * Servlet implementation class SimpleServlet
 */
@WebServlet("/attach")
@MultipartConfig()
public class AttachServlet extends HttpServlet {
	private static final String TARGET_URL = "https://vision.googleapis.com/v1/images:annotate?";
	private static final String API_KEY = "key=AIzaSyBtzn_SAfs9EoJ1JRwaaCFrxP6UYpnwYZ8";

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.getWriter().print("Hello World!");
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
    	response.setContentType("text/plain;charset=UTF-8");
    	Part part = request.getPart("file");
    	String fileName = request.getParameter("filename");
    	String mime = request.getParameter("filetype");
    	String type = mime.split("/")[0];
        String text=" ";
        JSONObject jsonObj = new JSONObject() ;
        if(type.equals("image"))
        {
        	 System.out.println("It's an image");
        	  try {
				text = callImageAPI(fileName) ;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         	 jsonObj.put("text",text) ;
        }else 
        {
            System.out.println("It's NOT an image");
            InputStream binStream = part.getInputStream();
    		//binStream.
    		final byte[] bytes = new byte[1024];
    		binStream.read(bytes) ;
    		text =new String(bytes) ;
		 
		jsonObj.put("text",text) ;
        }
    	
        URI comProfileURI=null;
		try {
			comProfileURI = new URI("http://GDPRRest.mybluemix.net/GDPR/api/rest/personalInfo").normalize();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
    		personObj.put("File_Name", fileName) ;
    		
    	
		System.out
		.println("~~~~~~~~~~~~~ Batch Classify :::::::Classifying Batch Files :"+personObj.toString());
		//System.out.write(bytes);

		System.out.println("Classify completed.");
		
		response.getWriter().println(personObj.toString());
		//response.getWriter().println(resultObject.toString());
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
		String apiKey = System.getenv("API_KEY") ;
		System.out.println("APIKEY: "+apiKey); ;
		URL serverUrl = null ;
		if (apiKey!=null)
			serverUrl = new URL(TARGET_URL + apiKey);
		else
			serverUrl = new URL(TARGET_URL + API_KEY);
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
    
    public static byte[] convertInputStreamToByteArrary(InputStream in) throws IOException {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    final int BUF_SIZE = 1024;
	    byte[] buffer = new byte[BUF_SIZE];
	    int bytesRead = -1;
	    while ((bytesRead = in.read(buffer)) > -1) {
	        out.write(buffer, 0, bytesRead);
	    }
	    in.close();
	    byte[] byteArray = out.toByteArray();
	    return byteArray;
	}
}

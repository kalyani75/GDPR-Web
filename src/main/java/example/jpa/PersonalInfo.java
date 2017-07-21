package example.jpa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

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

	@GET
	public String getInformation(@QueryParam("id") String fileName) throws Exception, IOException {

		System.out
				.println("~~~~~~~~~~~~~ PersonalInfo :::::::Fetching Personal Info ");

		
		JSONObject jsonObj = new JSONObject() ;
		ClassLoader classLoader = this.getClass().getClassLoader() ;
		URL url = classLoader.getResource("input-data") ;
		String sDir = "input-data";
		
		String text = getStringFromFile(classLoader.getResource("input-data").getPath()+"/"+fileName) ;
		jsonObj.put("text",text) ;
		URI comProfileURI = new URI("http://GDPRRest.mybluemix.net/api/rest/personalInfo").normalize();
//	URI comProfileURI = new URI("http://localhost:9081/GDPR/api/rest/personalInfo").normalize();
		Request comProfileRequest = Request.Post(comProfileURI)
				.addHeader("Accept", "application/json")
				.addHeader("Content-Type","application/json")
				.bodyString(jsonObj.toString(), ContentType.TEXT_PLAIN) ;
		System.out.println("jString:"+jsonObj.toString()) ;
		Executor comEexecutor = Executor.newInstance();

		String personStr = comEexecutor.execute(comProfileRequest).returnContent().asString() ;
		JSONObject personObj = JSONObject.parse(personStr) ;

/*		JSONObject personObj = new JSONObject() ;
		JSONObject jObj = new JSONObject() ;
		jObj.put("type", "Full Name") ;
		jObj.put("text", "Kalyani Deshpande") ;
		JSONObject jObj1 = new JSONObject() ;
		jObj1.put("type", "Company") ;
		jObj1.put("text", "IBM") ;
JSONArray jArr = new JSONArray() ;

jArr.add(jObj) ;
jArr.add(jObj1) ;
		personObj.put("entities", jArr);
	*///	
		JSONObject returnObj = new JSONObject() ;
		//returnObj.put("entities", personObj.get(""));
		System.out
				.println("~~~~~~~~~~~~~ Personal Info ::::::DBInfoObj.toString()::::"
						+ personObj.toString());
		return personObj.toString();

	}
	 public static String getStringFromFile(String filename) throws Exception {
		    
		 InputStream is = new FileInputStream(filename) ;
		 BufferedReader br = null;
		    final StringBuilder sb = new StringBuilder();

		    String line;
		    try {

		      br = new BufferedReader(new InputStreamReader(is));
		      while ((line = br.readLine()) != null) {
		        sb.append(line);
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
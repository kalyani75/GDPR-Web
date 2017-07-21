package example.jpa;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

import common.Constants;

// This class define the RESTful API to fetch the results  for each rules for Chat Transcript Files
// <basepath>/api/ruleresult

@Path("/call")
public class ChatFileContent {

	@GET 	
	@Path("/transcript")
	public String getInformation(@QueryParam("id") String fileName)
			throws Exception, IOException {

		System.out
				.println("~~~~~~~~~~~~~ ChatFileContent :::::: Fetching File Content of Chat Transcript File "
						+ fileName);

		String apiUrl = Constants.BASE_PATH +  "/api/transcript/"
				+ fileName;

		//Invoke the API and fetch the transcript File Content
		ClassLoader classLoader = this.getClass().getClassLoader() ;
		URL url = classLoader.getResource("input-data") ;
		String sDir = "input-data";
		
		String text = getStringFromFile(classLoader.getResource("input-data").getPath()+"/"+fileName) ;
		

			
			System.out
			.println("~~~~~~~~~~~~~ ChatFileContent ::::::ChatFileContent.toString()::::"
					+ text);
			
			return text ;
			

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
	
	@POST
	@Path("/transcript")
	public String updateFile(@QueryParam("id") String fileName, @FormParam("transcript") String transcript)
			throws Exception, IOException {

		System.out
				.println("~~~~~~~~~~~~~ ChatFileContent :::::: Fetching File Content of Chat Transcript File "
						+ fileName);

		String apiUrl = Constants.BASE_PATH +  "/api/transcript/"
				+ fileName;

		//Invoke the API and fetch the transcript File Content
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(apiUrl);		
		Entity<String> entity = Entity.text( transcript);
		
		Response response = target.request(MediaType.TEXT_PLAIN).put(entity);	
		
		System.out
				.println("~~~~~~~~~~~~~ ChatFileContent ::::::updateFile.toString()::::"
						+ response.getStatus());
		if ( response.getStatus() == 200 ){
			return "File updated succesfull" ;
		}else {
			return "There was an error in updating file" ;
		}

	}
	
	
}



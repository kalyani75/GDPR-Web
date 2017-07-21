package example.jpa;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

import common.Constants;

// This class define the RESTful API to fetch all the chat transcript files.
// <basepath>/api/chatinfo

@Path("/chatinfo")
public class ChatResource {

	@GET
	public String getInformation() throws Exception, IOException {

		System.out
				.println("~~~~~~~~~~~~~ ChatResource :::::::Fetching Transcript Files ");

		// Invoke API and fetch the transcript File
	/*	Client client = ClientBuilder.newClient();
		WebTarget target = client
				.target( Constants.BASE_PATH + "/api/transcript");

		String response = target.request(MediaType.APPLICATION_JSON).get(
				String.class);

		JSONObject jobj = (JSONObject) JSON.parse(response);

		// Look for the JSON Response for transcript files
		JSONArray chatFileNames = new JSONArray();
		String fname = null;
		JSONArray tlist = (JSONArray) jobj.get("transcripts");
		// Loop through and populate an array of Filename
		if (tlist !=null) {
			for (int i = 0; i < tlist.size(); i++) {
				jobj = (JSONObject) tlist.get(i);
				fname = (String) jobj.get("filename");
				chatFileNames.add(fname);
			}
		}*/


		ClassLoader classLoader = this.getClass().getClassLoader() ;
		URL url = classLoader.getResource("input-data") ;
        
		JSONArray chatFileNames = new JSONArray();
		String sDir = "input-data";
		 File[] faFiles = new File(classLoader.getResource("input-data").getPath()).listFiles() ;
		//	  File[] faFiles = new File(sDir).listFiles();
			  for(File file: faFiles){
				  chatFileNames.add(file.getName()) ;
			  }
			
		
		JSONObject FileInfoObj = new JSONObject();
		FileInfoObj.put("name", chatFileNames);

		System.out
				.println("~~~~~~~~~~~~~ ChatResource ::::::DBInfoObj.toString()::::"
						+ FileInfoObj.toString());
		return FileInfoObj.toString();

	}
}
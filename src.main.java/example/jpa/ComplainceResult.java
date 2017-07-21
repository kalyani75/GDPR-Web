package example.jpa;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

import common.Constants;

// This class define the RESTful API to fetch the results  for each rules for Chat Transcript Files
// <basepath>/api/ruleresult

@Path("/ruleresult")
public class ComplainceResult {

	@GET
	public String getInformation(@QueryParam("id") String fileName)
			throws Exception, IOException {

		System.out
				.println("~~~~~~~~~~~~~ ComplainceResult :::::: Fetching Rules and Result of Chat Transcript File "
						+ fileName);

		String apiUrl =  Constants.BASE_PATH + "/api/validatechat/"
				+ fileName;

		//Invoke the API and fetch the transcript File complaince check resultsClientConfig configuration = new ClientConfig();
		
		javax.ws.rs.client.ClientBuilder cb = ClientBuilder.newBuilder();
        cb.property("com.ibm.ws.jaxrs.client.timeout", "10000"); 
        Client client = cb.build(); 
		
		//Client client = ClientBuilder.newClient();
		
		WebTarget target = client.target(apiUrl);

		String response = target.request(MediaType.APPLICATION_JSON).get(
				String.class);

		JSONObject jobj = (JSONObject) JSON.parse(response);

		// Look for the JSON Response for rules files
		JSONArray chatFileResults = new JSONArray();

		JSONArray tlist = (JSONArray) jobj.get("rules");

		// Loop through the rules and store rules and results in an Array
		if(tlist!=null){
			for (int i = 0; i < tlist.size(); i++) {
	
				jobj = (JSONObject) tlist.get(i);
	
				JSONObject chatTranscript = new JSONObject();
				String strRule = (String) jobj.get("rule");
				String strResult = (String) jobj.get("result");
				String strDescr=(String) jobj.get("text");
				String strExpl=(String) jobj.get("resultHeader");
				String failureLine = "" ;
				if (jobj.containsKey("mandatoryCheckFailLine")){
					failureLine = (String) jobj.get("mandatoryCheckFailLine");
				}
				String moreDetails = "" ;
				if (jobj.containsKey("moreDetails")){
					moreDetails = (String) jobj.get("moreDetails");
				}
				
				JSONArray sequenceList = (JSONArray) jobj.get("sequenceList");
				
				if (strResult == null)
					strResult = "Rule Not Configured";
				chatTranscript.put("rule", strRule);
				chatTranscript.put("desc", strDescr);
				chatTranscript.put("result", strResult);
				chatTranscript.put("reason", strExpl);
				chatTranscript.put("sequenceList", sequenceList);				
				chatTranscript.put("failureLine", failureLine);
				chatTranscript.put("moreDetails", moreDetails);
				
				chatFileResults.add(chatTranscript);
			}
		}
		JSONObject FileInfoObj = new JSONObject();

		FileInfoObj.put("rules", chatFileResults);
		System.out
				.println("~~~~~~~~~~~~~ ComplainceResult ::::::chatFileResults.toString()::::"
						+ chatFileResults.toString());
		return FileInfoObj.toString();

	}
}
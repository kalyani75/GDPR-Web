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
import com.ibm.json.java.JSONObject;

import common.Constants;

// This class define the RESTful API to fetch the results  for each rules for Chat Transcript Files
// <basepath>/api/ruleresult

@Path("/ruleconfig")
public class RuleConfiguration {

	@GET 
	public String getInformation(@QueryParam("id") String ruleName)
			throws Exception, IOException {

		System.out
				.println("~~~~~~~~~~~~~ RuleConfiguration :::::: Fetching File Content of Chat Transcript File "
						+ ruleName);

		String apiUrl = "http://demochatcompliance.mybluemix.net/api/rule/"
				+ ruleName;

		//Invoke the API and fetch the transcript File Content
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(apiUrl);

		String response = target.request(MediaType.APPLICATION_JSON).get(
				String.class);

		JSONObject jobj = (JSONObject) JSON.parse(response);
		
		JSONObject fileContentObj = new JSONObject();
		fileContentObj.put("filecontent", jobj);
		
		System.out
				.println("~~~~~~~~~~~~~ RuleConfiguration ::::::ChatFileContent.toString()::::"
						+ jobj.toString());
		//return fileContentObj.toString();
		
		return response;

	}
	
	
}
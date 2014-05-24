package alfresco.extension.de;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class PackDirsTask {
	
	public RestTemplate restTemplate;
	
	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void pack() {
		Map<String, String> vars = new HashMap<String, String>();
		vars.put("folderToSearch", "2001");
		try {
			restTemplate.postForLocation("http://localhost:8080/alfresco/service/zip?folderToSearch={folderToSearch}", null, vars);
			System.out.println("Year zipper executed successfully");
		} catch (RestClientException e) {
			e.printStackTrace();
		}
	}

}

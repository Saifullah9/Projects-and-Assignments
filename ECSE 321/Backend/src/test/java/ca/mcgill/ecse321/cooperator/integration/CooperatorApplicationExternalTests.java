package ca.mcgill.ecse321.cooperator.integration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CooperatorApplicationExternalTests {
	@Autowired
	private TestRestTemplate restTemplate;

	private HttpHeaders headers = new HttpHeaders();



	// GROUP 17
	@Test
	public void testGetAllStudents17() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		String url = "https://ecse321-group17.herokuapp.com/students";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.GET, entity, String.class);
		String result = response.getBody().toString(); 
		assertEquals("[]", result);
	}
	// GROUP 17
	@Test
	public void testGetAllCoopAdmins() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		String url = "https://ecse321-group17.herokuapp.com/admins";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.GET, entity, String.class);
		String result = response.getBody().toString(); 
		assertEquals("[]", result);
	}


	// TESTS FOR GROUP 12
	@Test
	public void testGetStudents12() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		String url = "https://ecse321-group12.herokuapp.com/students";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.GET, entity, String.class);
		String result = response.getBody().toString(); 
		assertEquals(HttpStatus.OK, response.getStatusCode());

		// IT SHOULD RETURN AN EMPTY JSON
		assertEquals("[]", result);
	}

	// TESTS FOR GROUP 12
	@Test
	public void testGetEmployers12() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		String url = "https://ecse321-group12.herokuapp.com/employers";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.GET, entity, String.class);
		String result = response.getBody().toString(); 
		assertEquals(HttpStatus.OK, response.getStatusCode());
		// IT SHOULD RETURN AN EMPTY JSON
		assertEquals("[]", result);
	}



	@Test
	public void testGetEventNotifications12() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		String url = "https://ecse321-group12.herokuapp.com/getEventNotifications";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		System.out.print(builder.toUriString());
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.GET, entity, String.class);
		String result = response.getBody().toString(); 
		assertEquals(HttpStatus.OK, response.getStatusCode());
		// IT SHOULD RETURN AN EMPTY JSON
		assertEquals("[]", result);

	}

}

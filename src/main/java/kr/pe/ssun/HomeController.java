package kr.pe.ssun;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	private static final String REGISTRATION_TOKEN_FILE = "token.txt";
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/send", method = RequestMethod.GET)
	public void home(HttpServletRequest request, Locale locale, Model model) {
		RestTemplate restTemplate = new RestTemplate();
		URI uri = UriComponentsBuilder.fromHttpUrl("https://fcm.googleapis.com/fcm/send").build().toUri();
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "key=AIzaSyAc2W4OQhkEuocMzTYPz1H23ywaDsO7Y8Q");
		headers.set("Content-Type", "application/json");
		
		for (String token : getRegistrationTokens()) {
			String body = "{"
					+ "\"notification\" : {"
					+ " \"title\" : \"Bark!\","
					+ " \"text\" : \"Bark!\""
					+ "  }"
					+ ", \"to\" : \"" + token + "\""
					+ "}";
			HttpEntity entity = new HttpEntity(body, headers);
			ResponseEntity<String> response = restTemplate.postForEntity(uri, entity, String.class);
		}		
		
//		model.addAttribute("body", body);
//		model.addAttribute("response_code", response.getStatusCode().toString());
//		model.addAttribute("response_body", response.getBody());
		
		
//		return "home";
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String register(HttpServletRequest request, Model model) {
		ArrayList<String> tokens = getRegistrationTokens();
		String newToken = request.getParameter("token");
		
		boolean hasToken = false;
		for (String token : tokens) {
			if (token.equals(newToken)) {
				hasToken = true;
				break;
			}
		}
		
		boolean registerToken = false;
		if (!hasToken) {
			registerToken = registToken(newToken);
		}
		
		// debug
		String debug_tokens = "";
		for (String token : getRegistrationTokens()) {
			debug_tokens += token;
			debug_tokens += "<br>";
		}
		
		model.addAttribute("newToken", newToken);
		model.addAttribute("hasToken", hasToken);
		model.addAttribute("registerToken", registerToken);
		model.addAttribute("tokens", debug_tokens);
		
		return "register";
	}
	
	private ArrayList<String> getRegistrationTokens() {
		ArrayList<String> tokens = new ArrayList<String>();
		
		File file = new File(REGISTRATION_TOKEN_FILE);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileReader fr = null;
		try {
			 fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			return tokens;
		}
		BufferedReader br = new BufferedReader(fr);
		
		String token = null;
		try {
			while ((token = br.readLine()) != null) {
				tokens.add(token);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return tokens;
	}
	
	private boolean registToken(String newToken) {
		ArrayList<String> tokens = getRegistrationTokens();
		tokens.add(newToken);
		
		File file = new File(REGISTRATION_TOKEN_FILE);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
		} catch (IOException e) {
			e.printStackTrace();
			
			return false;
		}
		BufferedWriter bw = new BufferedWriter(fw);
		for (String token : tokens) {
			try {
				bw.write(token);
				bw.newLine();
			} catch (IOException e) {
				e.printStackTrace();
				
				return false;
			}
		}
		
		try {
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
}

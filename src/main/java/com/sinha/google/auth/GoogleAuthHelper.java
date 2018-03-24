package com.sinha.google.auth;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.sinha.google.bean.EmailCount;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A helper class for Google's OAuth2 authentication API.
 * @author Nitish Sinha
 */
public final class GoogleAuthHelper {

	/**
	 * Please provide a value for the CLIENT_ID constant before proceeding, set this up at https://code.google.com/apis/console/
	 */
	private static final String CLIENT_ID = "108722501056-11tds3ur1794l9keraj399a1hu9o3jac.apps.googleusercontent.com";
	/**
	 * Please provide a value for the CLIENT_SECRET constant before proceeding, set this up at https://code.google.com/apis/console/
	 */
	private static final String CLIENT_SECRET = "rointfsXoeSrCUQHDPxIxg4M";

	private static final String APPLICATION_NAME ="Gmail Search API";
	/**
	 * Callback URI that google will redirect to after successful authentication
	 */
	private static final String CALLBACK_URI = "http://gmailsearch-1084.appspot.com/Controller";
	
	// start google authentication constants
	private static final List<String> SCOPE = Arrays.asList("https://www.googleapis.com/auth/userinfo.profile;https://www.googleapis.com/auth/userinfo.email;https://www.googleapis.com/auth/gmail.readonly".split(";"));
	private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	// end google authentication constants
	
	private String stateToken;
	
	private final GoogleAuthorizationCodeFlow flow;
	
	/**
	 * Constructor initializes the Google Authorization Code Flow with CLIENT ID, SECRET, and SCOPE 
	 */
	public GoogleAuthHelper() {
		flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT,
				JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, SCOPE).build();
		
		generateStateToken();
	}

	/**
	 * Builds a login URL based on client ID, secret, callback URI, and scope 
	 */
	public String buildLoginUrl() {
		
		final GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
		
		return url.setRedirectUri(CALLBACK_URI).setState(stateToken).build();
	}
	
	/**
	 * Generates a secure state token 
	 */
	private void generateStateToken(){
		
		SecureRandom sr1 = new SecureRandom();
		
		stateToken = "google;"+sr1.nextInt();
		
	}
	
	/**
	 * Accessor for state token
	 */
	public String getStateToken(){
		return stateToken;
	}
	
	public Credential getCredential(final String authCode) throws IOException {
		final GoogleTokenResponse response = flow.newTokenRequest(authCode).setRedirectUri(CALLBACK_URI).execute();
		String accessToken = response.getAccessToken();
		final Credential credential = flow.createAndStoreCredential(response, null);
		return credential;
	}
	
	public String getAccessToken(final String authCode) throws IOException{
		final GoogleTokenResponse response = flow.newTokenRequest(authCode).setRedirectUri(CALLBACK_URI).execute();
		String accessToken = response.getAccessToken();
		return accessToken;
	}
	
	public Credential buildCredentialFromToken(String accessToken) throws IOException
	{
		GoogleCredential credential = new GoogleCredential.Builder()
        .setTransport(HTTP_TRANSPORT)
        .setJsonFactory(JSON_FACTORY)
        .setClientSecrets(CLIENT_ID, CLIENT_SECRET).build();
		credential.setAccessToken(accessToken);
		return credential;
	}
	
	/**
	 * Expects an Authentication Code, and makes an authenticated request for the user's profile information
	 * @return JSON formatted user profile information
	 * @param authCode authentication code provided by google
	 */
	public String getUserInfoJson(String accessToken ) throws IOException {
		
		Credential credential = buildCredentialFromToken(accessToken);
		final HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);
		// Make an authenticated request
		final GenericUrl url = new GenericUrl(USER_INFO_URL);
		final HttpRequest request = requestFactory.buildGetRequest(url);
		request.getHeaders().setContentType("application/json");
		final String jsonIdentity = request.execute().parseAsString();
		return jsonIdentity;

	}


	/**
	 * Build and return an authorized Gmail client service.
	 * @return an authorized Gmail client service
	 * @throws IOException
	 */
	public  Gmail getGmailService(String accessToken) throws IOException {
		Credential credential = buildCredentialFromToken(accessToken);
		return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
		.setApplicationName(APPLICATION_NAME)
		.build();
	}

	public static Message getMessage(Gmail service, String userId, String messageId)
			throws IOException {
		Message message = service.users().messages().get(userId, messageId).setFormat("metadata").execute();
		return message;
	}


	public static List<Message> listMessagesMatchingQuery(Gmail service, String userId,
			String query) throws IOException {

		ListMessagesResponse response = service.users().messages().list(userId).setQ(query).execute();

		List<Message> messages = new ArrayList<Message>();
		while (response.getMessages() != null) {
			messages.addAll(response.getMessages());
			if (response.getNextPageToken() != null) {
				String pageToken = response.getNextPageToken();
				response = service.users().messages().list(userId).setQ(query)
						.setPageToken(pageToken).execute();
			} else {
				break;
			}
		}

		return messages;
	}

	public List<EmailCount> getMessages(String query, String accessToken) throws IOException
	{
		Map<String, Integer> hmap = new HashMap<String, Integer>();
		Gmail service = getGmailService(accessToken);

		// Print the labels in the user's account.
		String user = "me";
		List<Message> msgs = listMessagesMatchingQuery(service,user,"in:sent "+query);
		for(Message msg: msgs)
		{
			Message message = getMessage(service, user, msg.getId());;  
			MessagePart mPart = message.getPayload();
			List<MessagePartHeader> partHeader = mPart.getHeaders();
			for(MessagePartHeader header: partHeader)
			{
				if(header.getName().equalsIgnoreCase("To"))
				{
					String emails[] = header.getValue().split("\\,");
					String email = "";
					for(int i=0; i<emails.length; i++)
					{
						email = emails[i].trim();
						hmap.put(email, hmap.get(email)==null?1:hmap.get(email)+1);
					}
				}
				if(header.getName().equalsIgnoreCase("Cc"))
				{
					String emails[] = header.getValue().split("\\,");
					String email = "";
					for(int i=0; i<emails.length; i++)
					{
						email = emails[i].trim();
						hmap.put(email, hmap.get(email)==null?1:hmap.get(email)+1);
					}
				}
				if(header.getName().equalsIgnoreCase("Bcc"))
				{
					String emails[] = header.getValue().split("\\,");
					String email = "";
					for(int i=0; i<emails.length; i++)
					{
						email = emails[i].trim();
						hmap.put(email, hmap.get(email)==null?1:hmap.get(email)+1);
					}
				}
			}
		}
		EmailCount emailObj = null;
		List<EmailCount> listEmail = new ArrayList<EmailCount>();
		for ( String key : hmap.keySet() ) {
			emailObj = new EmailCount(key.trim(), hmap.get(key));
			listEmail.add(emailObj);
		}
		Collections.sort(listEmail);
		List<EmailCount> listToSend = new ArrayList<EmailCount>();
		listToSend.add(listEmail.get(0));
		listToSend.add(listEmail.get(1));
		listToSend.add(listEmail.get(2));
		return listToSend;
	}
	
	public String revoke(String token) throws IOException
	{
		HttpResponse revokeResponse = HTTP_TRANSPORT.createRequestFactory()
			    .buildGetRequest(new GenericUrl(
			        String.format(
			            "https://accounts.google.com/o/oauth2/revoke?token=%s",
			            token))).execute();
		return "revoked";
    }
	

}

package com.sinha.google.servlet;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.*;
import com.sinha.google.auth.GoogleAuthHelper;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class Controller extends HttpServlet{
	public void init() throws ServletException
	  {
	      // Do required initialization
	  }

	  public void doGet(HttpServletRequest request,
	                    HttpServletResponse response)
	            throws ServletException, IOException
	  {
		  String authCode = request.getParameter("code");
		  GoogleAuthHelper helper = new GoogleAuthHelper();
		  String accessToken = helper.getAccessToken(authCode);
		  String json = helper.getUserInfoJson(accessToken);
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> map = mapper.readValue(json,
					new TypeReference<HashMap<String, String>>() {
					});
			
		//Obtain the session object, create a new session if doesn't exist
	        HttpSession session = request.getSession(true);
	        session.setAttribute("credential", accessToken);
	        session.setAttribute("name", map.get("name"));
			session.setAttribute("email", map.get("email"));
	      // Set response content type
	      response.setContentType("text/html");
	      response.sendRedirect("index.jsp");  
	  }
}

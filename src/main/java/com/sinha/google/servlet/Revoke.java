package com.sinha.google.servlet;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.sinha.google.auth.GoogleAuthHelper;

public class Revoke extends HttpServlet{
	public void init() throws ServletException
	  {
	      
	  }

	  public void doGet(HttpServletRequest request,
	                    HttpServletResponse response)
	            throws ServletException, IOException
	  {
		  String token = request.getParameter("token");
		  GoogleAuthHelper helper = new GoogleAuthHelper();
		  helper.revoke(token);
		  HttpSession session = request.getSession(true);
		  session.invalidate();
	      // Set response content type
	      response.setContentType("text/html");
	      response.sendRedirect("index.jsp");  
	  }
}

package com.sinha.google.servlet;

import java.io.*;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;

import com.sinha.google.auth.GoogleAuthHelper;
import com.sinha.google.bean.EmailCount;

public class GetMail extends HttpServlet{
	public void init() throws ServletException
	  {
	      // Do required initialization
		//System.out.println("Nitish - GetMail");
	  }

	  public void doGet(HttpServletRequest request,
	                    HttpServletResponse response)
	            throws ServletException, IOException
	  {
		  String sDate = request.getParameter("txtStartDate");
		  String eDate = request.getParameter("txtEndDate");
		  String query = "after:"+sDate+" before:"+eDate;
		  HttpSession session = request.getSession(true);
		  String accessToken = (String)session.getAttribute("credential");
		  if(accessToken==null)
			  response.sendRedirect("index.jsp");
		  GoogleAuthHelper helper = new GoogleAuthHelper();
		  List<EmailCount> result = helper.getMessages(query, accessToken);
		  //System.out.println("Result: "+result);
		  RequestDispatcher rd = request.getRequestDispatcher("mail.jsp");
		  request.setAttribute("result", result);
		  rd.forward(request, response);
	  }
}

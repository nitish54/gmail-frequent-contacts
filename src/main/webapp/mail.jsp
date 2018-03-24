<%@page import="com.sinha.google.auth.GoogleAuthHelper"%>
<%@page import="java.util.List"%>
<%@page import="com.sinha.google.bean.EmailCount"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Top Mails</title>
<script type="text/javascript">
function isValid(val) {
	  var IsoDateRe = new RegExp("^([0-9]{4})/([0-9]{2})/([0-9]{2})$");

	  var matches = IsoDateRe.exec(val);
	  if (!matches) return false;
	  //alert(matches[1]+"   "+matches[2] +"  "+ matches[3]);
	  var composedDate = new Date(matches[1], (matches[2]-1) , matches[3]);
	  //alert("CDate: "+composedDate);
	  return ((composedDate.getMonth() == (matches[2]-1)) &&
	          (composedDate.getDate() == matches[3]) &&
	          (composedDate.getFullYear() == matches[1]));
	}
function getStartDate(val){
	if(isValid(val))
	{
		var date = new Date(val);
		//alert("Val Date: "+date);
		//alert(date.getMonth()+" Diff "+(date.getMonth()- 2));
		date.setMonth(date.getMonth()- 3);
		var sDate = new Date(date.getFullYear(),date.getMonth(),date.getDate());
		var printDate = sDate.getFullYear()+"/"+(sDate.getMonth()+1)+"/"+sDate.getDate();
		document.getElementById("txtStartDate").value = printDate;
		//alert(date);
	}
	else{
		document.getElementById("txtStartDate").value = "";
	}
}
function validate()
{
	var val = document.getElementById("txtEndDate").value;
	if(isValid(val)) return true;
	else {
		alert("Please select correct date or format");
		return false;
	}
}
</script>
<style type="text/css">
body {
	font-family: Sans-Serif;
	margin: 1em;
}

.revoke a {
	display: block;
	border-style: solid;
	border-color: #bbb #888 #666 #aaa;
	border-width: 1px 2px 2px 1px;
	background: #ccc;
	color: #333;
	line-height: 2;
	text-align: center;
	text-decoration: none;
	font-weight: 900;
	width: 13em;
}


.revoke a:active {
	border-color: #666 #aaa #bbb #888;
	border-width: 2px 1px 1px 2px;
	color: #000;
}
</style>
</head>
<body>
<%GoogleAuthHelper helper = new  GoogleAuthHelper();
String token = (String)session.getAttribute("credential");
String url = "Revoke?token="+token;
%>
<div align="center"><h2> Hi <%=session.getAttribute("name")%></h2></div><div class="revoke" align="right"><a href="<%=url %>">Revoke Access</a></div>
<div align="center">
<form method="get" action="getMail" onsubmit="return validate();">
<table>
<tr><td align="left">Your Email Address:</td>
<td><%=session.getAttribute("email")%></td></tr>
<tr><td></td></tr>
<tr><td align="left">Enter End Date:</td>
<td><input type="text" id="txtEndDate" name="txtEndDate" onkeyup="getStartDate(this.value)" onblur="getStartDate(this.value)"/> Format: YYYY/MM/DD</td></tr>
<tr><td></td></tr>
<tr><td align="left">Start Date: </td>
<td><input type="text" id="txtStartDate" name="txtStartDate" readonly="readonly"/> (readonly)</td></tr>
<tr><td></td></tr>
<tr><td colspan="2" align="center"><input type="submit" value="Click to get results"/></td></tr>
</table>
</form>
</div>
<div align="center">
<center><h3>Top three Conversed Emails</h3></center>
<table>
<tr><th>Email</th><th>Count</th></tr>
<%
	List<EmailCount> result = (List<EmailCount>) request.getAttribute("result") ;	
	if(result!=null)
	{
		for(EmailCount str: result){
			String email =str.getEmail();
			email=email.replaceAll("<","&lt;");
	        email=email.replaceAll(">","&gt;");
			out.println("<tr><td>"+email+"</td><td>"+str.getCount()+"</td></tr>");
		}		
	}
%>
</table>
</div>
</body>
</html>
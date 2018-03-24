Assumptions:

- Familiarity with OOP, Java, Maven, and JEE
- Java application server listening on localhost:8080

Prerequisites:

- Google API access credentials (Client ID, Client Secret). 
1. Set it up here https://code.google.com/apis/console/
	- Set up allowed Redirect URIs at Google API -> API Access. 

Usage:
Input: http://localhost:8080/OAuth2v1/index.jsp

1. Add Client ID, and Client Secret parameters to GoogleAuthHelper.java

2. Compile the project ($ mvn clean install)

3. Deploy war to application server

4. Browse to: http://localhost:8080/OAuth2v1/

5. Click "log in with google" on top of this page
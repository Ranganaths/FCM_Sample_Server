<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
	<style>
		table {
			border-collapse: collapse;
		}
		
		td {
			border : 1px solid #bcbcbc; 
		}
	</style>
</head>
<body>
<h1>
	Hello world!  
</h1>

<table>
	<tr><td>body</td><td>${body}</td></tr>
	<tr><td>response_code</td><td>${response_code}</td></tr>
	<tr><td>response_body</td><td>${response_body}</td></tr>
	<tr> </tr>
</table>
</body>
</html>

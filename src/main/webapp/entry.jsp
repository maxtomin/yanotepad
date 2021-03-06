<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.appspot.yanotepad.controller.Notepad" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
    <title>Yet Another Notepad</title>
    <link href="style.css" rel="stylesheet" type="text/css">
    <meta name="viewport" content="width=device-width" />
    <script>
        function goBack()
        {
          window.history.back()
        }
    </script>
  </head>
  <body>
<%
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user == null)
    {
%>
<a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Sign in</a>
<%
    }
    else
    {
        String documentId = request.getParameter("documentId");
        String content = documentId == null ? "" : new Notepad(user).getEntryDetails(documentId).getContent();

        pageContext.setAttribute("user", user);
        pageContext.setAttribute("content", content);
        pageContext.setAttribute("documentId", documentId);
%>
<p>Logged in as <b>${fn:escapeXml(user.nickname)}</b> - <a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">Logout</a></p>
<form action="apply" method="post">
    <div>
        <input type="hidden" name="documentId" value="${fn:escapeXml(documentId)}" />
        <input type="submit" name="action" value="Save"/>
        <input type="submit" name="action" value="Delete"/>
        <input type="button" value="Back" onclick="goBack()">
    </div>
    <div><textarea name="content" rows="3" cols="60">${fn:escapeXml(content)}</textarea></div>
</form>
<%
    }
%>
  </body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.appspot.yanotepad.controller.Notepad" %>
<%@ page import="com.appspot.yanotepad.model.Entry" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
    <title>Yet Another Notepad</title>
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
        List<Entry> entries = new Notepad(user).searchEntries("");
        pageContext.setAttribute("user", user);
        pageContext.setAttribute("entries", entries);
%>
Logged in as <b>${fn:escapeXml(user.nickname)}</b> - <a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">Logout</a>
<form action="entry.jsp">
    <input type="submit" value="Add">
</form>
<form>
    <input type="text" name="Query">
    <input type="submit" value="Search">
</form>
<c:forEach var="entry" items="${entries}">
    <div><b>${entry.header}</b>
        <form action="entry.jsp">
            <input type="hidden" name="documentId" value="${entry.documentId}">
            <input type="submit" value="Edit">
        </form>
    </div>
</c:forEach>
<%
    }
%>
  </body>
</html>
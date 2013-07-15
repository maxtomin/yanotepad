package com.appspot.yanotepad.servlet;

import com.appspot.yanotepad.controller.Notepad;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.repackaged.com.google.common.base.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Save extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        String content = request.getParameter("content");
        String documentId = request.getParameter("documentId");

        Notepad notepad = new Notepad(user);

        if (StringUtil.isEmptyOrWhitespace(documentId))
        {
            notepad.addEntry(content);
        }
        else
        {
            notepad.updateEntry(documentId, content);
        }

        response.sendRedirect("/index.jsp");
    }
}

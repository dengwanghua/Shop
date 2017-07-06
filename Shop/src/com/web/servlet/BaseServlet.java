package com.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.omg.CORBA.Request;

public class BaseServlet extends HttpServlet {
 @Override
 	public void service(HttpServletRequest req, HttpServletResponse resp)
 			throws ServletException, IOException {
	 		req.setCharacterEncoding("utf-8");
	 		try {
	 			String methodName=req.getParameter("method");
		 		Class clazzClass=this.getClass();
				Method method=clazzClass.getMethod(methodName, HttpServletRequest.class,HttpServletResponse.class);
				method.invoke(this, req,resp);
	 		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
 	}
}

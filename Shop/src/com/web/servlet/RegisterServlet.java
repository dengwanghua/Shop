package com.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import com.pojo.User;
import com.service.UserService;
import com.utils.CommonUtils;
import com.utils.MailUtils;

public class RegisterServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		Map<String, String[]> properties=request.getParameterMap();
		User user=new User();
		try {
			ConvertUtils.register(new Converter(){

				@Override
				public Object convert(Class clazz, Object value) {
					SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
					Date date=null;
					try {
						date=format.parse(value.toString());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return date;
				}
				
			}, Date.class);
			BeanUtils.populate(user, properties);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		user.setUid(CommonUtils.getUuid());
		user.setTelephone(null);
		user.setState(0);
		String activeCode=CommonUtils.getUuid();
		user.setCode(activeCode);
		
		UserService userServer=new UserService();
		Boolean registerResult=userServer.register(user);
		
		if(registerResult){
			String emailMsg = "恭喜您注册成功，请点击下面的连接进行激活账户"
					+ "<a href='http://localhost:8080/Shop/active?activeCode="+activeCode+"'>"
							+ "http://localhost:8080/Shop/active?activeCode="+activeCode+"</a>";
			try {
				MailUtils.sendMail(user.getEmail(), emailMsg);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			response.sendRedirect(request.getContextPath()+"/registerSuccess.jsp");
		}else{
			response.sendRedirect(request.getContextPath()+"/registerFail.jsp");
		}
		
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}

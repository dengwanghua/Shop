package com.web.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.pojo.Category;
import com.pojo.Order;
import com.service.AdminService;
import com.utils.BeanFactory;

public class AdminServlet extends BaseServlet {
	public void findAllCategory(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		AdminService adminservice=(AdminService) BeanFactory.getBean("adminService");;
		List<Category> categoryList=adminservice.CategoryList();
		Gson gson=new Gson();
		String json=gson.toJson(categoryList);
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(json);
	}

		public void findAllOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			//获得所有的订单信息----List<Order>
			
			AdminService service =(AdminService) BeanFactory.getBean("adminService");
			List<Order> orderList = service.findAllOrders();
			
			request.setAttribute("orderList", orderList);
			
			request.getRequestDispatcher("/admin/order/list.jsp").forward(request, response);
			
		}
		public void findOrderInfoByOid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String oid = request.getParameter("oid");
			System.out.println(oid);
			AdminService service =(AdminService) BeanFactory.getBean("adminService");;
			
			List<Map<String,Object>> mapList = service.findOrderInfoByOid(oid);
			
			Gson gson = new Gson();
			String json = gson.toJson(mapList);
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(json);
			
		}
		
}

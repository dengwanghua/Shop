package com.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.dao.AdminDao;
import com.pojo.Category;
import com.pojo.Order;
import com.pojo.Product;
import com.service.AdminService;

public class AdminServiceImpl implements AdminService {
	public List<Category> CategoryList(){
		AdminDao adminDao=new AdminDao();
		List<Category> categoryList=null;
		try {
			categoryList = adminDao.CategoryList();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return categoryList;
	}

	public void saveProduct(Product product) {
		// TODO Auto-generated method stub
		AdminDao adminDao=new AdminDao();
		try {
			adminDao.saveProduct(product);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<Order> findAllOrders() {
		AdminDao dao = new AdminDao();
		List<Order> ordersList = null;
		try {
			ordersList = dao.findAllOrders();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ordersList;
	}
	public List<Map<String, Object>> findOrderInfoByOid(String oid) {
		AdminDao dao = new AdminDao();
		List<Map<String, Object>> mapList = null;
		try {
			mapList = dao.findOrderInfoByOid(oid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mapList;
	}

}

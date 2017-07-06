package com.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.dao.AdminDao;
import com.pojo.Category;
import com.pojo.Order;
import com.pojo.Product;

public interface AdminService {
	public List<Category> CategoryList();

	public void saveProduct(Product product);
	
	public List<Order> findAllOrders();
	public List<Map<String, Object>> findOrderInfoByOid(String oid) ;
		
}

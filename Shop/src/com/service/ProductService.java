package com.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.dao.ProductDao;
import com.pojo.Order;
import com.pojo.PageBean;
import com.pojo.Product;
import com.pojo.User;
import com.utils.DataSourceUtils;

public class ProductService {

	public List<Product> hotProducts() {
		// TODO Auto-generated method stub
		ProductDao productDao=new ProductDao();
		List<Product> hotProducts=null;
		try {
			hotProducts=productDao.hotProducts();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hotProducts;
	}
	
	public List<Product> newProducts() {
		// TODO Auto-generated method stub
		ProductDao productDao=new ProductDao();
		List<Product> newProducts=null;
		try {
			newProducts=productDao.newProducts();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newProducts;
	}
	public int totalProudctCount(String cid) {
		ProductDao productDao=new ProductDao();
		int totalcount=0;
		try {
			totalcount = productDao.totalProudctCount(cid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return totalcount;
	}

	public PageBean pageProducts(String cid,int currentPage,int currentCount) {
		ProductDao productDao=new ProductDao();
		
		PageBean<Product> pageBean = new PageBean<Product>();
		
		pageBean.setCurrentPage(currentPage);
		pageBean.setCurrentCount(currentCount);
		int totalCount=0;
		try {
			totalCount=productDao.totalProudctCount(cid);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		pageBean.setTotalCount(totalCount);
		
		int totalPage=(int) Math.ceil(totalCount*1.0/currentCount);
		pageBean.setTotalPage(totalPage);
		
		int index=(currentPage-1)*currentCount;
		List<Product> list=null;
		try {
			list=productDao.pageProducts(cid, index, currentCount);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pageBean.setList(list);
		return pageBean;
		
		
	}

	public Product selectProductById(String pid) {
		ProductDao productDao=new ProductDao();
		Product product=null;
		try {
			product = productDao.selectProductById(pid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return product;
	}

	public void submitOrder(Order order) {
		// TODO Auto-generated method stub
		ProductDao dao = new ProductDao();
		try {
			DataSourceUtils.startTransaction();
			dao.addOrder(order);
			dao.addOrderItem(order);
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			try {
				DataSourceUtils.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}finally{
			try {
				DataSourceUtils.commitAndRelease();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		
	}

	public void updateOrderAdrr(Order order) {
		ProductDao dao = new ProductDao();
		try {
			dao.updateOrderAdrr(order);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public void updateOrderState(String r6_Order) {
		ProductDao dao = new ProductDao();
		try {
			dao.updateOrderState(r6_Order);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Order> findOrdersByUser(String uid) {
		ProductDao productDao=new ProductDao();
		List<Order> orders=null;
		try {
			orders = productDao.findOrdersByUser(uid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return orders;
	}

	public List<Map<String, Object>> findOrderItemsByOrder(String oid) {
		ProductDao productDao=new ProductDao();
		List<Map<String, Object>> mapList=null;
		try {
			mapList = productDao.findOrderItemsByOrder(oid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mapList;
	}
}

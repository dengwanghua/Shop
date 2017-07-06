package com.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.pojo.Order;
import com.pojo.OrderItem;
import com.pojo.PageBean;
import com.pojo.Product;
import com.utils.DataSourceUtils;

public class ProductDao {
	public List<Product> hotProducts() throws SQLException{
		QueryRunner runner=new QueryRunner(DataSourceUtils.getDataSource());
		String sqlString="select * from product where is_hot=? limit ?,?";
		List<Product> hotproducts=runner.query(sqlString, new BeanListHandler<Product>(Product.class), 1,0,9);
		return hotproducts;
	}
	
	public List<Product> newProducts() throws SQLException{
		QueryRunner runner=new QueryRunner(DataSourceUtils.getDataSource());
		String sqlString="select * from product order by pdate desc limit ?,?";
		List<Product> newproducts=runner.query(sqlString, new BeanListHandler<Product>(Product.class),0,9);
		return newproducts;
	}
	
	public int totalProudctCount(String cid) throws SQLException {
		// TODO Auto-generated method stub
		QueryRunner runner=new QueryRunner(DataSourceUtils.getDataSource());
		String sqlString="select count(*) from product where cid=?";
		Long totalCount=(Long) runner.query(sqlString, new ScalarHandler(),cid);
		return totalCount.intValue();
	}
	
	public List<Product> pageProducts(String cid,int index,int currentCount) throws SQLException {
		// TODO Auto-generated method stub
		QueryRunner runner=new QueryRunner(DataSourceUtils.getDataSource());
		String sqlString="select * from product where cid=? limit ?,?";
		List<Product> list=runner.query(sqlString, new BeanListHandler<Product>(Product.class),cid,index,currentCount);
		return list;
	}

	public Product selectProductById(String pid) throws SQLException {
		QueryRunner runner=new QueryRunner(DataSourceUtils.getDataSource());
		String sqlString="select * from product where pid=?";
		Product product=runner.query(sqlString, new BeanHandler<Product>(Product.class),pid);
		return product;
	}

	public void addOrder(Order order) throws SQLException {
		QueryRunner runner = new QueryRunner();
		String sql = "insert into orders values(?,?,?,?,?,?,?,?)";
		Connection conn = DataSourceUtils.getConnection();
		runner.update(conn,sql, order.getOid(),order.getOrdertime(),order.getTotal(),order.getState(),order.getAddress(),order.getName(),order.getTelephone(),order.getUser().getUid());
	}

	public void addOrderItem(Order order) throws SQLException{
		// TODO Auto-generated method stub
		QueryRunner runner = new QueryRunner();
		String sql = "insert into orderitem values(?,?,?,?,?)";
		Connection conn = DataSourceUtils.getConnection();
		List<OrderItem> orderItems = order.getOrderItems();
		for(OrderItem item : orderItems){
			runner.update(conn,sql,item.getItemid(),item.getCount(),item.getSubtotal(),item.getProduct().getPid(),item.getOrder().getOid());
		}
	}

	public void updateOrderAdrr(Order order) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "update orders set address=?,name=?,telephone=? where oid=?";
		runner.update(sql, order.getAddress(),order.getName(),order.getTelephone(),order.getOid());
		
	}
	public void updateOrderState(String r6_Order) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "update orders set state=? where oid=?";
		runner.update(sql, 1,r6_Order);
	}

	public List<Order> findOrdersByUser(String uid) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select * from orders where uid=?";
		List<Order> orders=(List<Order>) runner.query(sql,new BeanListHandler<Order>(Order.class), uid);
		return orders;
	}

	public List<Map<String, Object>> findOrderItemsByOrder(String oid) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select p.pimage,p.pname,p.shop_price,o.count,o.subtotal from orderitem o,product p where o.pid=p.pid and o.oid=?";
		List<Map<String, Object>> mapList=runner.query(sql,new MapListHandler(), oid);
		return mapList;
		
	}
}

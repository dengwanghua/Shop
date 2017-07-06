package com.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.pojo.Category;
import com.pojo.Product;
import com.utils.DataSourceUtils;

public class CategoryDao {
	public List<Category> CategoryList() throws SQLException{
		QueryRunner runner=new QueryRunner(DataSourceUtils.getDataSource());
		String sqlString="select * from category";
		List<Category> categoryList=runner.query(sqlString, new BeanListHandler<Category>(Category.class));
		return categoryList;
	}
}

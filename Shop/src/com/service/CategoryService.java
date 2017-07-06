package com.service;

import java.sql.SQLException;
import java.util.List;

import com.dao.CategoryDao;
import com.dao.ProductDao;
import com.pojo.Category;

public class CategoryService {
	public List<Category> CategoryList() {
		// TODO Auto-generated method stub
		CategoryDao categoryDao=new CategoryDao();
		List<Category> categoryList=null;
		try {
			categoryList=categoryDao.CategoryList();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return categoryList;
	}
}

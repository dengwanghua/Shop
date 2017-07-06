package com.service;

import java.sql.SQLException;

import com.dao.UserDao;
import com.pojo.User;

public class UserService {

	public Boolean register(User user){
		
		UserDao userDao=new UserDao(); 
		int row=0;
		try {
			row = userDao.register(user);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return row>0?true:false;
	}

	public void active(String activeCode) {
		UserDao userDao=new UserDao(); 
		try {
			userDao.active(activeCode);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public Boolean checkname(String username) {
		
		UserDao userDao=new UserDao();
		Long  count=(long) 0;
		try {
			count=userDao.checkname(username);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count>0?true:false;
		// TODO Auto-generated method stub
	}
	public User login(String username, String password) throws SQLException {
		UserDao dao = new UserDao();
		return dao.login(username,password);
	}
}

package com.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.pojo.User;
import com.utils.DataSourceUtils;

public class UserDao {
	public int register(User user) throws SQLException{
		QueryRunner runner=new QueryRunner(DataSourceUtils.getDataSource());
		String sqlString="insert into user values(?,?,?,?,?,?,?,?,?,?)";
		int row=runner.update(sqlString,user.getUid(),user.getUsername(),user.getPassword(),
				user.getName(),user.getEmail(),user.getTelephone(),user.getBirthday(),
				user.getSex(),user.getState(),user.getCode());
		return row;
	}

	public void active(String activeCode) throws SQLException {
		QueryRunner runner=new QueryRunner(DataSourceUtils.getDataSource());
		String sqlString="update user set state=? where code=?";
		runner.update(sqlString, 1,activeCode);
		
	}

	public Long checkname(String username) throws SQLException {
		// TODO Auto-generated method stub
		QueryRunner runner=new QueryRunner(DataSourceUtils.getDataSource());
		String sqlString="select count(*) from user where username=?";
		Long count=(Long) runner.query(sqlString, new ScalarHandler(), username);
		return count;
	}
	public User login(String username, String password) throws SQLException {
		QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select * from user where username=? and password=?";
		return runner.query(sql, new BeanHandler<User>(User.class), username,password);
	}
}

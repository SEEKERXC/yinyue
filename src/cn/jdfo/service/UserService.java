package cn.jdfo.service;

import cn.jdfo.domain.LoginLog;
import cn.jdfo.domain.User;
import cn.jdfo.tool.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.jdfo.dao.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Service //将这个类标注为服务层的Bean
public class UserService {

	private final MyDao myDao;

	@Autowired
	public UserService(MyDao myDao) {
		this.myDao = myDao;
	}

	public boolean hasMatchUser(String email,String password){
		password= MD5.getMD5(password);
		return myDao.hasObject(Arrays.asList("email","password"), Arrays.asList(email,password),User.class);
	}
	
	public User findUserByEmail(String email){
		List<User> u = myDao.get(User.class,Collections.singletonList("email"),Collections.singletonList(email));
		return u!=null&&u.size()>0?u.get(0):null;
	}
	
	public LoginLog loginSuccess(User user){
		LoginLog loginLog=new LoginLog();
		loginLog.setUserId(user.getId());
		loginLog.setIp(user.getLastIp());
		loginLog.setLoginTime(user.getLastVisit());
		myDao.update(user);
		loginLog.setId(myDao.save(loginLog));
		return loginLog;
	}
	
	public boolean register(User user){
		return myDao.save(user)>0;
	}

	public void updateLogInfo(LoginLog loginLog){
		myDao.update(loginLog);
	}
	
	
}

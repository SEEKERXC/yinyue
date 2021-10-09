package cn.jdfo.web;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import cn.jdfo.domain.User;
import cn.jdfo.tool.MD5;
import cn.jdfo.tool.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.jdfo.service.UserService;

import javax.servlet.http.HttpServletRequest;

@Controller 
public class LoginController{
	
	private final UserService userService;

	@Autowired
	public LoginController(UserService userService) {
		this.userService = userService;
	}

	@RequestMapping(value="/login")
	@JsonView(View.SimpleUser.class)
	public ResponseEntity<Object> login(LoginCommand command, HttpServletRequest request) {
	    if(request.getSession().getAttribute("user")!=null)return ResponseEntity.status(400).body(new Message(400,"您已经登录了"));
		boolean isValidUser = userService.hasMatchUser(command.getemail(),command.getPassword());
		if(!isValidUser){
			return ResponseEntity.status(HttpStatus.OK).body(new Message(200,"用户名或密码错误"));
		}else{
			User user= userService.findUserByEmail(command.getemail());
			user.setLastIp(request.getRemoteAddr());
			user.setLastVisit(new Timestamp(new Date().getTime()));
			request.getSession().setAttribute("logInfo",userService.loginSuccess(user));
			request.getSession().setAttribute("user", user);
			request.getSession().setMaxInactiveInterval(600);
			return ResponseEntity.ok(user);
		}
	}
	
	@RequestMapping(value="/getuser")
	@JsonView(View.SimpleUser.class)
	public ResponseEntity<Object> getUser(HttpServletRequest request){
		if(request.getSession().getAttribute("user")!=null){
			return ResponseEntity.ok(request.getSession().getAttribute("user"));
		}else return ResponseEntity.status(HttpStatus.OK).body(new Message(200,"您还没有登录"));
	}
	
	@RequestMapping(value="/register")
	@ResponseBody
	public boolean register(String name, String email, String password, HttpServletRequest request) {
		User user=new User();
		user.setEmail(email);
		user.setPassword(MD5.getMD5(password));
		user.setName(name);
		boolean b=userService.register(user);
		if(b){//注册成功，进行登录
			user=userService.findUserByEmail(user.getEmail());
			user.setLastIp(request.getRemoteAddr());
			user.setLastVisit(new Timestamp(new Date().getTime()));
			request.getSession().setAttribute("logInfo",userService.loginSuccess(user));
			request.getSession().setAttribute("user", user);
			request.getSession().setMaxInactiveInterval(600);
			userService.loginSuccess(user);
		}
		return b;
	}
	
	@RequestMapping(value="/signout")
	@ResponseBody
	public boolean signOut(HttpServletRequest request){
		request.getSession().invalidate();
		return true;
	}
	
	@RequestMapping(value="/hasUser")
	@ResponseBody
	public boolean hasUser(String email){
		return userService.findUserByEmail(email) != null;
	}

	@RequestMapping(value="/nothing")
	@ResponseBody
	public boolean doNothing(){
		return true;
	}//空请求
}

package cn.jdfo.listener;

import cn.jdfo.domain.LoginLog;
import cn.jdfo.service.UserService;
import cn.jdfo.tool.BeanGetter;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.sql.Timestamp;
import java.util.Date;

public class LogListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent httpSessionEvent) {

	}

	@Override
	public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
		BeanGetter beanGetter=BeanGetter.getBeanGetter();
		UserService userService=beanGetter.getBean("userService",UserService.class);
		HttpSession session=httpSessionEvent.getSession();
		LoginLog loginLog=(LoginLog)session.getAttribute("logInfo");
		loginLog.setLogoutTime(new Timestamp(new Date().getTime()));
		userService.updateLogInfo(loginLog);//插入注销时间
	}
}

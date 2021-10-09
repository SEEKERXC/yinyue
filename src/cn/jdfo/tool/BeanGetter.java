package cn.jdfo.tool;

import cn.jdfo.spider.SongSpider;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;

public class BeanGetter implements ApplicationContextAware{

	private ApplicationContext applicationContext;
	private static BeanGetter beanGetter;
	private BeanGetter(){}

	public static BeanGetter getBeanGetter() {
		return beanGetter;
	}

	public void setBeanGetter(BeanGetter beanGetter) {
		BeanGetter.beanGetter = beanGetter;
	}

	public void getSpiders(List<SongSpider> spiders){
		spiders.add(applicationContext.getBean("kuwo", SongSpider.class));
		spiders.add(applicationContext.getBean("qq", SongSpider.class));
		spiders.add(applicationContext.getBean("kugou", SongSpider.class));
		spiders.add(applicationContext.getBean("netease", SongSpider.class));
//		spiders.add(applicationContext.getBean("xiami", SongSpider.class));//虾米的有些奇怪，有时候需要登录
	}

	public Object getBean(String beanName){
		return applicationContext.getBean(beanName);
	}

	public <T> T getBean(String beanName, Class<T> tClass){
		return applicationContext.getBean(beanName,tClass);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;
	}


}

package com.cvnavi.scheduler.web;

import com.cvnavi.scheduler.proxy.dao.ProxyDaoService;
import com.cvnavi.scheduler.task.AbstractTask;
import com.cvnavi.scheduler.task.WebBackgroundTaskScheduler;
import org.apache.http.HttpHost;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;


@RestController
public class IndexController {
	private static final long serialVersionUID = 1L;

	@RequestMapping("/")
	protected ModelAndView proxy(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Collection<HttpHost> proxies = ProxyDaoService.getInstance().loadAliveProxy();
		return new ModelAndView("proxy","proxies",proxies);
	}

	@RequestMapping("/task")
	protected ModelAndView task()  {
		return new ModelAndView("task","tasks", WebBackgroundTaskScheduler.tasks);
	}

	@RequestMapping("/exeTask")
	protected Object exeTask(String name)  {
		AbstractTask task=WebBackgroundTaskScheduler.getTaskByName(name);
		if(task!=null){
			new Thread(task).start();
		}
		HashMap<String,String> map=new HashMap<>();
		map.put("success","true");
		map.put("message","操作成功");
		return map;
	}
}

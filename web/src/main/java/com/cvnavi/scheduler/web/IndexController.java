package com.cvnavi.scheduler.web;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cvnavi.scheduler.proxy.dao.ProxyDaoService;
import org.apache.http.HttpHost;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class IndexController {
	private static final long serialVersionUID = 1L;

	@RequestMapping("/")
	protected Object doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Collection<HttpHost> all = ProxyDaoService.getInstance().loadAliveProxy();
		StringBuilder sb=new StringBuilder();
		sb.append("<!Doctype html><html><head><meta http-equiv=Content-Type content=\"text/html;charset=utf-8\"></head><body>");
		sb.append("total count:"+all.size()).append("<br/>");
		for(HttpHost host:all) {
			sb.append(host.toHostString()).append("<br/>");
		}
		sb.append("</body></html>");
		return sb.toString();
	}
}

package com.cvnavi.scheduler.proxy;

import com.cvnavi.scheduler.browser.BrowserServiceInvoker;
import com.cvnavi.scheduler.proxy.html.ProxyExtracter;
import com.cvnavi.scheduler.task.ScheduleAnnotation;
import com.cvnavi.scheduler.util.ResourceReader;
import org.apache.http.HttpHost;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.regex.Matcher;

/**
 * 通过jxBrowser浏览器抓取代理ip。
 * 
 * @author lixy
 *
 */

@ScheduleAnnotation(begin = "07:30:00",end = "23:50:10",period = 483000)
public class BrowserCrawler extends AbstractProxyCrawler {

	static String[] urls = ResourceReader.readLines("/proxy_sites2.txt").toArray(new String[0]);

	@Override
	public String[] getCrawlUrl() {
		return urls;
	}


	@Override
	public void interruptTask() {
		Socket socket=BrowserServiceInvoker.getSocket();
		if(socket!=null){
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}

	public String getUrlContent(String url) {
		HttpHost proxy= ProxyProvider.getRandomProxy();
		String content=BrowserServiceInvoker.visitePage(url,"get",proxy);
		if(content.contains("ERR_CONNECTION_RESET")||content.contains("您所请求的网址（URL）无法获取")){
			content=BrowserServiceInvoker.visitePage(url,"get",null);
		}
		return content;
	}

	@Override
	public HashSet<HttpHost> doCrawl(String url) {
		if(url.contains("zdaye.com")){
			return doZdayeCrawl(url);
		}
		return super.doCrawl(url);
	}

	public HashSet<HttpHost> doZdayeCrawl(String url) {
		HashSet<HttpHost> set = new HashSet<>();

		String s = getUrlContent(url);
		HashSet<String> ip = ProxyExtracter.extractIP(s);
		for(String i:ip){
			set.add(new  HttpHost(i,80));
			set.add(new  HttpHost(i,8080));
			set.add(new  HttpHost(i,3128));
			set.add(new  HttpHost(i,1080));
			set.add(new  HttpHost(i,9999));
			set.add(new  HttpHost(i,4216));
			set.add(new  HttpHost(i,8118));
		}
		return set;
	}
}

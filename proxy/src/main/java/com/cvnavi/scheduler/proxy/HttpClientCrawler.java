package com.cvnavi.scheduler.proxy;

import com.cvnavi.scheduler.proxy.html.ProxyExtracter;
import com.cvnavi.scheduler.task.ScheduleAnnotation;
import com.cvnavi.scheduler.util.Header;
import com.cvnavi.scheduler.util.HttpUtil;
import com.cvnavi.scheduler.util.ResourceReader;
import com.cvnavi.scheduler.util.OcrUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Level;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通过HttpClient抓取代理ip。
 * 
 * @author lixy
 *
 */

@ScheduleAnnotation(begin = "07:30:30",end="23:50:00",period = 183000)
@Log4j2
public class HttpClientCrawler extends AbstractProxyCrawler {

	static String[] urls = ResourceReader.readLines("/proxy_sites.txt").toArray(new String[0]);

	@Override
	public String[] getCrawlUrl() {
		return urls;
	}


	@Override
	public void interruptTask() {

	}

	public String getUrlContent(String url) {
		HashMap<String, String> header = Header.createRandom();
		header.put("Upgrade-Insecure-Requests", "1");

		String s = HttpUtil.doHttpGet(url, header,null, ProxyProvider.getRandomProxy(), Level.DEBUG);
		if (s.length() == 0 || s.contains("The plain HTTP request was sent to HTTPS port")) {
			s = HttpUtil.doHttpGet(url, header, null,null, Level.DEBUG);
		}
		if(url.contains("proxy.mimvp.com")){
			s=ocrForMimvp(s);
		}
		return s;
	}

	static Pattern p1 = Pattern.compile(ProxyExtracter.IP_PATTERN);

	public String ocrForMimvp(String s){
	    String result="";
		Matcher m = p1.matcher(s);
		while (m.find()) {
			String ip=m.group();
            int index=s.indexOf("<img",m.end())+9;
            String img=s.substring(index,s.indexOf(" />",m.end()));
            String url="https://proxy.mimvp.com/"+img;
            try {
                BufferedImage bi= ImageIO.read(new URL(url));
                String port= OcrUtil.doOcr(bi);
                result+=ip+":"+port+"\n";
            } catch (IOException e) {
            }
		}
		return result;
	}


	static CloseableHttpClient httpclient = null;
	static {
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(200);
		connectionManager.setDefaultMaxPerRoute(20);
		httpclient = HttpClients.custom().setConnectionManager(connectionManager).build();
	}

}

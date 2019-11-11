package com.cvnavi.schduler.proxy;

import com.cvnavi.schduler.task.ScheduleAnnotation;
import com.cvnavi.schduler.util.Header;
import com.cvnavi.schduler.util.HttpUtil;
import com.cvnavi.schduler.util.ResourceReader;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * 通过HttpClient抓取代理ip。
 * 
 * @author lixy
 *
 */

@ScheduleAnnotation(begin = "07:30:30",end="22:00:00",period = 183000)
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
		String s = sendHttp(new HttpGet(url), header, ProxyProvider.getRandomProxy(), Level.DEBUG);
		if (s.length() == 0) {
			s = sendHttp(new HttpGet(url), header, null, Level.DEBUG);
		}
		return s;
	}

	static CloseableHttpClient httpclient = null;
	static {
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(200);
		connectionManager.setDefaultMaxPerRoute(20);
		httpclient = HttpClients.custom().setConnectionManager(connectionManager).build();
	}

	protected synchronized String sendHttp(HttpRequestBase requestMethod, HashMap<String, String> header,
			HttpHost proxy, Level level) {

		String result = "";

		if (header != null && header.size() > 0) {
			for (Entry<String, String> entry : header.entrySet()) {
				requestMethod.setHeader(entry.getKey(), entry.getValue());
			}
		}

		HttpClientContext context = HttpClientContext.create();
		RequestConfig.Builder configBuilder = RequestConfig.custom();
		configBuilder.setConnectTimeout(2000);
		configBuilder.setSocketTimeout(2000);
		configBuilder.setProxy(proxy);
		context.setRequestConfig(configBuilder.build());

		HttpUtil.log(requestMethod, context, level);

		CloseableHttpResponse response1 = null;
		try {
			response1 = httpclient.execute(requestMethod, context);
			HttpEntity entity1 = response1.getEntity();
			result = EntityUtils.toString(entity1, "UTF-8");
			EntityUtils.consume(entity1);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		} finally {
			if (response1 != null) {
				try {
					response1.close();
				} catch (IOException e) {
					log.error(e.getMessage(),e);
				}
			}
		}
		return result;
	}

}

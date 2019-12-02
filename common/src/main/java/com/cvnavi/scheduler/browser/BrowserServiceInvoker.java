package com.cvnavi.scheduler.browser;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.cvnavi.scheduler.util.CmdExecutor;
import com.cvnavi.scheduler.util.JavaExecutor;
import com.cvnavi.scheduler.util.ProcessUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpHost;

import org.codehaus.jackson.map.ObjectMapper;

@Log4j2
public class BrowserServiceInvoker  /*extends AbstractDailyTask */implements AutoCloseable {

	static boolean browserServiceRunning = false;
	static Socket socket = null;

	static void testOrStartServer() {
		browserServiceRunning=false;
		String pid=findBrowserServicePID();
		if(pid==null){
			startBrowserService();
		}
		Socket socket = null;
		for (int i = 0; i < 200; i++) {
			try{
				Thread.sleep(100);
				socket = new Socket("127.0.0.1", 55536);
				browserServiceRunning = true;
				socket.close();
				break;
			}catch(Exception ex){}
		}
		if(!browserServiceRunning){
			killBrowserService();
		}
	}

	static synchronized String sendCmd(String cmd) {
		if (!browserServiceRunning) {
			testOrStartServer();
		}

		try {
			socket = new Socket("127.0.0.1", BrowserService.port);
			socket.setSoTimeout(60 * 1000);
			socket.getOutputStream().write(cmd.getBytes());
			InputStream is = socket.getInputStream();
			byte[] b = new byte[0];
			byte[] buf = new byte[10240];
			int len = 0;
			while ((len = is.read(buf)) != -1) {
				byte[] temp = new byte[b.length + len];
				System.arraycopy(b, 0, temp, 0, b.length);
				System.arraycopy(buf, 0, temp, b.length, len);
				b = temp;
			}
			String result = new String(b,"UTF-8").trim();
			// System.out.println(result);

			return result;
		} catch (Exception e) {
			log.error(e);
			browserServiceRunning = false;
		} finally {
			try {
				if (socket != null) {
					socket.getInputStream().close();
					socket.getOutputStream().close();
					socket.close();
				}
			} catch (Exception e) {
			}
		}
		return "";
	}


	public static String visitePage(String url, String method,HttpHost proxy) {
		return visitePage(url,method,proxy,null,20000,null);
	}

	public static String visitePage(String url, String method,HttpHost proxy,HashMap<String,Object> params) {
		return visitePage(url,method,proxy,params,20000,null);
	}

	public static String visitePage(String url, String method,HttpHost proxy,HashMap<String,Object> params,int timeout,Class<?> listener) {
		HashMap<String,Object> map=new HashMap<>();
		map.put("action",BrowserService.ACTION_VISITE_PAGE);
		map.put("url",url);
		map.put("method",method);
		map.put("proxy",proxy!=null?proxy.toHostString():null);
		map.put("params",params);
		map.put("timeout",timeout);
		map.put("listener",listener!=null?listener.getName():null);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json=mapper.writeValueAsString(map);
			return sendCmd(json);
		} catch (IOException e) {
			log.error(e);
		}
		return "";
	}

	@Override
	public void close() throws Exception {
		stopBrowserService();
	}

	/**
	 * 开启浏览器服务
	 */
	public static synchronized void startBrowserService(){
		log.info("start browser service");
		String property = "SCHEDULER_HOME=" + System.getProperty("SCHEDULER_HOME")+" -Xms256m -Xmx512m ";
		JavaExecutor.runMainClass(BrowserStartup.class.getName(), property,true);
	}
	
	/**
	 * 停止浏览器服务 
	 */
	public static synchronized void stopBrowserService() {
		log.info("stop browser service");

		Socket socket = null;

		HashMap<String,Object> map=new HashMap<>();
		map.put("action",BrowserService.ACTION_EXIT);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json=mapper.writeValueAsString(map);
			socket = new Socket("127.0.0.1", BrowserService.port);
			socket.getOutputStream().write(json.getBytes());
		} catch (IOException e) {
		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * 通过杀进程的方式关闭浏览器。
	 */
	public static synchronized void killBrowserService() {

		log.info("kill browser service");
		String pid = findBrowserServicePID();
		if (pid != null) {
			ProcessHandle.of(Long.parseLong(pid)).ifPresent(ProcessHandle::destroy);
		}
	}
	
	/**
	 * 查询浏览器的进程号。
	 */
	public static synchronized String findBrowserServicePID() {
		for(Map.Entry e:ProcessUtil.jps().entrySet()){
			if(e.getValue().equals(BrowserStartup.class.getName())){
				return e.getKey().toString();
			}
		}

		return null;
	}


	public static void main(String args[]) {
		String s = "";
		// s=getPage("http://ip.zdaye.com/?port=8080", null);
		// s = sendCmd("cmd=" + BrowserService.CMD_LOGIN_MARINE_CIRCLE);
//		s = loginMarinecircle();
		System.out.println(s);
	}

	public static Socket getSocket() {
		return socket;
	}
}

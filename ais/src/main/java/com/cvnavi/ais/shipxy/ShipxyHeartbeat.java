package com.cvnavi.ais.shipxy;

import com.cvnavi.ais.Config;
import com.cvnavi.ais.browser.LoginShipxyPageHandler;
import com.cvnavi.schduler.base.KeyValue;
import com.cvnavi.schduler.browser.BrowserServiceInvoker;
import com.cvnavi.schduler.proxy.ProxyProvider;
import com.cvnavi.schduler.task.AbstractDailyTask;
import com.cvnavi.schduler.task.Schedule;
import com.cvnavi.schduler.task.ScheduleAnnotation;
import com.cvnavi.schduler.util.Header;
import com.cvnavi.schduler.util.HttpUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

/**
 * 定时访问特定URL,保持scode不过期。
 * 
 * @author lixy
 */
@Log4j2
@ScheduleAnnotation(begin = "08:00:00",end = "00:00:59",period = 290000)
public class ShipxyHeartbeat extends AbstractDailyTask {
	
	public static final String SCODE="Scode";

	private static String scode = null;// scode="290753304";
	private static String sessionId = null;
	private static String userAuth = null;
	
	@Override
	protected void scheduleBeginEvent(Schedule s) {
		sessionId = null;
		userAuth = null;
		scode = null;
	}

	public static synchronized String getScode() {
		if (scode != null && scode.length() > 0) {
			return scode;
		}

		loginShipxy();
		if (scode == null || scode.length() == 0) {// 尝试两次
			loginShipxy();
		}
		log.info(ShipxyHeartbeat.SCODE+"=" + scode);
		return scode;
	}

	private static void loginShipxy() {
		String url="http://www.shipxy.com/Home/Login";
		KeyValue<String> kv = Config.getRandomShipxyAccount();
		HashMap<String,Object>params=new HashMap<>();
		params.put("userName",kv.getKey());
		params.put("password",kv.getValue());
		params.put("autologin","false");
		params.put("authCode","");

		String s = BrowserServiceInvoker.visitePage(url, "post", ProxyProvider.getRandomProxy(),params,15000,LoginShipxyPageHandler.class);
		if (s.contains("SERVERID=")) {
			HashMap<String, String> map=new HashMap<>();
			for(String line:s.split("\n")){
				if(line!=null && line.contains("=")){
					map.put(line.split("=")[0], line.split("=")[1]);
				}
			}
			sessionId = map.get("SERVERID");
			userAuth = map.get(".UserAuth2");
			scode = map.get(SCODE);
		}
	}

	public static void refreshScode() {
		scode = null;
		getScode();
	}

	@Override
	public void doTask() {
		try {
			if (getScode() != null && getScode().length() > 0) {
				
				HashMap<String, String> cookie = new HashMap<>();
				cookie.put("SERVERID", sessionId);
				cookie.put(".UserAuth2", userAuth);
				
				Header header = Header.createRandom().referer("http://www.shipxy.com/");
				
//				String freshScodeUrl = "http://freedll.shipxy.com/dll/dp.dll?cmd=123&level=3&minx=24819335&minY=10701000&maxX=192866210&maxY=56678813&enc=1&scode="
//						+ scode;
				//未登录情况下，调用freedll.shipxy.com。登录情况下，要调用shipdll.shipxy.com。
				String freshScodeUrl = "http://shipdll.shipxy.com/dll/dp.dll?cmd=123&level=3&minx=24819335&minY=10701000&maxX=192866210&maxY=56678813&enc=1&scode="
						+ scode;
				
				HttpUtil.doHttpGet(freshScodeUrl, header, cookie, HttpUtil.RANDOM_PROXY, Level.DEBUG);

				freshScodeUrl = "http://m22.shipxy.com/dll/dp.dll?cmd=400&level=3&minx=24819335&minY=10701000&maxX=192866210&maxY=56678813&enc=1&mode=1&scode="
						+ scode;
				HttpUtil.doHttpGet(freshScodeUrl, header, cookie, HttpUtil.RANDOM_PROXY, Level.DEBUG);
			}
		} catch (Exception ex) {
			log.error(ex);
		}
	}

	public static String getSessionId(){
		return sessionId;
	}
	
	public static String getUserAuth(){
		return userAuth;
	}

	public static void main(String[] args) {
		getScode();
	}

}

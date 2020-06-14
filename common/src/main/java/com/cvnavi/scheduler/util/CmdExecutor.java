package com.cvnavi.scheduler.util;

import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 执行操作系统命令
 * 
 * @author lixy
 *
 */
public class CmdExecutor {
	static Logger log = LogManager.getLogger(CmdExecutor.class);

	public static String[] prepareCmd(String cmd, boolean requireDisplay) {

		String cmdArray[] = null;

		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("windows")) {
			cmdArray = new String[] { "cmd", "/c", cmd };
		} else if (os.toLowerCase().equals("linux")) {
			if(requireDisplay && GraphicsEnvironment.isHeadless()){
				if(!existCmd("xvfb-run")){
					log.warn("xvfb-run not found.");
				}
				cmdArray = new String[] { "/bin/sh", "-c",
						"xvfb-run --server-args=\"-screen 0 1024x768x24\" -a " + cmd };
			}else{
				cmdArray = new String[] { "/bin/sh", "-c", cmd };
			}
		}
		log.info(cmdArray[cmdArray.length - 1]);
		return cmdArray;
	}

	/**
	 * 执行操作系统命令
	 * 
	 * @param cmd
	 * @return
	 */
	public static String execCmd(String[] cmd) {
		Process p;
		StringBuilder sb = new StringBuilder();
		try {
			ProcessBuilder pb = new ProcessBuilder(cmd);
			pb.directory(new File(System.getProperty("user.home")));
			pb.redirectErrorStream(true);
			p = pb.start();
			p.waitFor(30, TimeUnit.SECONDS);
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			p.destroy();
			return sb.toString();
		} catch (Exception e) {
			log.error(e);
			return e.getMessage();
		}
	}

	public static boolean existCmd(String cmd){
		String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("linux")) {
			String str=execCmd(new String[]{"which",cmd}).trim();
			if(str.length()>0){
				return !Pattern.compile("no.*"+cmd+" in").matcher(str).find();
			}
		}
		return false;
	}

	public static void main(String[] args) {
		System.out.println(existCmd("ls"));
	}
}

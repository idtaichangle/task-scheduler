package com.cvnavi.scheduler.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 执行带main方法的java类。
 * 
 * @author lixy
 *
 */
public class JavaExecutor {
	static Logger log = LogManager.getLogger(JavaExecutor.class);

	/**
	 * 调用操作系统命令执行一个带main方法的java类。本方法不会等子进程结束。
	 * 
	 * @param mainClass
	 *            带main方法的类名。
	 */
	public static void runMainClass(String mainClass) {
		runMainClass(mainClass, null);
	}

	/**
	 * 调用操作系统命令执行一个带main方法的java类。本方法不会等子进程结束。
	 * 
	 * @param mainClass
	 *            带main方法的类名。
	 * @param systemProperty
	 *            启动java程序时的系统属性，用于执行java命令时放在参数里： -D<name>=<value>
	 */
	public static void runMainClass(String mainClass, String systemProperty) {
		runMainClass(mainClass, null,false);
	}

	/**
	 * 调用操作系统命令执行一个带main方法的java类。本方法不会等子进程结束。
	 * @param mainClass
	 * @param systemProperty
	 * @param requireDisplay 该java程序是否需要屏幕显示
	 */
	public static void runMainClass(String mainClass, String systemProperty,boolean requireDisplay) {
		String[] cmd = prepareJavaCmd(mainClass, systemProperty,requireDisplay);
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			log.error(e);
		}
	}

	/**
	 * 调用操作系统命令执行一个带main方法的java类并返回控制台输出字符。本方法将等子进程结束或等待超时。
	 * 
	 * @param mainClass
	 *            带main方法的类名。
	 */
	public static String runMainClassWithResult(String mainClass) {
		return runMainClassWithResult(mainClass,null);
	}

	/**
	 * 调用操作系统命令执行一个带main方法的java类并返回控制台输出字符。本方法将等子进程结束或等待超时。
	 * 
	 * @param mainClass
	 *            带main方法的类名。
	 * @param systemProperty
	 *            启动java程序时的系统属性，用于执行java命令时放在参数里： -D<name>=<value>
	 */
	public static String runMainClassWithResult(String mainClass, String systemProperty) {
		return runMainClassWithResult(mainClass,null,false);
	}

	/**
	 * 调用操作系统命令执行一个带main方法的java类并返回控制台输出字符。本方法将等子进程结束或等待超时。
	 * @param mainClass
	 * @param systemProperty
	 * @param requireDisplay 该java程序是否需要屏幕显示
	 * @return
	 */
	public static String runMainClassWithResult(String mainClass, String systemProperty,boolean requireDisplay) {
		String[] cmd = prepareJavaCmd(mainClass, systemProperty, requireDisplay);
		String output = CmdExecutor.execCmd(cmd);
		return output;
	}

	/**
	 * 根据当前操作系统，生成相应的系统命令
	 * 
	 * @param mainClass
	 * @param requireDisplay
	 * @return
	 */
	private static String[] prepareJavaCmd(String mainClass, String systemProperty, boolean requireDisplay) {
		if (systemProperty == null || systemProperty.length()==0) {
			systemProperty = "";
		} else {
			if (!systemProperty.startsWith("-D")) {
				systemProperty = "-D" + systemProperty;
			}
		}
		String classPath = currentClassPath();
		String str=systemProperty + "-Dfile.encoding=UTF-8 -cp " + classPath + " " + mainClass;

		String javaVersion=System.getProperty("java.version");
		String javaBinPath=System.getProperty("java.home")+File.separator+"bin"+File.separator+"java";
		if(javaBinPath.contains(" ")){
			javaBinPath="\""+javaBinPath+"\"";
		}
		String cmd=null;
		if(Pattern.compile("1\\.[6,7,8](.)*").matcher(javaVersion).matches()){
			cmd = javaBinPath + str;
		}else{
			String file=System.getProperty("java.io.tmpdir")+File.separator+"java.cp";
			Path p=Paths.get(file);

			try {
				if(Files.exists(p)){
					Files.delete(p);
				}

				Files.write(p,str.getBytes(), StandardOpenOption.CREATE_NEW);
			} catch (IOException e) {
				log.error(e);
			}
			cmd=javaBinPath+"  @"+file;
		}

		return CmdExecutor.prepareCmd(cmd,requireDisplay);
	}


	/**
	 * 取得包含className类的jar文件的绝对路径。
	 * 
	 * @return
	 */
	public static String getJar(String className) {
		Class<?> clazz = null;
		String jarPath = "";
		try {
			clazz = Class.forName(className);
			jarPath = findPathJar(clazz);
		} catch (ClassNotFoundException e) {
			log.error(e);
		}
		return jarPath;
	}

	private static String currentClassPath() {
		String result = "";
		ClassLoader cl = JavaExecutor.class.getClassLoader();
		if (cl instanceof URLClassLoader) {
			URLClassLoader ucl = (URLClassLoader) cl;
			for (URL u : ucl.getURLs()) {
				result += new java.io.File(u.getFile()) + File.pathSeparator;
				// File f=new File(u.getFile());
				// result=f+File.pathSeparator+f.getParent()+File.separator+"lib"+File.separator+"*"+File.pathSeparator;
				// break;
			}
			try {
				ucl.close();
			} catch (IOException e) {
			}
		}else{
			result=System.getProperty("java.class.path");
		}
		return result;
	}

	private static String findPathJar(Class<?> context) throws IllegalStateException {
		String rawName = context.getName();
		String classFileName;
		/*
		 * rawName is something like package.name.ContainingClass$ClassName. We
		 * need to turn this into ContainingClass$ClassName.class.
		 */ {
			int idx = rawName.lastIndexOf('.');
			classFileName = (idx == -1 ? rawName : rawName.substring(idx + 1)) + ".class";
		}

		String uri = context.getResource(classFileName).toString();
		if (uri.startsWith("file:"))
			throw new IllegalStateException("This class has been loaded from a directory and not from a jar file.");
		if (!uri.startsWith("jar:file:")) {
			int idx = uri.indexOf(':');
			String protocol = idx == -1 ? "(unknown)" : uri.substring(0, idx);
			throw new IllegalStateException("This class has been loaded remotely via the " + protocol
					+ " protocol. Only loading from a jar on the local file system is supported.");
		}

		int idx = uri.indexOf('!');
		// As far as I know, the if statement below can't ever trigger, so it's
		// more of a sanity check thing.
		if (idx == -1)
			throw new IllegalStateException(
					"You appear to have loaded this class from a local jar file, but I can't make sense of the URL!");

		try {
			String fileName = URLDecoder.decode(uri.substring("jar:file:".length(), idx),
					Charset.defaultCharset().name());
			return new File(fileName).getAbsolutePath();
		} catch (UnsupportedEncodingException e) {
			throw new InternalError("default charset doesn't exist. Your VM is borked.");
		}
	}
}
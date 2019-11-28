package com.cvnavi.scheduler.browser;

import java.io.File;
import java.net.URI;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
 
/**
 * 浏览器在新的jvm中运行，需要它自己的日志。
 * @author lixy
 *
 */
public class CustomConfigurationFactory extends ConfigurationFactory {
	static {
		setSchedulerHome();
	}

	static Configuration createConfiguration(final String name, ConfigurationBuilder<BuiltConfiguration> builder) {
		builder.setConfigurationName(name);
		builder.setStatusLevel(Level.ERROR);

		LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout").addAttribute("pattern",
				"%d{MM-dd HH:mm:ss} %-5level %logger{1} - %L - %msg%n");

		// create the console appender
		AppenderComponentBuilder consoleBuilder = builder.newAppender("Stdout", ConsoleAppender.PLUGIN_NAME)
				.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
		consoleBuilder.add(layoutBuilder);
		builder.add(consoleBuilder);

		// create the rolling file appender
		ComponentBuilder<?> triggeringPolicy = builder.newComponent("Policies")
				.addComponent(builder.newComponent("TimeBasedTriggeringPolicy"))
				.addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "100M"));

		AppenderComponentBuilder rollingFileBuilder = builder
				.newAppender("RollingFile", RollingFileAppender.PLUGIN_NAME)
				.addAttribute("fileName", "${sys:SCHEDULER_HOME}/logs/browser.log")
				.addAttribute("filePattern", "${sys:SCHEDULER_HOME}/logs/browser-%d{yyyy-MM-dd}.log.gz")
				.add(layoutBuilder).addComponent(triggeringPolicy);
		builder.add(rollingFileBuilder);

		builder.add(builder.newRootLogger(Level.INFO).add(builder.newAppenderRef("RollingFile")));

		return builder.build();
	}

	@Override
	public Configuration getConfiguration(final LoggerContext loggerContext, final ConfigurationSource source) {
		return getConfiguration(loggerContext, source.toString(), null);
	}

	@Override
	public Configuration getConfiguration(final LoggerContext loggerContext, final String name,
			final URI configLocation) {
		ConfigurationBuilder<BuiltConfiguration> builder = newConfigurationBuilder();
		return createConfiguration(name, builder);
	}

	@Override
	protected String[] getSupportedTypes() {
		return new String[] { "*" };
	}

	static void setSchedulerHome() {
		String SCHEDULER_HOME=null;
		if (System.getProperty("SCHEDULER_HOME") == null) {
			String file=CustomConfigurationFactory.class.getProtectionDomain().getCodeSource().getLocation().getFile();
			if(file.endsWith(".jar")){
				SCHEDULER_HOME=new File(file).getParentFile().getParent();
			}else if(file.endsWith("/classes/")){
				SCHEDULER_HOME=new File(file).getParent();
			}
			System.setProperty("SCHEDULER_HOME",SCHEDULER_HOME);
		}
	}
}
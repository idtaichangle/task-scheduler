package com.cvnavi.schduler.task;

import lombok.extern.log4j.Log4j2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * web后台任务调度。
 * 
 * @author lixy
 *
 */
@Log4j2
public class WebBackgroundTaskScheduler implements Runnable{

	static List<AbstractTask> tasks;
	public static long timerPeriod = 100;
//	protected Timer timer;
	protected long tomorrow;// 明天零时00:00:00。在这个时刻，需要重新计算每个任务的当天排班。
	protected static ScheduledExecutorService timer = (ScheduledExecutorService) Executors.newScheduledThreadPool(1);

	protected static ThreadPoolExecutor scheduler = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
	static {
		scheduler.setKeepAliveTime(10, TimeUnit.SECONDS);
		scheduler.allowCoreThreadTimeOut(true);
	}
	static WebBackgroundTaskScheduler instance;

	private WebBackgroundTaskScheduler(){

	}

	public static WebBackgroundTaskScheduler getInstance(){
		if(instance==null){
			instance=new WebBackgroundTaskScheduler();
		}
		return instance;
	}

	public void startScheduler() {
		prepareTask();
		calcTomorrow();
		timer.scheduleAtFixedRate(this,1000,timerPeriod,TimeUnit.MILLISECONDS);
	}

	public void stopScheduler(){
		for (AbstractTask task : tasks) {
			((AbstractDailyTask)task).setScheduleCancel(true);
			task.interruptTask();
		}
		scheduler.shutdownNow();
		timer.shutdownNow();
	}

	@Override
	public void run() {
		long time = System.currentTimeMillis();
		for (AbstractTask task : tasks) {
			if (task.timeToFire(time)) {
				scheduler.execute(task);
			}
		}
		
		if (time >= tomorrow) {
			log.info("new day");
			calcTomorrow();
			for (AbstractTask task : tasks) {
				new Thread(){
					@Override
					public void run() {
						task.newDayBegin();
					}
				}.start();
			}
		}
	}

	/**
	 * 计算明天零时00:00:00的unix时间戳。
	 */
	private void calcTomorrow() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 1);
		String time = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()) + " 00:00:00";
		try {
			tomorrow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time).getTime();
		} catch (ParseException e) {
		}
	}

	private void prepareTask() {
		tasks = new ArrayList<>();
		ServiceLoader<AbstractTask> loader=ServiceLoader.load(AbstractTask.class);
		Iterator<AbstractTask> it=loader.iterator();
		while(it.hasNext()){
			AbstractTask task=it.next();
			((AbstractDailyTask)task).initSchedules();
			task.newDayBegin();
			tasks.add(task);
		}
	}

	public static AbstractTask getTaskByName(String name) {
		for (AbstractTask task : tasks) {
			if (task.getClass().getName().equals(name)) {
				return task;
			}
		}
		return null;
	}
}

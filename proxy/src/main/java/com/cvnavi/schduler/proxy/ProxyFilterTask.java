package com.cvnavi.schduler.proxy;

import com.cvnavi.schduler.proxy.dao.ProxyDaoService;
import com.cvnavi.schduler.task.AbstractDailyTask;
import com.cvnavi.schduler.task.ScheduleAnnotation;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpHost;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 先前验证过的代理，过一段时间后也可能失效。本类定时验证代理是否还有效。无效的代理要从alive_proxy表删除。
 * 
 * @author lixy
 *
 */
@Log4j2
@ScheduleAnnotation(begin = "00:00:00",end="07:00:00",period = 3600000)
@ScheduleAnnotation(begin = "07:30:30",end="22:00:00",period = 183000)
public class ProxyFilterTask extends AbstractDailyTask {

	@Override
	public void doTask() {
		log.info("Start test alive proxy.");
		try {
			Collection<HttpHost> all = ProxyDaoService.getInstance().loadAliveProxy();
			Collection<HttpHost> tested = ProxyTester.testProxy(all);
			ArrayList<HttpHost> toBeRemove = new ArrayList<>(all);
			toBeRemove.removeAll(tested);
			ProxyDaoService.getInstance().deleteAliveProxy(toBeRemove);
			all.removeAll(toBeRemove);	
			log.info("Test complete. Remove " + toBeRemove.size() + " proxy." + all.size() + " proxy remain.");
		} catch (Exception ex) {
			log.error(ex);
		}
	}

	@Override
	public void interruptTask() {
		ProxyTester.interrupt();
	}

}

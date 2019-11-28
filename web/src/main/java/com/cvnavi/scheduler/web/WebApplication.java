package com.cvnavi.scheduler.web;

import com.cvnavi.scheduler.db.DbChecker;
import com.cvnavi.scheduler.proxy.dao.ProxyDaoService;
import com.cvnavi.scheduler.proxy.ProxyProvider;
import com.cvnavi.scheduler.task.WebBackgroundTaskScheduler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class WebApplication /*implements ServletContextListener*/{

    public static String SCHEDULER_HOME;
    static {
        setHome();
    }

    public static void setHome(){
        if(System.getProperty("SCHEDULER_HOME")!=null){
            SCHEDULER_HOME=System.getProperty("SCHEDULER_HOME");
        }else{
            String file=WebApplication.class.getProtectionDomain().getCodeSource().getLocation().getFile();
            if(file.endsWith(".jar")){
                SCHEDULER_HOME=new File(file).getParentFile().getParent();
            }else if(file.endsWith("/classes/")){
                SCHEDULER_HOME=new File(file).getParent();
            }
        }
        System.setProperty("SCHEDULER_HOME",SCHEDULER_HOME);
    }

    private static final Logger log = LogManager.getLogger(WebApplication.class);

    /**
     * servlet context是否有效(已经初始化，未被销毁)。
     */
    public static boolean contextValid = false;


    public static void main(String[] args)throws Exception {
        SpringApplication.run(WebApplication.class, args);
        DbChecker.checkDatabase();
        WebBackgroundTaskScheduler.getInstance().startScheduler();
        ProxyDaoService.getInstance().loadAliveProxy();
        ProxyProvider.register(ProxyDaoService.getInstance());
        contextValid = true;
        log.info("============spring started."+"============");
    }
}

package com.cvnavi.task;

import com.cvnavi.task.cm.SecTokenIntercept;
import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptInitializer;
import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptPipeline;
import com.github.monkeywie.proxyee.server.HttpProxyServer;
import com.github.monkeywie.proxyee.server.HttpProxyServerConfig;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ProxyServer {

    public static boolean PROXY_RUNNING=false;

    public static final int PROXY_PORT=8888;

    private static HttpProxyServer server;

    private static HttpProxyInterceptInitializer initializer=new HttpProxyInterceptInitializer(){
        @Override
        public void init(HttpProxyInterceptPipeline pipeline) {
            pipeline.addLast(new SecTokenIntercept());
        }
    };

    public static void startProxy(){
        log.info("start proxy server...");
        PROXY_RUNNING=true;
        HttpProxyServerConfig config =  new HttpProxyServerConfig();
        config.setHandleSsl(true);
        server=new  HttpProxyServer();
        server.serverConfig(config).proxyInterceptInitializer(initializer);
        server.start(PROXY_PORT);

    }

    public static void stopProxy(){
        PROXY_RUNNING=false;
        if(server!=null){
            server.close();
        }
    }

    public static void main(String[] args) {
        startProxy();
    }
}

package com.cvnavi.task.cm;

import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptPipeline;
import com.github.monkeywie.proxyee.intercept.common.FullResponseIntercept;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import lombok.extern.log4j.Log4j2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class SecTokenIntercept extends FullResponseIntercept {
    @Override
    public boolean match(HttpRequest httpRequest, HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
        return httpRequest.uri().startsWith("/datau/jsp/spgs/flow/flow_index.html");
    }

    @Override
    public void handelResponse(HttpRequest httpRequest, FullHttpResponse httpResponse,
                               HttpProxyInterceptPipeline pipeline) {
        String secToken=null;
        Pattern p= Pattern.compile("(?<=secToken=).{64}(?=&)");
        Matcher m= p.matcher(httpRequest.uri());
        if(m.find()){
            secToken=m.group(0);
            ChinaMobileTask.SEC_TOKENS.add(secToken);
            log.info(secToken);
        }
    }
}

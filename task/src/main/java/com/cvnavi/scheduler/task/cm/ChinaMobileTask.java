package com.cvnavi.scheduler.task.cm;


import com.cvnavi.scheduler.task.AbstractDailyTask;
import com.cvnavi.scheduler.task.ScheduleAnnotation;
import com.cvnavi.scheduler.util.HttpUtil;
import com.cvnavi.scheduler.util.ResourceReader;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@ScheduleAnnotation(begin = "06:30:00",end = "23:30:30",period = 14400000)
@Log4j2
public class ChinaMobileTask extends AbstractDailyTask {

    public static final String USER_AGENT="Mozilla/5.0 (Linux; Android 5.1.1; DUK-AL20 Build/LMY48Z) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/39.0.0.0 Safari/537.36 shydhn/2.3.0";
    static HashMap<String,String> header=new HashMap<>();
    static {
        header.put("User-Agent",USER_AGENT);
    }

    public static final Set<String> SEC_TOKENS=Collections.synchronizedSet(new HashSet<String>());;
    static {
        List<String> lines=ResourceReader.readLines("/cm-sec-token.properties");
        SEC_TOKENS.addAll(lines);
    }

    public static final String[] TASK_ID={"1115073","1115074","1118466","1115076","1116894","1115077","1118090","1118091","1118823","1115078","1115075"};

    @Override
    public void doTask() {
        if(ProxyServer.PROXY_RUNNING==false){
            new Thread(ProxyServer::startProxy).start();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
            }
        }
        ArrayList<String> toBeRemove=new ArrayList<>();

        for(String token:SEC_TOKENS){
            String sSOCode=getSsoCode(token);
            if(sSOCode!=null){
                for(String s:TASK_ID){
                    doRequest(sSOCode,s);
                    try {
                        Thread.sleep(new Random().nextInt(3000));
                    } catch (InterruptedException e) {
                    }
                }
            }else{
                toBeRemove.add(token);
            }
        }
        SEC_TOKENS.removeAll(toBeRemove);//移除无效的token
    }

    public static void doRequest(String loginSign,String taskId){
        String url="https://professorhe.sh.chinamobile.com/datau/datau/xrPneumaShangBao.du?eventId=1114647&channelId=hn&loginSign="+loginSign;
        HashMap<String,String> params=new HashMap<>();
        params.put("taskId", taskId);
        String result=HttpUtil.doHttpPost(url,params,header,null);
        log.info(result);
    }

    public static String getSsoCode(String token){
        String url="https://professorhe.sh.chinamobile.com/datau/datau/getSecCode.du";
        HashMap<String,String> params=new HashMap<>();
        params.put("secToken", token);
        String result=HttpUtil.doHttpPost(url,params,header,null);
        if(result.contains("数据正确")){//{"sSOCode":"6BDEB4EE36ED682A2C699189F6E6897E","code":"0000","msg":"数据正确","mobile":"15000337120"}
            log.info(result);
            return result.substring(12,44);
        }else{//{"code":"1004","msg":"解密失败"}
            log.error(result);
            return null;
        }
    }

}
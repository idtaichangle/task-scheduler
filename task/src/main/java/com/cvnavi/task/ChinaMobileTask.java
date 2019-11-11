package com.cvnavi.task;

import com.cvnavi.schduler.task.AbstractDailyTask;
import com.cvnavi.schduler.task.ScheduleAnnotation;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ScheduleAnnotation(begin = "00:30:00",end = "15:43:30",period = 600000)
@Log4j2
public class ChinaMobileTask extends AbstractDailyTask {

    public static final String USER_AGENT="Mozilla/5.0 (Linux; Android 5.1.1; DUK-AL20 Build/LMY48Z) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/39.0.0.0 Safari/537.36 shydhn/2.3.0";


    public static final String[] SEC_TOKENS={
            "Z0gaexf5i-NXDdhB07d7nNakvaxteXW5w3U8LZEuU1fe2whNZmihOa-jDZlClj75",
            "4hIQmIVFtU06AdyMQikRGH87n2XKVvQHebGeQxM87277hSM3iZ3kEK3aE-RM6Ug0",
            "QLJyLvA82UaZp6UdnaVEfRTEJrWwRbKcsHYR4vJ8_MUKIXgmjYXUKw0Ev080xlGD",
            "HsDXWkrUXFrdwmYCMKzNlWLteJ8NnZ3HHByph0-HQldL8BHHlADm5QyCFC04HMd0"
    };

    public static final String[] TASK_ID={"1115073","1115074","1118466","1115076","1116894","1115077","1118090","1118091","1118823","1115078","1115075"};

    @Override
    public void doTask() {
        try {
            for(String token:SEC_TOKENS){
                String sSOCode=getSsoCode(token);
                for(String s:TASK_ID){
                    doRequest(sSOCode,s);
                    Thread.sleep(new Random().nextInt(3000));
                }
            }
        } catch (Exception e) {
           log.error(e.getMessage(),e);
        }
    }
    public static void doRequest(String loginSign,String taskId) throws Exception {
        HttpPost httpPost = new HttpPost("https://professorhe.sh.chinamobile.com/datau/datau/xrPneumaShangBao.du?eventId=1114647&channelId=hn&loginSign="+loginSign);
        httpPost.addHeader("User-Agent",USER_AGENT);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("taskId", taskId));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        CloseableHttpClient httpclient = HttpClients.createDefault();

        CloseableHttpResponse response2 = httpclient.execute(httpPost);

        try {
            HttpEntity entity2 = response2.getEntity();
            String result=EntityUtils.toString(entity2);
            log.info(result);
            EntityUtils.consume(entity2);
        } finally {
            response2.close();
        }
    }

    public static String getSsoCode(String token) throws Exception {
        String sSOCode=null;
        HttpPost httpPost = new HttpPost("https://professorhe.sh.chinamobile.com/datau/datau/getSecCode.du");
        httpPost.addHeader("User-Agent",USER_AGENT);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("secToken", "Z0gaexf5i-NXDdhB07d7nNakvaxteXW5w3U8LZEuU1fe2whNZmihOa-jDZlClj75"));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        CloseableHttpClient httpclient = HttpClients.createDefault();

        CloseableHttpResponse response2 = httpclient.execute(httpPost);

        try {
            HttpEntity entity2 = response2.getEntity();
            String result=EntityUtils.toString(entity2);
            log.info(result);
            sSOCode=result.substring(12,44);
            EntityUtils.consume(entity2);

        } finally {
            response2.close();
        }
        return sSOCode;
    }

}

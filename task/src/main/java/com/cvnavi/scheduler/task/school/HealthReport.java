package com.cvnavi.scheduler.task.school;


import com.cvnavi.scheduler.task.AbstractDailyTask;
import com.cvnavi.scheduler.task.ScheduleAnnotation;
import com.cvnavi.scheduler.util.HttpUtil;
import com.cvnavi.scheduler.util.MailSender;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.map.HashedMap;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Level;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@ScheduleAnnotation(begin = "06:30:00",end = "06:31:00",period = 600000)
@Log4j2
public class HealthReport extends AbstractDailyTask {

    static String authorization="bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1lIjoic2hpbmUiLCJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9zaWQiOiItMSIsIlVzZXJOYW1lIjoic2hpbmUiLCJVc2VyU24iOiIzODUxM2Q0Ny1iY2ZiLTRmMGMtOTZlOS1kMjgyZGFkYjRmNWYiLCJTY2hvb2xDb2RlIjoiLTEiLCJTY2hvb2xJZCI6IjAiLCJQaG9uZSI6IjE1MDAwMzM3MTIwIiwiZXhwIjoxNTkyMjcxMTc4LCJpc3MiOiJjbnRlY2giLCJhdWQiOiJjbnRlY2gifQ.Ikof0vR_vU9R4fi_-iXbR7aMraCzLTdAtlKqEd4fpwk";
    static {
        InputStream is = HealthReport.class.getResourceAsStream("/config.properties");
        Properties p = new Properties();
        try {
            p.load(is);
            authorization=p.getProperty("health.report.authorization");

        } catch (IOException e) {

        }

    }

    @Override
    public void doTask() {
        try {
            doReport();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void doReport() throws Exception{
        String url="https://xuhui.api.chennutech.com/api/v1/report/add";

        HttpPost httpPost = new HttpPost(url);
        Header[] headers={
                new BasicHeader("Accept","application/json, text/plain, */*"),
                new BasicHeader("Origin","https://xuhui.wechat.chennutech.com"),
                new BasicHeader("Authorization",authorization),
                new BasicHeader("User-Agent","Mozilla/5.0 (Linux; Android 6.0.1; MI MAX Build/MMB29M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/78.0.3904.62 XWEB/2352 MMWEBSDK/200301 Mobile Safari/537.36 MMWEBID/8449 MicroMessenger/7.0.13.1640(0x27000D36) Process/appbrand0 NetType/WIFI Language/zh_CN ABI/arm64 WeChat/arm32 miniProgram"),
                new BasicHeader("Content-Type","application/json;charset=UTF-8"),
                new BasicHeader("X-Requested-With","com.tencent.mm"),
                new BasicHeader("Sec-Fetch-Site","same-site"),
                new BasicHeader("Sec-Fetch-Mode","cors"),
                new BasicHeader("Referer","https://xuhui.wechat.chennutech.com/today-record\n")
        };

        String inputJson = "{\"StudentId\":109598,\"InSH\":false,\"IsOut\":true,\"IsAbroad\":false,\"IsChinese\":false,\"Place\":\"重庆市荣昌区\",\"BackTime\":\"2021-02-20\",\"OnBackWay\":false,\"HealthStatus\":true,\"IsFever\":false,\"Reason\":[],\"Temperature\":\"\",\"RiskList\":[{\"IsRisk\":0,\"RiskPlace\":\"\",\"RiskRelation\":\"\",\"IsRiskAbroad\":0}]}";
        StringEntity stringEntity = new StringEntity(inputJson);
        httpPost.setEntity(stringEntity);

        HttpResponse response = HttpUtil.sendHttp(httpPost,headers,null,null,3000, Level.INFO);
        HttpEntity entity=response.getEntity();
        String result=EntityUtils.toString(entity);
        log.info(result);
        EntityUtils.consume(entity);
        ObjectMapper mapper=new ObjectMapper();
        HashedMap map=mapper.readValue(result, HashedMap.class);
        if("true".equalsIgnoreCase(map.get("haserror").toString())){
            MailSender.sendMail("error report child health",result);
        }
    }

    public static void main(String[] args) {
        HealthReport task=new HealthReport();
        task.doTask();
    }
}

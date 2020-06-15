package com.cvnavi.scheduler.task.dsj;


import com.cvnavi.scheduler.task.AbstractDailyTask;
import com.cvnavi.scheduler.task.ScheduleAnnotation;
import com.cvnavi.scheduler.task.cm.ProxyServer;
import com.cvnavi.scheduler.util.HttpUtil;
import com.cvnavi.scheduler.util.ResourceReader;
import lombok.extern.log4j.Log4j2;

import java.util.*;

@ScheduleAnnotation(begin = "06:30:00",end = "23:30:30",period = 14400000)
@Log4j2
public class DianShiJiaTask extends AbstractDailyTask {

    public static final String USER_AGENT="Mozilla/5.0 (Linux; Android 5.1.1; DUK-AL20 Build/LMY48Z) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/39.0.0.0 Safari/537.36 shydhn/2.3.0";
    static HashMap<String,String> header=new HashMap<>();


    @Override
    public void doTask() {

    }

    public static void doRequest(){
        String url="http://act.gaoqingdianshi.com/api/v4/sign/signin";
        header.put("systemSdkVersion","28");
        header.put("cityCode","310112");
        header.put("User-Agent","android%2Fclient");
        header.put("hwBrand","HONOR");
        header.put("appVerName","2.5.8");
        header.put("hwDevice","HWRVL");
        header.put("language","zh_CN_%23Hans");
        header.put("Accept-Encoding","gzip%2Cdeflate");
        header.put("uuid","3c6557938359d9128769c9b3cddfe055");
        header.put("deviceId","cc7643e3c9f4a2fc70a6a6753caca500");
        header.put("userid","28d3de84f44fb35e79b645c53bc89d1f");
        header.put("platform","10");
        header.put("hwImei","861942040019755");
        header.put("authorization","TURFNU5qUmxOREJqTURWak5UWXpORGczWkROaE16Qm1PVGd6TkdSaE5ETT18MTU3OTY4ODAwODUyNDA3NzA5OHxiNWZkNzExZjZhNzk4ZDY2YWRjMjcxNDc0ODczYmY0NDMzM2JmNjY2");
        header.put("hwModel","RVL-AL09");
        header.put("ethMac","null");
        header.put("generation","com.dianshijia.tvlive");
        header.put("hwHardware","kirin970");
        header.put("erid","77276");
        header.put("Connection","close");
        header.put("routermac","6cf37fbb7ff0");
        header.put("appVerCode","233");
        header.put("hwMac","020000000000");
        header.put("areaCode","310000");
        header.put("appid","0990028e54b2329f2dfb4e5aeea6d625");
        header.put("gpsCityCode","310112");
        header.put("marketChannelName","huawei_mobile");
        header.put("Host","act.gaoqingdianshi.com");
        header.put("Cache-Control","no-cache");

        String result=HttpUtil.doHttpGet(url,null,header,null);
        log.info(result);
    }

    public static void main(String[] args) {
        doRequest();
    }
}
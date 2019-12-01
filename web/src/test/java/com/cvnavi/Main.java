package com.cvnavi;

import com.cvnavi.scheduler.task.cm.ChinaMobileTask;
import com.cvnavi.scheduler.web.WebApplication;

public class Main {
    public static void main(String[] args) {
        WebApplication.setHome();
        new ChinaMobileTask().doTask();
    }
}

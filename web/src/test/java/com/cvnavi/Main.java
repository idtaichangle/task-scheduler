package com.cvnavi;

import com.cvnavi.scheduler.web.WebApplication;
import com.cvnavi.task.cm.ChinaMobileTask;

public class Main {
    public static void main(String[] args) {
        WebApplication.setHome();
        new ChinaMobileTask().doTask();
    }
}

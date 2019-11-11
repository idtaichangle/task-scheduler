package com.cvnavi;

import com.cvnavi.schduler.web.WebApplication;
import com.cvnavi.task.ChinaMobileTask;

public class Main {
    public static void main(String[] args) {
        WebApplication.setHome();
        new ChinaMobileTask().doTask();
    }
}

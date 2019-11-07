package com.cvnavi.schduler.task;

import java.lang.annotation.*;

@Repeatable(ScheduleAnnotations.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScheduleAnnotation {
    String begin();
    String end();
    long period();
}


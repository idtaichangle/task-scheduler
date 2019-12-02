package com.cvnavi.scheduler.util;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@Log4j2
public class ProcessUtil {

    /**
     * 列出所有java进程.(类似jps -l命令)
     * @return
     */
    public static HashMap<Long,String> jps(){
        HashMap<Long,String> map=new HashMap<>();

        long current=ProcessHandle.current().pid();

        ProcessHandle.allProcesses().forEach((handle)->{
            if(handle.pid()!=current){
                handle.info().command().ifPresent((c)->{
                    if(c.contains("bin"+ File.separator+"java")){
                        try {
                            VirtualMachine vm=VirtualMachine.attach(handle.pid()+"");
                            if(vm!=null){
                                String mainClass=vm.getSystemProperties().getProperty("sun.java.command");
                                if(mainClass.contains(" ")){
                                    mainClass=mainClass.substring(0,mainClass.indexOf(' '));
                                }
                                map.put(handle.pid(),mainClass);
                                vm.detach();
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage(),e);
                        }
                    }
                });
            }
        });
        return map;
    }
}

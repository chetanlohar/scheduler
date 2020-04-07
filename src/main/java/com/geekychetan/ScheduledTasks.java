package com.geekychetan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class ScheduledTasks {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    long currentSize;
    long backupSize;

    @Scheduled(fixedDelay = 10000)
    public void reportCurrentTime() throws InterruptedException, IOException {
        File file = new File("/tmp/dmp.jar");
        if(file.exists()){
            backupSize=(file.length()/1024);
            log.info("backupSize:" + backupSize);
            Thread.sleep(5000);
            if(file.exists()){
                currentSize = (file.length()/1024);
                log.info("currentSize:" + currentSize);
                if(backupSize== currentSize){
                    ProcessBuilder processBuilder = new ProcessBuilder();
                    processBuilder.command("sh","/root/deployDMP.sh");
                    Process process = processBuilder.start();
                    StringBuilder output = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line + "\n");
                    }
                    int exitVal = process.waitFor();
                    if (exitVal == 0) {
                        log.info("Success! ");
                        log.info(output.toString());
                    } else {
                        log.error("exit error code: {}",exitVal);
                    }
                }
                else{
                    log.info("file copying in progress...");
                }
            }
        }
    }
}

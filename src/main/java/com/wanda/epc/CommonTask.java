package com.wanda.epc;


import com.wanda.epc.device.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author: 孙率众
 **/
@Configuration
@EnableScheduling
public class CommonTask {


    @Autowired
    private Device device;

    @Scheduled(cron = "${epc.cron:0/10 * * * * ?}")
    public boolean processData() throws Exception {
        return device.processData();
    }

}

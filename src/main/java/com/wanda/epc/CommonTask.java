package com.wanda.epc;

import com.wanda.epc.device.TcpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @program: iot_epc
 * @description: 扶梯采集器
 * @author: liuruishuo
 * @create: 2022-11-08 17:07
 **/
@Configuration
@EnableScheduling
public class CommonTask {


    @Autowired
    private TcpClient client;

    @Scheduled(cron = "0/10 * * * * ?")
    public boolean processData() throws Exception {
         client.doRequest();
         return true;
    }

}

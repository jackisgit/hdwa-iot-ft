package com.wanda.epc;


import com.wanda.epc.device.OtisTcpFT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @program: iot_epc
 * @description: 奥特斯直梯
 * @author: 孙率众
 * @create: 2023-01-29 17:07
 **/
@Configuration
@EnableScheduling
public class CommonTask {

    @Autowired
    private OtisTcpFT device;// opc

//    @Autowired
//    private OtisTcpFTAddr otisTcpFTAddr;// opc

    @Scheduled(cron = "0/30 * * * * ?")
    public boolean processData() throws Exception {
//        otisTcpFTAddr.processData();
        return device.processData();
    }

}

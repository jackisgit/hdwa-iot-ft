package com.wanda.epc.device;


import com.wanda.epc.param.DeviceMessage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Liurs
 * @project iot-epc-module
 * @description 电梯采集器
 * @date 2023/03/02 15:44:36
 */
@Slf4j
@Component
public class SlDevice extends BaseDevice {

    private final static Logger logger = LoggerFactory.getLogger(SlDevice.class);

    @Autowired
    CommonDevice commonDevice;

    /**
     * 解析一个电梯的数据
     */
    public void oneInfo(byte[] info) {
        int num = (info[0] & 0xff) & 0x3f;
        logger.info("电梯编号：" + num);
        String SXZT = "";
        String XTZT = "";
        String YXZT = "";
        String faultStatus = "";
        //电梯编号 info[1] 上下行 （停止）	bit7~6  00 停止，10上行，01下行
        switch ((info[1] & 0xff) >> 6) {
            case 0:
                logger.info("电梯停止");
                SXZT = "0";
                XTZT = "0";
                YXZT = "0";
                break;
            case 1:
                logger.info("电梯上行");
                SXZT = "0";
                XTZT = "1";
                YXZT = "1";
                break;
            case 2:
                logger.info("电梯下行");
                SXZT = "1";
                XTZT = "0";
                YXZT = "1";
                break;
            default:
                logger.info("电梯上下行，出错！");
                break;
        }

        if (((info[2] >> 2) & 0x1) == 1) {
            logger.info("电梯有故障,综合故障");
            faultStatus = "1";
        } else {
            logger.info("电梯无故障，无综合故障");
            faultStatus = "0";
        }
        DeviceMessage deviceMessageSXZT = deviceParamMap.get(num + "_wD_shifoushangxing");
        if (deviceMessageSXZT != null) {
            deviceMessageSXZT.setValue(SXZT);
            sendMessage(deviceMessageSXZT);
        }
        DeviceMessage deviceMessageXTZT = deviceParamMap.get(num + "_wD_shifouxiaxing");
        if (deviceMessageXTZT != null) {
            deviceMessageXTZT.setValue(XTZT);
            sendMessage(deviceMessageXTZT);
        }
        DeviceMessage deviceMessageYXZT = deviceParamMap.get(num + "_runStatus");
        if (deviceMessageYXZT != null) {
            deviceMessageYXZT.setValue(YXZT);
            sendMessage(deviceMessageYXZT);
        }
        DeviceMessage deviceMessageFaultStatus = deviceParamMap.get(num + "_faultStatus");
        if (deviceMessageFaultStatus != null) {
            deviceMessageFaultStatus.setValue(faultStatus);
            sendMessage(deviceMessageFaultStatus);
        }
    }


    @Override
    public void sendMessage(DeviceMessage dm) {
        if (dm != null) {
            commonDevice.sendMessage(dm);
        }
    }

    @Override
    public boolean processData() throws Exception {

        return false;
    }

    @Override
    public void dispatchCommand(String meter, Integer funcid, String value, String message) throws Exception {
    }

    @Override
    public boolean processData(String... obj) throws Exception {
        return false;
    }

}

package com.wanda.epc.device;


import com.wanda.epc.param.DeviceMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Liurs
 * @project iot-epc-module
 * @description 电梯采集器
 * @date 2023/03/02 15:44:36
 */
@Slf4j
@Service
public class Device extends BaseDevice {

    public static final String WD_SHIFOUSHANGXING = "_wD_shifoushangxing";
    public static final String WD_SHIFOUXIAXING = "_wD_shifouxiaxing";
    public static final String RUN_STATUS = "_runStatus";
    public static final String FAULT_STATUS = "_faultStatus";

    /**
     * 解析一个电梯的数据
     */
    public void oneInfo(byte[] info) {
        int num = (info[0] & 0xff) & 0x3f;
        log.info("电梯编号：" + num);
        String sxzt = "";
        String xtzt = "";
        String yxzt = "";
        String faultStatus = "";
        //电梯编号 info[1] 上下行 （停止）	bit7~6  00 停止，10上行，01下行
        switch ((info[1] & 0xff) >> 6) {
            case 0:
                log.info("电梯停止");
                sxzt = "0";
                xtzt = "0";
                yxzt = "0";
                break;
            case 1:
                log.info("电梯上行");
                sxzt = "0";
                xtzt = "1";
                yxzt = "1";
                break;
            case 2:
                log.info("电梯下行");
                sxzt = "1";
                xtzt = "0";
                yxzt = "1";
                break;
            default:
                log.info("电梯上下行，出错！");
                break;
        }

        if (((info[2] >> 2) & 0x1) == 1) {
            log.info("电梯有故障,综合故障");
            faultStatus = "1";
        } else {
            log.info("电梯无故障，无综合故障");
            faultStatus = "0";
        }
        sendMsg(num + WD_SHIFOUSHANGXING, sxzt);
        sendMsg(num + WD_SHIFOUXIAXING, xtzt);
        sendMsg(num + RUN_STATUS, yxzt);
        sendMsg(num + FAULT_STATUS, faultStatus);
    }


    @Override
    public void sendMessage(DeviceMessage dm) {
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

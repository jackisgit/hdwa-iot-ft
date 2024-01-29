package com.wanda.epc.device;

import com.wanda.epc.param.DeviceMessage;
import com.wanda.epc.util.DAPCUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author 孙率众
 * @version 1.0
 * @project iot_epc
 * @description 奥特斯直梯
 * @date 2023/1/17 14:28:34
 */

@Slf4j
@Service
public class OtisTcpFT extends BaseDevice {

    private final static Logger logger = LoggerFactory.getLogger(CommonDevice.class);
    public static final String YXZT = "YXZT";
    public static final String GZZT = "GZZT";
    public static final String SXZT = "SXZT";
    public static final String XTZT = "XTZT";
    private ModbusTcp modbusTcp = new ModbusTcp();
    private int modbusAddr = 1;
    @Autowired
    private TcpClientCommunicator communicator;

    @Override
    public void sendMessage(DeviceMessage dm) {
        commonDevice.sendMessage(dm);
    }

    @Override
    public boolean processData() throws Exception {
        if (modbusAddr > 2) {
            modbusAddr = 1;
        }
        int index = 300;
        if (this.modbusAddr == 2) {
            index = 415;
        }
        try {
            byte[] receiveBuff = this.communicator.writeAndReadBuffer(
                    this.modbusTcp.sendReadBuff(this.modbusAddr, 3, index, 115), true);
            byte[] msgbuff = this.modbusTcp.parseReceiveBuff(this.modbusAddr, 3, 230, receiveBuff);
            logger.info("msgbuff" + DAPCUtil.toHex(msgbuff));
            if (msgbuff != null) {
                int len = msgbuff.length;
                if (len % 10 == 0) {
                    for (int i = 0; i < len; ) {
                        String yxzt = "0";
                        String gzzt = "0";
                        String sxzt = "0";
                        String lc = "0";
                        String xxzt = "0";
                        byte[] jxbuff = new byte[10];
                        System.arraycopy(msgbuff, i, jxbuff, 0, 10);
                        if (jxbuff[3] == 0) {
                            if (jxbuff[7] == 1) {
                                yxzt = "1";
                                xxzt = "1";
                            } else if (jxbuff[7] == 2) {
                                yxzt = "1";
                                sxzt = "1";
                            }
                        } else {
                            gzzt = "1";
                        }
                        i += 10;
                        sendMsg(YXZT + String.valueOf(jxbuff[0]),yxzt.replace(" ",""));
                        sendMsg(GZZT + String.valueOf(jxbuff[0]),gzzt.replace(" ",""));
                        sendMsg(SXZT + String.valueOf(jxbuff[0]),sxzt.replace(" ",""));
                        sendMsg(XTZT + String.valueOf(jxbuff[0]),xxzt.replace(" ",""));
                        logger.info(String.valueOf(jxbuff[0]) + "号扶梯" + "YXZT:" + yxzt + " GZZT:" + gzzt + " SXZT:" + sxzt + " XTZT:" + xxzt + " LC:" + lc);
                    }
                }
            }
            modbusAddr++;
        } catch (Exception e) {
            logger.error(this.modbusAddr + "采集失败", e);
        }
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

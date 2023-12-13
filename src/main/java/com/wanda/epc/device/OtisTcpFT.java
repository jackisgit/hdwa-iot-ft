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
                        String YXZT = "0";
                        String GZZT = "0";
                        String SXZT = "0";
                        String LC = "0";
                        String XTZT = "0";
                        byte[] jxbuff = new byte[10];
                        System.arraycopy(msgbuff, i, jxbuff, 0, 10);
                        if (jxbuff[3] == 0) {
                            if (jxbuff[7] == 1) {
                                YXZT = "1";
                                XTZT = "1";
                            } else if (jxbuff[7] == 2) {
                                YXZT = "1";
                                SXZT = "1";
                            }
                        } else {
                            GZZT = "1";
                        }
                        i += 10;
                        List<DeviceMessage> deviceMessageYXZT = deviceParamListMap.get("YXZT" + String.valueOf(jxbuff[0]));
                        if (!CollectionUtils.isEmpty(deviceMessageYXZT)) {
                            String finalYXZT = YXZT.trim();
                            deviceMessageYXZT.forEach(deviceMessage -> {
                                deviceMessage.setValue(finalYXZT);
                                sendMessage(deviceMessage);
                            });
                        }
                        List<DeviceMessage> deviceMessageGZZT = deviceParamListMap.get("GZZT" + String.valueOf(jxbuff[0]));
                        if (!CollectionUtils.isEmpty(deviceMessageGZZT)) {
                            String finalGZZT = GZZT.trim();
                            deviceMessageGZZT.forEach(deviceMessage -> {
                                deviceMessage.setValue(finalGZZT);
                                sendMessage(deviceMessage);
                            });
                        }
                        List<DeviceMessage> deviceMessageSXZT = deviceParamListMap.get("SXZT" + String.valueOf(jxbuff[0]));
                        if (!CollectionUtils.isEmpty(deviceMessageSXZT)) {
                            String finalSXZT = SXZT.trim();
                            deviceMessageSXZT.forEach(deviceMessage -> {
                                deviceMessage.setValue(finalSXZT);
                                sendMessage(deviceMessage);
                            });
                        }
                        List<DeviceMessage> deviceMessageXTZT = deviceParamListMap.get("XTZT" + String.valueOf(jxbuff[0]));
                        if (!CollectionUtils.isEmpty(deviceMessageXTZT)) {
                            String finalXTZT = XTZT.trim();
                            deviceMessageXTZT.forEach(deviceMessage -> {
                                deviceMessage.setValue(finalXTZT);
                                sendMessage(deviceMessage);
                            });
                        }
                        logger.info(String.valueOf(jxbuff[0]) + "号扶梯" + "YXZT:" + YXZT + " GZZT:" + GZZT + " SXZT:" + SXZT + " XTZT:" + XTZT + " LC:" + LC);
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

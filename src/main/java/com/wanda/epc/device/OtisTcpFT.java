package com.wanda.epc.device;

import com.wanda.epc.param.DeviceMessage;
import com.wanda.epc.param.DispatchResult;
import com.wanda.epc.util.DAPCUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

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

    private ModbusTcp modbusTcp = new ModbusTcp();

    private int modbusAddr = 1;

    private final static Logger logger = LoggerFactory.getLogger(CommonDevice.class);

    private InetSocketAddress socketAddress;

    private SocketChannel sckChannel;

    @Autowired
    private TcpClientCommunicator communicator;

    @Override
    public void sendMessage(DeviceMessage dm) {
        //更新redis

        //如果数据变化则，发送emqx
        if (dm != null){
            commonDevice.sendMessage(dm);
        }
    }

    @Override
    public boolean processData() throws Exception {
        if (modbusAddr >2)
            modbusAddr = 1;
        int index = 300;
        if (this.modbusAddr == 2)
            index = 415;
        boolean isSuccess = false;
        byte[] receiveBuff = (byte[])null;
        byte[] msgbuff = (byte[])null;
        try {
            receiveBuff = this.communicator.writeAndReadBuffer(
                    this.modbusTcp.sendReadBuff(this.modbusAddr, 3, index, 115), true);
            msgbuff = this.modbusTcp.parseReceiveBuff(this.modbusAddr, 3, 230, receiveBuff);
            logger.info("msgbuff"+ DAPCUtil.toHex(msgbuff));
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
                        DeviceMessage deviceMessageYXZT = deviceParamMap.get("YXZT" + String.valueOf(jxbuff[0]));
                        if (deviceMessageYXZT != null) {
                            deviceMessageYXZT.setValue(YXZT);
                            sendMessage(deviceMessageYXZT);
                        }
                        DeviceMessage deviceMessageGZZT = deviceParamMap.get("GZZT" + String.valueOf(jxbuff[0]));
                        if (deviceMessageGZZT != null) {
                            deviceMessageGZZT.setValue(GZZT);
                            sendMessage(deviceMessageGZZT);
                        }
                        DeviceMessage deviceMessageSXZT = deviceParamMap.get("SXZT" + String.valueOf(jxbuff[0]));
                        if (deviceMessageSXZT != null) {
                            deviceMessageSXZT.setValue(SXZT);
                            sendMessage(deviceMessageSXZT);
                        }
                        DeviceMessage deviceMessageLC = deviceParamMap.get("LC" + String.valueOf(jxbuff[0]));
                        if (deviceMessageLC != null) {
                            deviceMessageLC.setValue(LC);
                            sendMessage(deviceMessageLC);
                        }
                        DeviceMessage deviceMessageXTZT = deviceParamMap.get("XTZT" + String.valueOf(jxbuff[0]));
                        if (deviceMessageXTZT != null) {
                            deviceMessageXTZT.setValue(XTZT);
                            sendMessage(deviceMessageXTZT);
                        }
                        logger.info("jxbuff"+ DAPCUtil.toHex(jxbuff));
                        logger.info("YXZT" + String.valueOf(jxbuff[0]));
                        logger.info(String.valueOf(jxbuff[0]) +"号扶梯"+ "YXZT:" + YXZT + " GZZT:" + GZZT + " SXZT:" + SXZT + " XTZT:" + XTZT + " LC:" + LC);
                    }
                } else {
                    logger.info("+ msgbuff");
                }
            }
            modbusAddr++;
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.info(String.valueOf(this.modbusAddr));
        }
        return false;
    }

    public static String ascii2String(String ASCIIs) {
        String[] ASCIIss = ASCIIs.split(",");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ASCIIss.length; i++)
            sb.append(ascii2Char(Integer.parseInt(ASCIIss[i])));
        return sb.toString();
    }

    public static char ascii2Char(int ASCII) {
        return (char)ASCII;
    }


    @Override
    public void dispatchCommand(String meter, Integer funcid, String value, String message) throws Exception{
    }

    @Override
    public boolean processData(String... obj) throws Exception {
        return false;
    }
}

package com.wanda.epc.device;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ModbusTcp协议
 *
 * @author Bo
 */
public class ModbusTcp {

    private byte[] sendBuff = new byte[12];
    private Log logger = LogFactory.getLog(ModbusTcp.class);

    public ModbusTcp() {
        for (int i = 0; i < 4; i++) {
            sendBuff[i] = 0;
        }
    }

    /**
     * 读取
     *
     * @param deviceAddress 设备地址
     * @param funCode       功能码
     * @param dataAddress   数据地址
     * @param dataNumber    读取数据数量
     * @return
     */
    public byte[] sendReadBuff(int deviceAddress, int funCode, int dataAddress,
                               int dataNumber) {

        sendBuff[4] = 0;
        sendBuff[5] = 6;
        sendBuff[6] = (byte) deviceAddress;
        sendBuff[7] = (byte) funCode;
        sendBuff[8] = (byte) ((0xff00 & dataAddress) >> 8);
        sendBuff[9] = (byte) (0xff & dataAddress);
        sendBuff[10] = (byte) ((0xff00 & dataNumber) >> 8);
        sendBuff[11] = (byte) (0xff & dataNumber);

        return sendBuff;
    }

    /**
     * 写3号指令
     *
     * @param deviceAddress 设备通讯地址
     * @param funCode       功能码
     * @param dataAddress   起始位
     * @param dataNumber    停止位
     * @param value         要更改的值
     * @return byte[] sendBuf
     */
    public byte[] sendWriteBuff(int deviceAddress, int funCode, int dataAddress, int dataNumber, int[] value) {

        byte[] sendBuf = new byte[13 + value.length];

        sendBuf[0] = 0;
        sendBuf[1] = 0;
        sendBuf[2] = 0;
        sendBuf[3] = 0;
        sendBuf[4] = 0;
        sendBuf[5] = 6;
        sendBuf[6] = (byte) deviceAddress;
        sendBuf[7] = (byte) funCode;
        sendBuf[8] = (byte) ((0xf & dataAddress) >> 8);
        sendBuf[9] = (byte) (0xff & dataAddress);
        sendBuf[10] = (byte) ((0xff00 & dataNumber) >> 8);
        sendBuf[11] = (byte) (0xff & dataNumber);
        sendBuf[12] = (byte) (value.length);
//		sendBuf[7] = (byte) ((0xff00 & value) >> 8);
//		sendBuf[8] = (byte) (0xff & value);
        for (int i = 0; i < value.length; i++) {
            sendBuf[13 + i] = (byte) value[i];
        }
        return sendBuf;
    }

    /**
     * 写5号指令
     *
     * @param deviceAddress 设备通讯地址
     * @param funCode       功能码
     * @param dataAddress   起始位
     * @param dataNumber    停止位
     * @param value         要更改的值
     * @return byte[] sendBuf
     */
    public byte[] sendWrite5Buff(int deviceAddress, int funCode, int dataAddress, int value) {

        byte[] sendBuf = new byte[12];

        sendBuf[0] = 0;
        sendBuf[1] = 0;
        sendBuf[2] = 0;
        sendBuf[3] = 0;
        sendBuf[4] = 0;
        sendBuf[5] = 6;
        sendBuf[6] = (byte) deviceAddress;
        sendBuf[7] = (byte) funCode;
        sendBuf[8] = (byte) ((0xff00 & dataAddress) >> 8);
        sendBuf[9] = (byte) (0xff & dataAddress);
        sendBuf[10] = (byte) ((0xff00 & value) >> 8);
        sendBuf[11] = (byte) (0xff & value);
        for (int i = 0; i < sendBuf.length; i++) {
            logger.info("写5号指令sendBuf[" + i + "]=" + sendBuf[i]);
        }
        return sendBuf;
    }

    /**
     * 解析 返回的字符串
     * 功能码 1 不适用，3 试用
     *
     * @param address     设备地址
     * @param funCode     功能码
     * @param length      数据长度
     * @param receiveBuff 返回的数据
     * @return
     * @throws Exception
     */
    public byte[] parseReceiveBuff(int address, int funCode, int length,
                                   byte[] receiveBuff) throws Exception {

        byte[] parsedBuff = null;
        int len = receiveBuff.length;
        int lengthT = length + 9;
        if (len >= lengthT) {
            byte addr = receiveBuff[6];
            byte code = receiveBuff[7];
            byte leth = receiveBuff[8];
            if (addr == (byte) address) {
                if (code == (byte) funCode) {
                    if (leth == (byte) length) {
                        int datalen = DAPCUtil.byteToUnsigned(receiveBuff[8]);
                        parsedBuff = new byte[datalen];
                        System.arraycopy(receiveBuff, 9, parsedBuff, 0, datalen);
                    }
                }
            } else {
                // 设备回应的功能代码和请求功能代码不一致
                byte errCode = receiveBuff[2];
                String strErrMsg = "";
                switch (errCode) {
                    case 1:
                        strErrMsg = "ILLEGAL_FUNCTION";// 非法的功能码
                    case 2:
                        strErrMsg = "ILLEGAL_DATA_ADDRESS";// 请求访问的数据地址非法
                    case 3:
                        strErrMsg = "ILLEGAL_DATA_VALUE";// 请求信文的数据非法
                    case 4:
                        strErrMsg = "SLAVE_DEVICE_FAILURE";// 当从站响应请求时，发生不可恢复的故障
                    case 5:
                        strErrMsg = "ACKNOWLWDGE";// 从站处理该主站的请求需要较长的时间
                    case 6:
                        strErrMsg = "SLAVE_DEVICE_BUSY"; // 从站忙，暂时无法响应该请求，请稍后重发
                    default:
                        strErrMsg = "function code reply error";
                }
                throw new Exception(strErrMsg);
            }
        }
        // 返回解析过的
        return parsedBuff;
    }


    /**
     * 解析 返回的字符串
     * 功能码 1 不适用，3 试用
     *
     * @param address     设备地址
     * @param funCode     功能码
     * @param length      数据长度
     * @param receiveBuff 返回的数据
     * @return
     * @throws Exception
     */
    public byte[] parseReceive1Buff(int address, int funCode, int length,
                                    byte[] receiveBuff) throws Exception {

        byte[] parsedBuff = null;
        int len = receiveBuff.length;
        int lengthT = length / 8 + 9;
        if (len >= lengthT) {
            byte addr = receiveBuff[6];
            byte code = receiveBuff[7];
            byte leth = receiveBuff[8];
            if (addr == (byte) address) {
                if (code == (byte) funCode) {
                    if (leth >= (byte) (length / 8)) {
                        int datalen = DAPCUtil.byteToUnsigned(receiveBuff[8]);
                        parsedBuff = new byte[datalen];
                        System.arraycopy(receiveBuff, 9, parsedBuff, 0, datalen);
                    }
                }
            } else {
                // 设备回应的功能代码和请求功能代码不一致
                byte errCode = receiveBuff[2];
                String strErrMsg = "";
                switch (errCode) {
                    case 1:
                        strErrMsg = "ILLEGAL_FUNCTION";// 非法的功能码
                    case 2:
                        strErrMsg = "ILLEGAL_DATA_ADDRESS";// 请求访问的数据地址非法
                    case 3:
                        strErrMsg = "ILLEGAL_DATA_VALUE";// 请求信文的数据非法
                    case 4:
                        strErrMsg = "SLAVE_DEVICE_FAILURE";// 当从站响应请求时，发生不可恢复的故障
                    case 5:
                        strErrMsg = "ACKNOWLWDGE";// 从站处理该主站的请求需要较长的时间
                    case 6:
                        strErrMsg = "SLAVE_DEVICE_BUSY"; // 从站忙，暂时无法响应该请求，请稍后重发
                    default:
                        strErrMsg = "function code reply error";
                }
                throw new Exception(strErrMsg);
            }
        }
        // 返回解析过的
        return parsedBuff;
    }


    /**
     * 解析接收5号指令反馈数据
     *
     * @param address
     * @param funCode
     * @param receiveBuff
     * @return
     * @throws Exception
     */
    public byte[] parseReceive5Buff(int address, int funCode,
                                    byte[] receiveBuff) throws Exception {

        for (int i = 0; i < receiveBuff.length; i++) {
            logger.info("解析接收5号指令反馈数据receiveBuff[" + i + "]=" + receiveBuff[i]);
        }
        byte[] parsedBuff = null;
        byte addr = receiveBuff[6];
        byte code = receiveBuff[7];
        if (addr == (byte) address) {
            if (code == (byte) funCode) {
                parsedBuff = new byte[4];
                System.arraycopy(receiveBuff, 8, parsedBuff, 0, 4);
            }
        } else {
            // 设备回应的功能代码和请求功能代码不一致
            byte errCode = receiveBuff[7];
            String strErrMsg = "";
            switch (errCode) {
                case 1:
                    strErrMsg = "ILLEGAL_FUNCTION";// 非法的功能码
                case 2:
                    strErrMsg = "ILLEGAL_DATA_ADDRESS";// 请求访问的数据地址非法
                case 3:
                    strErrMsg = "ILLEGAL_DATA_VALUE";// 请求信文的数据非法
                case 4:
                    strErrMsg = "SLAVE_DEVICE_FAILURE";// 当从站响应请求时，发生不可恢复的故障
                case 5:
                    strErrMsg = "ACKNOWLWDGE";// 从站处理该主站的请求需要较长的时间
                case 6:
                    strErrMsg = "SLAVE_DEVICE_BUSY"; // 从站忙，暂时无法响应该请求，请稍后重发
                default:
                    strErrMsg = "function code reply error";
            }
            throw new Exception(strErrMsg);
        }
        // 返回解析过的
        return parsedBuff;
    }

    /**
     * 解析接收16号指令反馈数据
     *
     * @param address
     * @param funCode
     * @param receiveBuff
     * @return
     * @throws Exception
     */
    public byte[] parseReceive16Buff(int address, int funCode,
                                     byte[] receiveBuff) throws Exception {

        byte[] parsedBuff = null;
        byte addr = receiveBuff[0];
        byte code = receiveBuff[1];
        if (code == (byte) funCode) {
            if (addr == (byte) address) {
                int datalen = DAPCUtil.byteToUnsigned(receiveBuff[2]);
                parsedBuff = new byte[datalen];
                System.arraycopy(receiveBuff, 4, parsedBuff, 0, datalen);
            }
        } else {
            // 设备回应的功能代码和请求功能代码不一致
            byte errCode = receiveBuff[2];
            String strErrMsg = "";
            switch (errCode) {
                case 1:
                    strErrMsg = "ILLEGAL_FUNCTION";// 非法的功能码
                case 2:
                    strErrMsg = "ILLEGAL_DATA_ADDRESS";// 请求访问的数据地址非法
                case 3:
                    strErrMsg = "ILLEGAL_DATA_VALUE";// 请求信文的数据非法
                case 4:
                    strErrMsg = "SLAVE_DEVICE_FAILURE";// 当从站响应请求时，发生不可恢复的故障
                case 5:
                    strErrMsg = "ACKNOWLWDGE";// 从站处理该主站的请求需要较长的时间
                case 6:
                    strErrMsg = "SLAVE_DEVICE_BUSY"; // 从站忙，暂时无法响应该请求，请稍后重发
                default:
                    strErrMsg = "function code reply error";
            }
            throw new Exception(strErrMsg);
        }
        // 返回解析过的
        return parsedBuff;
    }

    /**
     * 发送3号功能码请求<读> Read Holding Registers(Output register)
     *
     * @param deviceAddress
     * @param dataAddress
     * @param dataNumber
     * @return sendReadBuff()
     */
    public byte[] sendReadAICmd3Requst(int deviceAddress, int dataAddress, int dataNumber) {
        return sendReadBuff(deviceAddress, 3, dataAddress, dataNumber);
    }

    /**
     * 解析接收读取3号功能码反馈信息
     *
     * @param address
     * @param receiveBuff
     * @return parseReceiveBuff()
     * @throws Exception
     */
    public byte[] parseReceiveReadAICmd3Response(int address, int length, byte[] receiveBuff) throws Exception {
        return parseReceiveBuff(address, 3, length, receiveBuff);
    }

    /**
     * 发送5号功能码请求<写>
     *
     * @param deviceAddress
     * @param dataAddress
     * @param dataNum
     * @param data
     * @return sendWriteBuff();
     */
    public byte[] sendWriteAI5CmdRequst(int deviceAddress, int dataAddress, int value) {
        return sendWrite5Buff(deviceAddress, 5, dataAddress, value);
    }

    /**
     * 解析接收读取5号功能码反馈信息
     *
     * @param address
     * @param receiveBuff
     * @return parseReceiveBuff()
     * @throws CRCException
     * @throws ModbusException
     * @throws IndexOutOfBoundsException
     */
    public byte[] parseReceiveReadAICmd5Response(int address, byte[] receiveBuff) throws Exception {
        return parseReceive5Buff(address, 5, receiveBuff);
    }

    /**
     * 发送16号功能码请求<写>
     *
     * @param deviceAddress
     * @param dataAddress
     * @param dataNum
     * @param data
     * @return sendWriteBuff();
     */
    public byte[] sendWriteAI16CmdRequst(int deviceAddress, int dataAddress, int dataNum, int[] data) {
        return sendWriteBuff(deviceAddress, 16, dataAddress, dataNum, data);
    }

    /**
     * 解析接收读取16号功能码反馈信息
     *
     * @param address
     * @param receiveBuff
     * @return parseReceiveBuff()
     * @throws CRCException
     * @throws ModbusException
     * @throws IndexOutOfBoundsException
     */
    public byte[] parseReceiveReadAICmd16Response(int address, byte[] receiveBuff) throws Exception {
        return parseReceive16Buff(address, 16, receiveBuff);
    }

    public static void main(String[] args) {
        ModbusTcp modbusTcp = new ModbusTcp();
        modbusTcp.sendWrite5Buff(1, 5, 200, 65280);
    }
}

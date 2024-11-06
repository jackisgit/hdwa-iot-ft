package com.wanda.epc.device;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;
import com.digitalpetri.modbus.requests.ReadCoilsRequest;
import com.digitalpetri.modbus.requests.ReadDiscreteInputsRequest;
import com.digitalpetri.modbus.requests.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.requests.ReadInputRegistersRequest;
import com.digitalpetri.modbus.responses.ReadCoilsResponse;
import com.digitalpetri.modbus.responses.ReadDiscreteInputsResponse;
import com.digitalpetri.modbus.responses.ReadHoldingRegistersResponse;
import com.digitalpetri.modbus.responses.ReadInputRegistersResponse;
import com.wanda.epc.param.DeviceMessage;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;


/**
 * @author 孙率众
 * @project iot-epc-module
 * @description 日历扶梯采集器
 * @date 2023/03/02 15:44:36
 */
@Slf4j
@Service
public class Device extends BaseDevice {

    public static final String WD_SHIFOUSHANGXING = "_wD_shifoushangxing";
    public static final String WD_SHIFOUXIAXING = "_wD_shifouxiaxing";
    public static final String RUN_STATUS = "_runStatus";
    public static final String FAULT_STATUS = "_faultStatus";
    private final static Logger logger = LoggerFactory.getLogger(Device.class);
    private static ModbusTcpMaster master;
    private static Object result = null;
    private static int registerTypeId = 3;
    private static int dataTypeId = 3;
    @Autowired
    CommonDevice commonDevice;
    /**
     * ModbusIp
     */
    @Value("${modbus.serverIP}")
    private String serverIp;
    /**
     * ModbusPort
     */
    @Value("${modbus.port}")
    private Integer port;

    /**
     * 电梯开始编号，用作遍历查询
     */
    @Value("${epc.startUnitId}")
    private Integer startUnitId;

    /**
     * 电梯最大编号，用作遍历查询
     */
    @Value("${epc.unitIdCount}")
    private Integer unitIdCount;

    private static String hexString2binaryString(String hexString) {
        // 将16进制数转换为对应的整数
        int decimalNumber = Integer.parseInt(hexString, 16);
        // 将整数转换为2进制字符串
        String binaryNumber = Integer.toBinaryString(decimalNumber);
        while (binaryNumber.length() < 16) {
            binaryNumber = "0" + binaryNumber;
        }
        return binaryNumber;
    }

    @PostConstruct
    public void init() {
        ModbusTcpMasterConfig config = new ModbusTcpMasterConfig.
                Builder(serverIp).
                setPort(port).
                build();
        master = new ModbusTcpMaster(config);
        master.connect();
    }

    @PreDestroy
    public void close() {
        master.disconnect();
    }

    @Override
    public void sendMessage(DeviceMessage dm) {
        //如果数据变化则，发送emqx
        if (dm != null) {
            commonDevice.sendMessage(dm);
        }
    }

    @Override
    public boolean processData() {
        //读操作
        for (int unitId = startUnitId; unitId <= unitIdCount; unitId++) {
            try {
                String status = (String) readDevInfo(registerTypeId, dataTypeId, unitId, 1, 1);
                String[] s = status.split("\\s+");
                String data = s[1];
                logger.info(unitId + "号扶梯，采集数据：" + Arrays.toString(s));
                String runStatus = "0";
                String faultStatus = "0";
                String up = "0";
                String down = "0";
                //上行
                if ("90".equals(data) || "80".equals(data)) {
                    runStatus = "1";
                    up = "1";
                    down = "0";
                }
                //下行
                else if ("50".equals(data) || "60".equals(data)) {
                    runStatus = "1";
                    up = "0";
                    down = "1";
                }
                //故障
                else if ("20".equals(data)) {
                    faultStatus = "1";
                }
                log.warn(unitId + "号扶梯：故障：" + faultStatus + "  上行：" + up + "  下行：" + down);

                sendMsg(unitId + RUN_STATUS, runStatus);
                sendMsg(unitId + WD_SHIFOUSHANGXING, up);
                sendMsg(unitId + WD_SHIFOUXIAXING, down);
                sendMsg(unitId + FAULT_STATUS, faultStatus);
            } catch (Exception e) {
                logger.error("采集{}号扶梯数据失败", unitId, e);
            }

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


    /**
     * @Param registerTypeId 寄存器类型
     * @Param dataTypeId 数据类型
     * @Param address 寄存器地址
     * @Param unitId 设备号
     * @Param quantity 寄存器个数
     */
    private Object readDevInfo(int registerTypeId, int dataTypeId, int address, int unitId, int quantity) throws Exception {
        address = Math.max(0, address - 1);

        if (registerTypeId == 1) {// 读写线圈
            CompletableFuture<ReadCoilsResponse> future = master
                    .sendRequest(new ReadCoilsRequest(address, Math.max(1, quantity)), unitId);
            ReadCoilsResponse readCoilsResponse = future.get();// 工具类做的同步返回.实际使用推荐结合业务进行异步处理
            if (readCoilsResponse != null) {
                ByteBuf buf = readCoilsResponse.getCoilStatus();
                result = buf.readBoolean();
                // number或boolean
                if (result instanceof Boolean) {
                    if ((boolean) result) {
                        result = 1;
                    } else {
                        result = 0;
                    }
                }

                ReferenceCountUtil.release(readCoilsResponse);
            }
        } else if (registerTypeId == 2) {// 读输入点开关量
            CompletableFuture<ReadDiscreteInputsResponse> future = master
                    .sendRequest(new ReadDiscreteInputsRequest(address, Math.max(1, quantity)), unitId);
            ReadDiscreteInputsResponse discreteInputsResponse = future.get();// 工具类做的同步返回.实际使用推荐结合业务进行异步处理
            if (discreteInputsResponse != null) {
                ByteBuf buf = discreteInputsResponse.getInputStatus();
                // number或boolean
                if (result instanceof Boolean) {
                    if ((boolean) result) {
                        result = 1;
                    } else {
                        result = 0;
                    }
                }
                ReferenceCountUtil.release(discreteInputsResponse);
            }
        } else if (registerTypeId == 3) {// 读写寄存器(HOLDING REGISTER)
            CompletableFuture<ReadHoldingRegistersResponse> future = master
                    .sendRequest(new ReadHoldingRegistersRequest(address, Math.max(1, quantity)), unitId);
            ReadHoldingRegistersResponse readHoldingRegistersResponse = future.get();// 工具类做的同步返回.实际使用推荐结合业务进行异步处理
            if (readHoldingRegistersResponse != null) {
                ByteBuf buf = readHoldingRegistersResponse.getRegisters();
                result = readDataByType(buf, dataTypeId);
                ReferenceCountUtil.release(readHoldingRegistersResponse);
            }
        } else if (registerTypeId == 4) {// 只读寄存器
            CompletableFuture<ReadInputRegistersResponse> future = master
                    .sendRequest(new ReadInputRegistersRequest(address, Math.max(1, quantity)), unitId);
            ReadInputRegistersResponse readInputRegistersResponse = future.get();// 工具类做的同步返回.实际使用推荐结合业务进行异步处理
            if (readInputRegistersResponse != null) {
                ByteBuf buf = readInputRegistersResponse.getRegisters();
                result = readDataByType(buf, dataTypeId);
                ReferenceCountUtil.release(readInputRegistersResponse);
            }
        }
        return result;
    }

    private Object readDataByType(ByteBuf buf, int type) {
        switch (type) {
            case 1: // 二进制
                return ByteUtil.intToBinary(buf.readShort());
            case 2: // 整形
                return buf.readShort();
            case 4: // 浮点
                byte[] datas = new byte[4];
                buf.readBytes(datas);
                return ByteUtil.byte2float(datas);
            case 5:
                byte[] datasSwapped = new byte[4];
                buf.readBytes(datasSwapped);
                return ByteUtil.byte2floatSwapped(datasSwapped);
            case 3: // 十六进制
                byte[] bytes = new byte[buf.capacity()];
                buf.readBytes(bytes, 0, buf.capacity());
                return ByteUtil.byteArrToHexString(bytes, true);
            default:
                byte[] datas1 = new byte[buf.readableBytes()];
                buf.readBytes(datas1);
                return ByteUtil.byteArrToHexString(datas1);
        }
    }
}

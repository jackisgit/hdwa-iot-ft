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
import java.util.concurrent.CompletableFuture;


/**
 * @author Liurs
 * @project iot-epc-module
 * @description 日历扶梯采集器
 * @date 2023/03/02 15:44:36
 */
@Slf4j
@Service
public class RLFTDevice  extends BaseDevice {

    private final static Logger logger = LoggerFactory.getLogger(RLFTDevice.class);


    @Autowired
    CommonDevice commonDevice;

    @Value("${epc.gcId}")
    private String gcId;

    @Value("${epc.gatewayId}")
    private String gatewayId;

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

    private static ModbusTcpMaster master;

    private static Object  result = null;

    private static int registerTypeId = 3;

    private static int dataTypeId = 3;


    @PostConstruct
    public void init() {
        ModbusTcpMasterConfig config = new ModbusTcpMasterConfig.
                Builder(serverIp).
                setPort(port).
                build();
        master = new ModbusTcpMaster(config);
    }

    @Override
    public void sendMessage(DeviceMessage dm) {
        //如果数据变化则，发送emqx
        if (dm != null) {
            commonDevice.sendMessage(dm);
        }
    }

    @Override
    public boolean processData() throws Exception {
        //读操作
        for (int unitId =1; unitId <= 47; unitId++){
            try {
                //40002 状态
                String status = (String)readDevInfo(registerTypeId, dataTypeId, 2, unitId, 1);
                String[] s = status.split("\\s+");
                String zt = ByteUtil.decimalToBinary(ByteUtil.hexStringToInt(s[0]), 8);
                String xxzt = String.valueOf(zt.charAt(4));
                String sxzt = String.valueOf(zt.charAt(5));
                String yxzt = String.valueOf(zt.charAt(7));
                //40003 故障
                String faultStatus = (String)readDevInfo(registerTypeId, dataTypeId, 3, unitId, 1);
                String[] f = faultStatus.split("\\s+");
                String fault = ByteUtil.decimalToBinary(ByteUtil.hexStringToInt(f[0]), 8);
                String gz = String.valueOf(fault.charAt(7));
                DeviceMessage deviceMessageSXZT = deviceParamMap.get(unitId + "_wD_shifoushangxing");
                if (deviceMessageSXZT != null) {
                    deviceMessageSXZT.setValue(sxzt);
                    sendMessage(deviceMessageSXZT);
                }
                DeviceMessage deviceMessageXTZT = deviceParamMap.get(unitId + "_wD_shifouxiaxing");
                if (deviceMessageXTZT != null) {
                    deviceMessageXTZT.setValue(xxzt);
                    sendMessage(deviceMessageXTZT);
                }
                DeviceMessage deviceMessageYXZT = deviceParamMap.get(unitId + "_runStatus");
                if (deviceMessageYXZT != null) {
                    deviceMessageYXZT.setValue(yxzt);
                    sendMessage(deviceMessageYXZT);
                }
                DeviceMessage deviceMessageFaultStatus = deviceParamMap.get(unitId + "_faultStatus");
                if (deviceMessageFaultStatus != null) {
                    if (gz.equals("0")){
                        deviceMessageFaultStatus.setValue("0");
                    }else {
                        deviceMessageFaultStatus.setValue("1");
                    }
                    sendMessage(deviceMessageFaultStatus);
                }
                DeviceMessage deviceMessageAccRunTime = deviceParamMap.get(unitId + "_accRunTime");
                if (deviceMessageAccRunTime != null) {
                    deviceMessageAccRunTime.setValue("0");
                    sendMessage(deviceMessageAccRunTime);
                }
                logger.info(unitId +"号扶梯"+ "YXZT:" + yxzt + " SXZT:" + sxzt + " XXZT:" + xxzt );
            }catch (Exception e){
                logger.info("采集{}号扶梯数据失败", unitId);
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
     *
     * @Param  registerTypeId 寄存器类型
     * @Param  dataTypeId 数据类型
     * @Param  address 寄存器地址
     * @Param  unitId 设备号
     * @Param  quantity 寄存器个数
     */
    public static Object readDevInfo(int  registerTypeId, int  dataTypeId,  int address, int unitId, int quantity) throws Exception {
        address = Math.max(0, address - 1);

        if (registerTypeId == 1) {// 读写线圈
            CompletableFuture<ReadCoilsResponse> future = master
                    .sendRequest(new ReadCoilsRequest(address, Math.max(1, quantity)), unitId);
            ReadCoilsResponse readCoilsResponse = future.get();// 工具类做的同步返回.实际使用推荐结合业务进行异步处理
            if (readCoilsResponse != null) {
                ByteBuf buf = readCoilsResponse.getCoilStatus();
                result = buf.readBoolean();
                // number或boolean
                if(result instanceof Boolean){
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
                if(result instanceof Boolean){
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

    private static Object readDataByType(ByteBuf buf, int type) {
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

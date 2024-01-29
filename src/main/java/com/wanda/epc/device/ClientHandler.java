package com.wanda.epc.device;

import com.wanda.epc.common.SpringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@ChannelHandler.Sharable
@Component
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    //数据类别 1：轮询命令（ENQ ） 数值：0xE0
    //数据类别 2：回应数据（DAT ） 数值：0xA0
    private static byte ctrl_Data = (byte) 0xa0;

    private static Device slDevice;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            ByteBuf buf = (ByteBuf) msg;
            byte[] buff = new byte[buf.readableBytes()];
            // 复制内容到字节数组bytes
            buf.readBytes(buff);
            // 将接收到的数据转为字符串，此字符串就是客户端发送的字符串
            logger.info("接收到的数据:" + ByteUtil.hex2String(buff));
            int dataSize = ByteUtil.bytesToShort(buff, 0);
            //数据长度2个byte 控制码1个byte 校验码一个byte 电梯字节12byte
            if (buff.length % 12 != 4 || dataSize != buff.length) {
                logger.info("长度不对");
                return;
            } else if (buff[2] != ctrl_Data) {
                logger.info("收到的控制码不正确，获得数据失败！");
                return;
            } else if (cRC(buff)) {
                //格式验证完成，开始解析
                byte[] temp = new byte[12];
                for (int i = 3; i < buff.length - 12; i = i + 12) {
                    System.arraycopy(buff, i, temp, 0, 12);
                    slDevice = SpringUtil.getBean(Device.class);
                    slDevice.oneInfo(temp);
                }
            } else {
                logger.info("校验失败！");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 校验码
     *
     * @param b
     * @return 是否校验成功
     */
    private boolean cRC(byte[] b) {

        byte crcByte = 0;
        for (int i = 0; i < b.length - 1; i++) {
            crcByte += b[i];
        }
        crcByte = (byte) ~crcByte;

        if ((b[b.length - 1] & 0xff) == (crcByte & 0xff)) {
            return true;
        }
        logger.info("CRC" + (crcByte & 0xff) + ",得到的数据为：" + (b[b.length - 1] & 0xff));
        return false;
    }


}

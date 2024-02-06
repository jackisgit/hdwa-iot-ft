package com.wanda.epc.device;

import com.wanda.epc.common.SpringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Slf4j
@Component
public class NioClientHandler extends ChannelInboundHandlerAdapter {

    //数据类别 1：轮询命令（ENQ ） 数值：0xE0
    //数据类别 2：回应数据（DAT ） 数值：0xA0
    private static byte ctrl_Data = (byte) 0xa0;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            ByteBuf buf = (ByteBuf) msg;
            byte[] buff = new byte[buf.readableBytes()];
            // 复制内容到字节数组bytes
            buf.readBytes(buff);
            // 将接收到的数据转为字符串，此字符串就是客户端发送的字符串
            log.info("接收到的数据:" + ByteUtil.hex2String(buff));
            int dataSize = ByteUtil.bytesToShort(buff, 0);
            //数据长度2个byte 控制码1个byte 校验码一个byte 电梯字节12byte
            if (buff.length % 12 != 4 || dataSize != buff.length) {
                log.info("长度不对");
                return;
            } else if (buff[2] != ctrl_Data) {
                log.info("收到的控制码不正确，获得数据失败！");
                return;
            } else if (CRC(buff)) {
                //格式验证完成，开始解析
                byte[] temp = new byte[12];
                Device device = SpringUtil.getBean(Device.class);
                for (int i = 3; i < buff.length - 12; i = i + 12) {
                    System.arraycopy(buff, i, temp, 0, 12);
                    device.oneInfo(temp);
                }
            } else {
                log.info("校验失败！");
            }

        } catch (Exception e) {
            log.error("数据处理异常", e);
        }

    }

    /**
     * 连接关闭!
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress socket = (InetSocketAddress) ctx.channel().remoteAddress();
        String ip = socket.getAddress().getHostAddress();
        int port = socket.getPort();
        log.error("{}连接关闭！", ip + ":" + port);
        final Device fdDevice = SpringUtil.getBean(Device.class);
        fdDevice.reconnect();
        super.channelInactive(ctx);
    }

    /**
     * 客户端主动连接服务端
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        try {
            super.channelActive(ctx);
        } catch (Exception e) {
            log.error("客户端连接异常", e);
        }
    }

    /**
     * 发生异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.fireExceptionCaught(cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        try {
            super.userEventTriggered(ctx, evt);
        } catch (Exception e) {
            log.error("客户端异常", e);
        }
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                ctx.close();
            }
        }
    }


    /**
     * 校验码
     *
     * @param b
     * @return 是否校验成功
     */
    private boolean CRC(byte[] b) {

        byte crcByte = 0;
        for (int i = 0; i < b.length - 1; i++) {
            crcByte += b[i];
        }
        crcByte = (byte) ~crcByte;

        if ((b[b.length - 1] & 0xff) == (crcByte & 0xff)) {
            return true;
        }
        log.info("CRC" + (crcByte & 0xff) + ",得到的数据为：" + (b[b.length - 1] & 0xff));
        return false;
    }


}

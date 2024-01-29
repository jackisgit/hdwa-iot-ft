package com.wanda.epc.device;


import com.wanda.epc.param.DeviceMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Liurs
 * @project iot-epc-module
 * @description 电梯采集器
 * @date 2023/03/02 15:44:36
 */
@Slf4j
@Component
public class Device extends BaseDevice {

    private final static Logger logger = LoggerFactory.getLogger(Device.class);
    public static final String WD_SHIFOUSHANGXING = "_wD_shifoushangxing";
    public static final String WD_SHIFOUXIAXING = "_wD_shifouxiaxing";
    public static final String RUN_STATUS = "_runStatus";
    public static final String FAULT_STATUS = "_faultStatus";

    @Value("${tcp.serverIP}")
    private String serverIP;

    @Value("${tcp.port}")
    private Integer port;

    private static byte[] base = {(byte) 0x00, (byte) 0x04, (byte) 0xe0, (byte) 0x1b};

    @PostConstruct
    private void init() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            /**客户端相关配置信息*/
            Bootstrap bootstrap = new Bootstrap();
            //绑定线程组
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new ClientHandler());
                }
            });
            ChannelFuture future = bootstrap.connect(serverIP, port).sync();
            //请求报文
            ByteBuf byteBufMsg = Unpooled.buffer();
            byteBufMsg.writeBytes(base);
            future.channel().writeAndFlush(byteBufMsg);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("初始化异常：", e);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 解析一个电梯的数据
     */
    public void oneInfo(byte[] info) {
        int num = (info[0] & 0xff) & 0x3f;
        logger.info("电梯编号：" + num);
        String sxzt = "";
        String xtzt = "";
        String yxzt = "";
        String faultStatus = "";
        //电梯编号 info[1] 上下行 （停止）	bit7~6  00 停止，10上行，01下行
        switch ((info[1] & 0xff) >> 6) {
            case 0:
                logger.info("电梯停止");
                sxzt = "0";
                xtzt = "0";
                yxzt = "0";
                break;
            case 1:
                logger.info("电梯上行");
                sxzt = "0";
                xtzt = "1";
                yxzt = "1";
                break;
            case 2:
                logger.info("电梯下行");
                sxzt = "1";
                xtzt = "0";
                yxzt = "1";
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
        sendMsg(num + WD_SHIFOUSHANGXING,sxzt);
        sendMsg(num + WD_SHIFOUXIAXING,xtzt);
        sendMsg(num + RUN_STATUS,yxzt);
        sendMsg(num + FAULT_STATUS,faultStatus);
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

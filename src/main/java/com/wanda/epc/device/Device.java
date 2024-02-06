package com.wanda.epc.device;


import com.wanda.epc.param.DeviceMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author Liurs
 * @project iot-epc-module
 * @description 电梯采集器
 * @date 2023/03/02 15:44:36
 */
@Slf4j
@Component
public class Device extends BaseDevice {

    public static final String WD_SHIFOUSHANGXING = "_wD_shifoushangxing";
    public static final String WD_SHIFOUXIAXING = "_wD_shifouxiaxing";
    public static final String RUN_STATUS = "_runStatus";
    public static final String FAULT_STATUS = "_faultStatus";

    /**
     * 重连频率，单位：秒
     */
    private static final Integer RECONNECT_SECONDS = 20;
    /**
     * 线程组，用于客户端对服务端的链接、数据读写
     */
    private EventLoopGroup eventGroup = new NioEventLoopGroup();
    @Resource
    private ClientChannelInitializer nettyClientHandlerInitializer;
    @Value("${tcp.serverIP}")
    private String serverHost;
    @Value("${tcp.port}")
    private Integer serverPort;

    /**
     * 启动 Netty Client
     */
    @PostConstruct
    public void init() throws InterruptedException {
        // 创建 Bootstrap 对象，用于 Netty Client 启动
        Bootstrap bootstrap = new Bootstrap();
        // 设置 Bootstrap 的各种属性。
        bootstrap.group(eventGroup) // 设置一个 EventLoopGroup 对象
                .channel(NioSocketChannel.class)  // 指定 Channel 为客户端 NioSocketChannel
                .remoteAddress(serverHost, serverPort) // 指定链接服务器的地址
                .option(ChannelOption.SO_KEEPALIVE, true) // TCP Keepalive 机制，实现 TCP 层级的心跳保活功能
                .option(ChannelOption.TCP_NODELAY, true) // 允许较小的数据包的发送，降低延迟
                .handler(nettyClientHandlerInitializer);
        // 链接服务器，并异步等待成功，即启动客户端
        bootstrap.connect().addListener((ChannelFutureListener) future -> {
            // 连接失败
            if (!future.isSuccess()) {
                log.error("[start][Netty Client 连接服务器({}:{}) 失败]", serverHost, serverPort);
                reconnect();
                return;
            }
            future.channel();
            log.info("[start][Netty Client 连接服务器({}:{}) 成功]", serverHost, serverPort);
        });
    }

    @PreDestroy
    public void preDestroy() {
        eventGroup.shutdownGracefully();
    }

    public void reconnect() {
        eventGroup.schedule(() -> {
            log.info("[reconnect][开始重连]");
            try {
                init();
            } catch (InterruptedException e) {
                log.error("[reconnect][重连失败]", e);
            }
        }, RECONNECT_SECONDS, TimeUnit.SECONDS);
        log.info("[reconnect][{} 秒后将发起重连]", RECONNECT_SECONDS);
    }

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

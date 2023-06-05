package com.wanda.epc;

import com.wanda.epc.device.ClientHandler;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @program: iot_epc
 * @description: 扶梯采集器
 * @author: 孙率众
 * @create: 2022-11-08 17:07
 **/
@Configuration
@EnableScheduling
public class CommonTask {

    private static byte[] base = {(byte) 0x00, (byte) 0x04, (byte) 0xe0, (byte) 0x1b};

    @Value("${tcp.serverIP}")
    private String serverIP;

    @Value("${tcp.port}")
    private Integer port;

    @Scheduled(cron = "0/10 * * * * ?")
    public boolean processData() {
        doRequest();
        return true;
    }

    private void doRequest() {
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
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

}

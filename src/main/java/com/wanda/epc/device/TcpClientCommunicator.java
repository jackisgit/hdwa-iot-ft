package com.wanda.epc.device;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * TCP/IP 通信
 *
 * @author Bo
 */
@Service
public class TcpClientCommunicator {

    private int contentMsgBufferSize = 100000;
    private InetSocketAddress socketAddress;
    private SocketChannel sckChannel;
    private ByteBuffer buffMsgContent = ByteBuffer.allocate(contentMsgBufferSize);

    private boolean isOpened = false;
    private boolean isContinueOpenAfterUse = true;

    private long waitReadTime = 500;
    @Value("${modbus.serverIP}")
    private String serverIP;
    @Value("${modbus.port}")
    private int port;
    private String communicatorName;
    private static Object lockObj = new Object();
    //	private Logger				logger					= Logger.getLogger(this.getClass().getName());
    private Log logger = LogFactory.getLog(TcpClientCommunicator.class);



    /**
     * 打开TCP/IP通信口
     */
    @PostConstruct
    public boolean open() {
        //根据 IP地址/主机名和端口号创建套接字地址
        socketAddress = new InetSocketAddress(serverIP, port);
        try {
            //打开套接字通道
            sckChannel = SocketChannel.open();
            //连接此通道的套接字
            sckChannel.connect(socketAddress);
            //调整此通道的为非阻塞模式
            sckChannel.configureBlocking(false);
            //完成套接字通道的连接过程
//			sckChannel.finishConnect();
//			sckChannel.socket().setSoTimeout(3000);
            isOpened = true;
        } catch (IOException e) {
            logger.error("can't connect " + serverIP + ":" + port + "," + e.getMessage());
        }
        return isOpened;
    }

    /**
     * 读数据
     */
    public byte[] readBuffer() throws IOException {

        try {
            //设置线程读取数据等待时间
            Thread.sleep(waitReadTime);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
        int pos = 0;
        //清除此缓冲区
        buffMsgContent.clear();
        //将字节序列从此通道中读入给定的缓冲区
        pos = sckChannel.read(buffMsgContent);
        //设置此缓冲区的限制
        buffMsgContent.limit(pos);
        //反转此缓冲区
        buffMsgContent.flip();

        byte[] buf = new byte[buffMsgContent.limit()];
        if (pos > 0) {
            buffMsgContent.get(buf, 0, pos);
        } else if (pos == -1)
            close();
        return buf;
    }

    /**
     * 写数据
     */
    public void writeBuffer(byte[] b) throws IOException {
        //清除此缓冲区
        buffMsgContent.clear();
        //将传入的数据写入此缓存区
        buffMsgContent.put(b);
        //反转此缓冲区
        buffMsgContent.flip();
        //将字节序列从给定的缓冲区中写入此通道
        sckChannel.write(buffMsgContent);
    }

    /**
     * 读写数据
     */
    public byte[] writeAndReadBuffer(byte[] command, boolean isResponsable) throws IOException {

        byte[] responseBuff = null;
        synchronized (lockObj) {
            writeBuffer(command);
            if (isResponsable) {
                try {
                    Thread.sleep(waitReadTime);
                } catch (InterruptedException e) {
                    logger.error("TcpClientCommunicator writerAndReadBuffer error,InterruptedException:"
                            + e.getMessage());
                }
                responseBuff = readBuffer();
            }
            return responseBuff;
        }
    }

    /**
     * 关闭通信口
     */
    public void close() {
        try {
            //关闭此通道
            sckChannel.close();
            isOpened = false;
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}

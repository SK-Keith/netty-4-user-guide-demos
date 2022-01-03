package com.keith.transTo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author MX.Y
 * @DATE 2021/11/17 13:44
 * @qq 2690399241
 */
public class Server {
    public static void main(String[] args) {
        int port = 9999;
        InetSocketAddress address = new InetSocketAddress(port);
        SocketChannel acceptChannel;
        FileChannel fileChannel;
        try {
            ServerSocketChannel socketChannel = ServerSocketChannel.open();
            socketChannel.socket().bind(address);
            System.out.println("NonBlokingEchoServer已启动，端口：" + port);

            // 创建buffer
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            while (true) {
                acceptChannel = socketChannel.accept();
                long startTime = System.currentTimeMillis();
                fileChannel = FileChannel.open(
                        Paths.get("D:\\workspackGW\\netty-4-user-guide-demos\\netty4-demos\\" + System.currentTimeMillis() + ".zip") ,
                        StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
                while (acceptChannel.read(byteBuffer) != -1) {
                    byteBuffer.flip();
                    fileChannel.write(byteBuffer);
                    byteBuffer.rewind();
                }
                System.out.println("服务端耗时：" + (System.currentTimeMillis()- startTime));
                acceptChannel.close();
                fileChannel.close();
                byteBuffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

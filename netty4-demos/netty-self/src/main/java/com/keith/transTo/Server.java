package com.keith.transTo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;

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

            // 创建buffer 4k,文件17k,所以会循环5次
            ByteBuffer byteBuffer = ByteBuffer.allocate(4 * 1024);
            while (true) {
                acceptChannel = socketChannel.accept();
                fileChannel = FileChannel.open(
                        Paths.get("D:\\workspackGW\\netty-4-user-guide-demos\\netty4-demos\\" + new Date().getTime() + ".jpg") ,
                        StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
                int i = 0;
                while (acceptChannel.read(byteBuffer) != -1) {
                    System.out.println("第" + ++i + "次");
                    byteBuffer.flip();
                    fileChannel.write(byteBuffer);
                    byteBuffer.rewind();
                }
                acceptChannel.close();
                fileChannel.close();
                byteBuffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

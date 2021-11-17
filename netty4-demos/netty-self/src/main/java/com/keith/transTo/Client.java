package com.keith.transTo;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * @author MX.Y
 * @DATE 2021/11/17 9:28
 * @qq 2690399241
 */
public class Client {

    public static void main(String[] args) {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("localhost", 9999));
//            String fileName = "MotionProSetup_win64.zip";
            String fileName = "head.jpg";
            FileChannel channel = new FileInputStream(fileName).getChannel();
            long startTime = System.currentTimeMillis();
            long count = channel.size() / (8 * 1024 * 1024);
            channel.transferTo(0, channel.size(), socketChannel);
            channel.close();
            socketChannel.close();
//            System.out.println("拷贝的文件大小为：" + channel.size()/(1024*1024) + "M, 耗时：" + (System.currentTimeMillis()- startTime));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

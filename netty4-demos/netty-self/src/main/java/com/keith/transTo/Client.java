package com.keith.transTo;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * 使用NIO 零拷贝方法传递transferTo一个大文件
 * @author MX.Y
 * @DATE 2021/11/17 9:28
 * @qq 2690399241
 */
public class Client {

    public static void main(String[] args) {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("localhost", 9999));
            String fileName = "MotionProSetup_win64.zip";
            FileChannel channel = new FileInputStream(fileName).getChannel();
            long startTime = System.currentTimeMillis();
            int singleSize = 8 * 1024 * 1024;
            long count = channel.size() / singleSize + 1;// 单次传输的数据是8M左右,所以要循环写入
            for (long i = 0; i < count; i++) {
                channel.transferTo(i * singleSize, (i + 1) * singleSize, socketChannel);
            }
            System.out.println("拷贝的文件大小为：" + channel.size()/(1024*1024) + "M, 耗时：" + (System.currentTimeMillis()- startTime));
            channel.close();
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

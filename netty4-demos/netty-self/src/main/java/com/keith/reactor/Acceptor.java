package com.keith.reactor;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author MX.Y
 * @DATE 2021/11/18 18:28
 * @qq 2690399241
 */
public class Acceptor implements Runnable{

    private final ServerSocketChannel serverSocketChannel;

    // CPU核心数
    private final int coreNum = Runtime.getRuntime().availableProcessors();

    // 创建selector给Reactor使用
    private final Selector[] selectors = new Selector[coreNum];

    private SubReactor[] reactors = new SubReactor[coreNum];

    // subReactor的处理线程
    private Thread[] threads = new Thread[coreNum];

    public Acceptor(ServerSocketChannel serverSocketChannel) throws IOException {
        this.serverSocketChannel = serverSocketChannel;
        // 初始化
        for (int i = 0; i < coreNum; i++) {
            selectors[i] = Selector.open();
            // 初始化SubReactor
            reactors[i] = new SubReactor(selectors[i], i);
            threads[i] = new Thread(reactors[i]);
            // 启动（启动后的执行参考SubReactor里的run方法）
            threads[i].start();
        }
    }

    @Override
    public void run() {
        SocketChannel socketChannel;
        try {
            // 连接
            socketChannel = serverSocketChannel.accept();
            System.out.println(String.format("accept %s", socketChannel.getRemoteAddress()));
            socketChannel.configureBlocking(false);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

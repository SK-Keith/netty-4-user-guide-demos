package com.keith.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * @author MX.Y
 * @DATE 2021/11/18 18:08
 * @qq 2690399241
 */
public class Reactor implements Runnable{

    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;


    public Reactor(int port) throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();// 建立一个Server端通道
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);//selector模式下，所有通道必须是非阻塞的

        // Reactor入口,最初给一个channel注册上去的事件都是accept
        SelectionKey sk = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        // 绑定Acceptor处理类
        sk.attach(new Acceptor(serverSocketChannel));
    }

    @Override
    public void run() {

    }
}

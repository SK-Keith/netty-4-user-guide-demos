package com.keith.reactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

/**
 * @author MX.Y
 * @DATE 2021/11/18 21:15
 * @qq 2690399241
 */
public class SubReactor implements Runnable {

    private final Selector selector;
    // 序号，即Acceptor初始化SubReactor的下标
    private int num;
    // 注册开关表示
    private boolean register = false;


    public SubReactor(Selector selector, int num) {
        this.selector = selector;
        this.num = num;
    }

    @Override
    public void run() {
        while(!Thread.interrupted()) {
            System.out.println(String.format("NO %d SubReactor waiting for register...", num));
            while(!Thread.interrupted() && !register) {
                try {
                    if (selector.select() == 0) {
                        continue;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                while (it.hasNext()) {
                    dispatch(it.next());
                    it.remove();
                }
            }

        }
    }

    private void dispatch(SelectionKey next) {
        Runnable r = (Runnable) next.attachment();
        if (r != null) {
            r.run();
        }
    }

    public void setRegister(boolean register) {
        this.register = register;
    }
}

package com.keith.reactor;

import java.io.IOException;

/**
 * 服务端，接收到任务后调度
 * @author MX.Y
 * @DATE 2021/11/18 18:09
 * @qq 2690399241
 */
public class MainSubReactorDemo {

    public static void main(String[] args) {
        try {
            new Thread(new Reactor(9777)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

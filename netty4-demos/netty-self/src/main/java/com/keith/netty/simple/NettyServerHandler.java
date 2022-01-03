package com.keith.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

import java.util.Date;

/**
 * @author MX.Y
 * @DATE 2021/11/20 10:56
 * @qq 2690399241
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {



    // 读取数据，读取客户端发送的消息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 查看当前分配的线程是哪个
//        System.out.println("服务器读取线程" + Thread.currentThread().getName());
//        System.out.println("server ctx = " +ctx);
//        Channel channel = ctx.channel();
//        ChannelPipeline pipeline = ctx.pipeline();
//
//        ByteBuf buf = (ByteBuf) msg;
//
//        System.out.println("客户端发送消息是" +buf.toString(CharsetUtil.UTF_8));
//        System.out.println("客户端地址：" + channel.remoteAddress());
//        System.out.println("now:" + new Date());
//        Thread.sleep(5 * 1000);
//        ctx.writeAndFlush(Unpooled.copiedBuffer("你好呀, 客户端1", CharsetUtil.UTF_8));
        ctx.channel().eventLoop().execute(() -> {
            try {
                Thread.sleep(5 * 1000);
                System.out.println("now:" + new Date());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ctx.writeAndFlush(Unpooled.copiedBuffer("你好呀, 客户端1", CharsetUtil.UTF_8));
        });
        System.out.println("go on");
        System.out.println("now:" + new Date());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // writeAndFlush 是 write 和 flust
        // 发送数据(发之前进行编码)，将数据写入到缓存，并刷新
        ctx.writeAndFlush(Unpooled.copiedBuffer("你好呀, 客户端", CharsetUtil.UTF_8));
    }

}

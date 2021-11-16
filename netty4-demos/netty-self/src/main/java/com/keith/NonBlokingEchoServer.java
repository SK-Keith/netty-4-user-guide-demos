/**
 * Welcome to https://waylau.com
 */
package com.keith;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * 测试echo服务端、客户端
 * 	打印服务器接收到的字符串(编码)
 */
public class NonBlokingEchoServer {
	public static int DEFAULT_PORT = 7;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int port;

		try {
			port = Integer.parseInt(args[0]);
		} catch (RuntimeException ex) {
			port = DEFAULT_PORT;
		}

		ServerSocketChannel serverChannel;
		Selector selector;
		try {
			serverChannel = ServerSocketChannel.open();
			InetSocketAddress address = new InetSocketAddress(port);
			serverChannel.bind(address);
			serverChannel.configureBlocking(false);
			selector = Selector.open();
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);

			System.out.println("NonBlokingEchoServer已启动，端口：" + port);
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
		String encoding = System.getProperty("file.encoding");// 获取系统字符集
		while (true) {
			try {
				selector.select();
			} catch (IOException e) {
				System.out.println("NonBlockingEchoServer异常!" + e.getMessage());
			}
			Set<SelectionKey> readyKeys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = readyKeys.iterator();
			while (iterator.hasNext()) {
				SelectionKey key = iterator.next();
				iterator.remove();
				try {
					// 可连接
					if (key.isAcceptable()) {
						ServerSocketChannel server = (ServerSocketChannel) key.channel();
						SocketChannel socketChannel = server.accept();

						System.out.println("NonBlokingEchoServer接受客户端的连接：" + socketChannel);

						// 设置为非阻塞
						socketChannel.configureBlocking(false);

						// 客户端注册到Selector
						SelectionKey clientKey = socketChannel.register(selector,
								SelectionKey.OP_WRITE | SelectionKey.OP_READ);

						// 分配缓存区
						ByteBuffer buffer = ByteBuffer.allocate(100);
						clientKey.attach(buffer);
					}

					// 可读
					if (key.isReadable()) {
						SocketChannel client = (SocketChannel) key.channel();
						ByteBuffer buff = (ByteBuffer) key.attachment();
						client.read(buff);

						System.out.println(client.getRemoteAddress() 
								+ " -> NonBlokingEchoServer：" + buff.toString());

						key.interestOps(SelectionKey.OP_WRITE);
						// 打印1，会出现中文乱码
//						buff.flip();
//						System.out.print("打印文字：");
//						while(buff.hasRemaining()) {
//							System.out.print((char)buff.get());
//						}
						// 打印2，中文正常
//						buff.rewind();// 可以
						buff.flip();// 也可以
						System.out.println("打印文字：" + Charset.forName(encoding).decode(buff));// encoding: UTF-8

						// 打印3  不能用，要写入时指定编码，读出时就不用指定编码了
//						buff.flip();
//						System.out.println(buff.asCharBuffer());

					}

					// 可写
					if (key.isWritable()) {
						SocketChannel client = (SocketChannel) key.channel();
						ByteBuffer output = (ByteBuffer) key.attachment();
						output.flip();
						client.write(output);

						System.out.println("NonBlokingEchoServer  -> " 
								+ client.getRemoteAddress() + "：" + output.toString());

						output.compact();

						key.interestOps(SelectionKey.OP_READ);
					}
				} catch (IOException ex) {
					key.cancel();
					try {
						key.channel().close();
					} catch (IOException cex) {
						System.out.println(
								"NonBlockingEchoServer异常!" + cex.getMessage());
					}
				}
			}
		}
	}

}

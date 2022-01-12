---
Netty ChannelOption.SO_BACKLOG配置
---
ChannelOption.SO_BACKLOG 对应的是tcp/ip协议，listen函数中的 backlog 参数，用来初始化服务端可连接队列。
函数：
// backlog指定了内核为此套接口排队的最大连接个数；
// 对于给定的监听套接口，内核要维护两个队列：未连接队列和已连接队列
// backlog 的值即为未连接队列和已连接队列的和
listen(int socketfd, int backlog)

backlog用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放
已完成握手的最大长度（三次握手并不等于连接成功？就是成功连接了。但是这里的临时可能还没有完成
三次握手，即syns queue中的连接）

内核维护的未连接队列和已连接队列是？
未连接队列和已连接队列：syns queue和accept queue
服务端处理客户端连接请求时顺序处理的，所以同一时间只能处理一个客户端连接，多个客户端来的时候，
服务端不能处理的客户端连接请求就放在队列中等待处理。
syns queue：保存一个SYN已经到达，但三次握手还没有完成的连接
    用于保存半保存状态的请求，其大小通过/proc/sys/net/ipv4/tcp_max_syn_backlog指定，一般默认
    是512，不过设置有效的前提是系统的syncookies功能被禁用。（todo ymx 啥功能？）
    互联网常见的TCP SYN FLOOD恶意DOS攻击方式就是建立大量的半连接状态的请求，然后丢弃，导致
    syns queue不能保存其他正常的请求。
accept queue：保存三次握手已完成，内核正等待进程执行accept的调用的连接、
    用于保存全连接状态的请求，其大小通过/proc/sys/net/core/somaxconn指定
    在使用listen函数时，内核会根据传入的backlog参数与系统参数somaxconn，取二者的最小值

注意：
    1. 如果未设置或所设置的值小于1，Java将使用默认值50
    2. 如果accept queue队列满了，server将发送一个ECONNREFUSED错误信息 Connection refused给client

backlog 设置注意点
    服务器TCP内核 内维护了两个队列，成为A(未连接队列)和(已连接队列)
    如果A+B的长度大于Backlog时，新的连接就会被TCP内核拒绝掉
    所以如果backlog过小，就可能出现Accept速度跟不上，A,B队列满了，就会导致客户端无法建立连接
    需要注意的是，backlog对程序的连接数没影响，但是影响的是还没有被Accept取出的连接
    
Netty应用
    在netty视线中，backlog默认通过NetUtil.SOMAXCONN指定
    也可以在服务器启动时，通过option方法自定义backlog的大小
    
TCP的连接状态（SYN,FIN,ACK,PSH,RST,URG）
1. SYN 表示建立连接
2. FIN 表示关闭连接
3. ACK 表示响应
4. PSH表示有DATA数据传输
5. 表示连接重置
TCP三次握手

client                                          server
                                                bind()
                                                listen()
connect()
SYN_SEND →→    
                        SYN →→
                                                SYN_RECV →→       syns_queue
                        ←←SYN+ACK
ESTABLISHED →→                                                            ↓↓
                        ACK →→
                                                ESTABLISHED         accept_queue
                                                                        ↓↓
                                                ACCEPT                  ←←
第一次握手：客户端向服务端发送SYN标志的包建立连接时，客户端发送syn=j到服务器，并进入SYN_SEND
            状态，等待服务器确认；
第二次握手：服务器端，向客户端发送SYN和ACK的包，服务器收到syn包，必须确认客户的SYN(ack=j+1),
            同时自己也发送一个SYN包(syn=k)，此时服务器进入SYN_RECV
第三次握手：客户端向服务器端，收到服务端发送的SYN和ACK包，确认正确后，给服务器发送ACK的包，
            客户端收到服务器的SYN+ACK包，向服务器发送确认包ACK(ack=k+1),此包发送完毕，客户端
            和服务器都进入ESTABLISHED状态，完成三次握手，客户端和服务器开始传送数据。
    





































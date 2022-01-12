---
childOption和option的问题
---
option 主要是针对boss线程组，child主要是针对worker线程组
option 主要是设置ServerChannel 的一些选项，而childOption主要是设置的ServerChannel的子Channel的选项，如果是
Bootstrap的话，只会有option而没有childOption，所以设置的客户端Channel的选项

服务端的option参数
server.group(boss,worker)//设置时间循环对象，前者用来处理accept事件，后者用于处理已经建立的连接的io
                //Server是NioServerSocketChannel 客户端是NioSocketChannel绑定了jdk NIO创建的ServerSocketChannel对象,
                //用它来建立新accept的连接
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,1024)//
                // 第2次握手服务端向客户端发送请求确认，同时把此连接放入队列A中，
                // 然后客户端接受到服务端返回的请求后，再次向服务端发送请求，表示准备完毕，此时服务端收到请求，把这个连接从队列A移动到队列B中，
                // 此时A+B的总数，不能超过SO_BACKLOG的数值，满了之后无法建立新的TCP连接,2次握手后和3次握手后的总数
                // 当服务端从队列B中按照FIFO的原则获取到连接并且建立连接[ServerSocket.accept()]后，B中对应的连接会被移除，这样A+B的数值就会变小
                //此参数对于程序的连接数没影响，会影响正在准备建立连接的握手。
                .option(ChannelOption.SO_KEEPALIVE,true)
                //启用心跳，双方TCP套接字建立连接后（即都进入ESTABLISHED状态），
                // 并且在两个小时左右上层没有任何数据传输的情况下，这套机制才会被激活，TCP会自动发送一个活动探测数据报文
                // 对应用层的程序而言没有什么用。可以通过应用层实现了解服务端或客户端状态，而决定是否继续维持该Socket
                .option(ChannelOption.TCP_NODELAY,true)
                //TCP协议中，TCP总是希望每次发送的数据足够大，避免网络中充满了小数据块。
                // Nagle算法就是为了尽可能的发送大数据快。
                // TCP_NODELAY就是控制是否启用Nagle算法。
                // 如果要求高实时性，有数据发送时就马上发送，就将该选项设置为true关闭Nagle算法；
                // 如果要减少发送次数减少网络交互，就设置为false等累积一定大小后再发送。默认为false。
                .option(ChannelOption.SO_REUSEADDR,true)//是否允许重复绑定端口，重复启动，会把端口从上一个使用者上抢过来
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,30000)//连接超时30000毫秒
                .option(ChannelOption.SO_TIMEOUT,5000)//输入流的read方法被阻塞时，接受数据的等待超时时间5000毫秒，抛出SocketException
                //child是在客户端连接connect之后处理的handler，不带child的是在客户端初始化时需要进行处理的
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)//缓冲池
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("decoder",new DotDecodeHandler());
                    }
                });


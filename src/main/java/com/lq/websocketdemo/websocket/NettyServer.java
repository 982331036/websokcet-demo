package com.lq.websocketdemo.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 *
 * @author lq
 * @date 2020/4/1 10:07
 */
@Component
@Slf4j
public class NettyServer {

    /**
     * webSocket 协议名
     */
    private static final String WEBSOCKET_PROTOCOL = "webSocket";

    /**
     * netty服务端口
     */
    @Value("${websocket.server.port}")
    private int port;

    /**
     * webSocket路径
     */
    @Value("${websocket.server.path}")
    private String webSocketPath;

    @Autowired
    private WebSocketHandler webSocketHandler;

    /**
     * bossGroup辅助客户端的tcp连接请求
     */
    private EventLoopGroup bossGroup;
    /**
     *  workGroup负责与客户端之前的读写操作
     */
    private EventLoopGroup wordGroup;


    private void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        wordGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        //bossGroup辅助客户端的tcp连接请求, workGroup负责与客户端之前的读写操作
        bootstrap.group(bossGroup,wordGroup);
        //设置NIO类型的channel
        bootstrap.channel(NioServerSocketChannel.class);
        //设置监听端口
        bootstrap.localAddress(new InetSocketAddress(port));
        //连接到达时会创建一个通道
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                log.info("initChannel执行");
                //流水线管理通道中的处理程序（handler）,用来处理业务
                //websocket 协议本身就是基于http 协议的，所以这边要使用http编码器
                ch.pipeline().addLast(new HttpServerCodec())
                        .addLast(new ObjectEncoder());

                ch.pipeline().addLast(new ChunkedWriteHandler());

                /**
                 * http数据传输过程中是分段的，HttpObjectAggregator可以将多个段聚合
                 * 这就是为什么，当浏览器发送大量数据时，就会发送多次http 请求
                 */
                ch.pipeline().addLast(new HttpObjectAggregator(8192));
                /**
                 * 1.对应websocket，他的数据是以帧（frame）的信息传递
                 * 2.浏览器请求时，ws://localhost:port/xxx 表示请求的uri
                 * 3.核心功能是将http 协议升级为ws协议，保持长连接
                 */
                ch.pipeline().addLast(new WebSocketServerProtocolHandler(webSocketPath,WEBSOCKET_PROTOCOL,true,65536*10));
                //自定义的handler,处理业务逻辑
                ch.pipeline().addLast(webSocketHandler);
            }
        });
        log.info("websocket服务启动完成，监听端口：{}",port);
        ChannelFuture sync = bootstrap.bind().sync();
        sync.channel().closeFuture().sync();
        log.info("websocket服务关闭");
    }

    /**
     * 释放资源
     * @throws InterruptedException
     */
    @PreDestroy
    public void destroy() throws InterruptedException {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully().sync();
        }
        if (wordGroup != null) {
            wordGroup.shutdownGracefully().sync();
        }
    }

    /**
     * 启动webSocket服务器
     */
    @PostConstruct
    public void init(){
        new Thread(() -> {
            try {
                log.info("websocket server starting...");
                start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"webSocketStart").start();
    }
}

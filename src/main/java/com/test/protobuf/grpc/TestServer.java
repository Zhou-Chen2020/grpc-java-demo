package com.test.protobuf.grpc;

import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @author 周陈
 * @Title: 服务端
 * @date 2020/3/3 21:36
 */
public class TestServer {
    private static Logger log = Logger.getLogger(TestServer.class);

    private static int port = 8080;
    private static io.grpc.Server server;

    /**
     * 　　* @Description: 服务端启方法
     * 　　* @author 周陈
     * 　　* @date 2020/3/3 23:37
     *
     */
    public void run() {
        StreamServiceImpl serviceImpl = new StreamServiceImpl();
        server = io.grpc.ServerBuilder.forPort(port).addService(serviceImpl).build();
        try {
            server.start();
            log.info("Server start success on port:" + port);
            server.awaitTermination();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                server.shutdown();
            }
        }
    }

    public static void main(String[] args) {
        TestServer server = new TestServer();
        //服务端启动
        server.run();
    }

}

package com.test.protobuf;

import com.test.protobuf.grpc.TestServer;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

/**
 * @author 周陈
 * @Title: 客户端测试类
 * @date 2020/3/3 22:05
 */
public class TestClient {
    private static Logger log = Logger.getLogger(TestServer.class);
    private static int port = 10006;
//    private static String url = "114.84.148.40";
    private static String url = "localhost";
    private static ManagedChannel channel;

    @Before
    public void builderManagedChannel(){
        //管理通道构建
         channel = ManagedChannelBuilder.forAddress(url, port).usePlaintext().build();
    }

    /**
     * 　　* @Description: 模拟 简单rpc
     * 　　* @author 周陈
     * 　　* @date 2020/3/3 22:24
     *
     */
    @Test
    public void testSimpleFun() {
        //创建一个阻塞Stub
        StreamServiceGrpc.StreamServiceBlockingStub stub = StreamServiceGrpc.newBlockingStub(channel);
        for (int i = 0; i < 5; i++) {
            Stream.RequestData requestData = Stream.RequestData.newBuilder().setRequest("hello world-"+i).build();
            Stream.ResponseData responseData = stub.simpleFun(requestData);
            log.info(responseData.getResponse());
        }
        channel.shutdown();
    }

    /**
     * 　　* @Description: 模拟 服务器端流式rpc
     * 　　* @author 周陈
     * 　　* @date 2020/3/3 22:24
     *
     */
    @Test
    public void testServerSideStreamFun() {
        Stream.RequestData requestData = Stream.RequestData.newBuilder().setRequest("hello world").build();
        StreamServiceGrpc.StreamServiceBlockingStub stub = StreamServiceGrpc.newBlockingStub(channel);
        Iterator<Stream.ResponseData> iterator = stub.serverSideStreamFun(requestData);
        while (iterator.hasNext()) {
            log.info(iterator.next().getResponse());
        }
        channel.shutdown();

    }

    /**
     * 　　* @Description: 模拟 客户端流式rpc
     * 　　* @author 周陈
     * 　　* @date 2020/3/3 22:23
     *
     */
    @Test
    public void testClientSideStreamFun() throws InterruptedException {
        //创建一个异步Stub
        StreamServiceGrpc.StreamServiceStub asyncStub = StreamServiceGrpc.newStub(channel);
        //服务器端响应回调
        StreamObserver<Stream.ResponseData> responseData = new StreamObserver<Stream.ResponseData>() {
            @Override
            public void onNext(Stream.ResponseData value) {
               
                log.info(value.getResponse());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                // 关闭channel
                channel.shutdown();
            }
        };

        StreamObserver<Stream.RequestData> requestData = asyncStub.clientSideStreamFun(responseData);
        for (int i = 0; i < 10; i++) {
            requestData.onNext(Stream.RequestData.newBuilder().setRequest("hello world-" + i).build());
        }
        requestData.onCompleted();
        // 由于是异步获得结果，所以sleep 5秒
        Thread.sleep(5000);
    }

    /**
     * 　　* @Description: 模拟 双向流式rpc
     * 　　* @author 周陈
     * 　　* @date 2020/3/3 22:24
     *
     */
    @Test
    public void testTwoWayStreamFun() throws InterruptedException {
        StreamServiceGrpc.StreamServiceStub asyncStub = StreamServiceGrpc.newStub(channel);
        StreamObserver<Stream.ResponseData> responseData = new StreamObserver<Stream.ResponseData>() {
            @Override
            public void onNext(Stream.ResponseData value) {
                log.info(value.getResponse());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                // 关闭channel
                channel.shutdown();
            }
        };

        StreamObserver<Stream.RequestData> requestData = asyncStub.twoWayStreamFun(responseData);
        for (int i = 0; i < 10; i++) {
            requestData.onNext(Stream.RequestData.newBuilder().setRequest("hello world-" + i).build());
        }
        requestData.onCompleted();
        // 由于是异步获得结果，所以sleep 5秒
        Thread.sleep(5000);
    }
}

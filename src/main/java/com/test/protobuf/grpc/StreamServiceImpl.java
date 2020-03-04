package com.test.protobuf.grpc;

import com.test.protobuf.Stream;
import com.test.protobuf.StreamServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.apache.log4j.*;

/**
 * @author 周陈
 * @Title: 服务端方法实现类
 * @date 2020/3/3 21:36
 */
public class StreamServiceImpl extends StreamServiceGrpc.StreamServiceImplBase {
    private static Logger log = Logger.getLogger(StreamServiceImpl.class);

    /**
     * 　　* @Description: 重写 简单rpc
     * 　　* @author 周陈
     * 　　* @date 2020/3/3 21:41
     */
    @Override
    public void simpleFun(Stream.RequestData request, StreamObserver<Stream.ResponseData> responseObserver) {
        log.info("请求简单rpc 参数：" + request.getRequest());
        responseObserver.onNext(Stream.ResponseData.newBuilder().setResponse("请求简单rpc 响应").build());
        responseObserver.onCompleted();
    }

    /**
     * 　　* @Description: 重写 服务器端流式rpc
     * 　　* @author 周陈
     * 　　* @date 2020/3/3 22:11
     *
     */
    @Override
    public void serverSideStreamFun(Stream.RequestData request, StreamObserver<Stream.ResponseData> responseObserver) {
        System.out.println("请求服务器端流式rpc 参数：" + request.getRequest());
        for (int i = 0; i < 5; i++) {
            responseObserver.onNext(Stream.ResponseData.newBuilder().setResponse("服务器端流式rpc 响应-" + i).build());
        }
        responseObserver.onCompleted();
    }

    /**
     * 　　* @Description: 重写 客户端流式rpc
     * 　　* @author 周陈
     * 　　* @date 2020/3/3 22:11
     *
     */
    @Override
    public StreamObserver<Stream.RequestData> clientSideStreamFun(StreamObserver<Stream.ResponseData> responseObserver) {
        //客户端请求回调
        return new StreamObserver<Stream.RequestData>() {

            @Override
            public void onNext(Stream.RequestData value) {
                log.info("请求客户端流式rpc 参数：" + value.getRequest());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                Stream.ResponseData responseData = Stream.ResponseData.newBuilder()
                        .setResponse("客户端流式rpc 响应").build();
                responseObserver.onNext(responseData);
                responseObserver.onCompleted();
            }

        };
    }

    /**
     * 　　* @Description:
     * 　　* @author 周陈  双向流式rpc
     * 　　* @date 2020/3/3 22:11
     *
     */
    @Override
    public StreamObserver<Stream.RequestData> twoWayStreamFun(StreamObserver<Stream.ResponseData> responseObserver) {
        return new StreamObserver<Stream.RequestData>() {

            @Override
            public void onNext(Stream.RequestData value) {
                log.info("请求双向流式rpc 参数：" + value.getRequest());
                responseObserver.onNext(Stream.ResponseData.newBuilder().setResponse("双向流式rpc 响应-" + value.getRequest()).build());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }

        };
    }
}

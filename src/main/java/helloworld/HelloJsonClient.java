package helloworld;

import static io.grpc.stub.ClientCalls.blockingUnaryCall;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.MethodDescriptor;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.AbstractStub;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.grpc.stub.ClientCalls.blockingUnaryCall;

public final class HelloJsonClient {
    private static final Logger logger = Logger.getLogger(HelloJsonClient.class.getName());

    private final ManagedChannel channel;
    private final HelloJsonStub blockingStub;

    /** Construct client connecting to HelloWorld server at {@code host:port}. */
    public HelloJsonClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext(true)
                .build();
        blockingStub = new HelloJsonStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /** Say hello to server. */
    public void greet(String name) {
        logger.info("Will try to greet " + name + " ...");
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try {
            response = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Greeting: " + response.getMessage());
    }

    /**
     * Greet server. If provided, the first element of {@code args} is the name to use in the
     * greeting.
     */

    static final class HelloJsonStub extends AbstractStub<HelloJsonStub> {

        static final MethodDescriptor<HelloRequest, HelloReply> METHOD_SAY_HELLO =
                GreeterGrpc.METHOD_SAY_HELLO
                        .toBuilder(
                                JsonMarshaller.jsonMarshaller(HelloRequest.getDefaultInstance()),
                                JsonMarshaller.jsonMarshaller(HelloReply.getDefaultInstance()))
                        .build();

        protected HelloJsonStub(Channel channel) {
            super(channel);
        }

        protected HelloJsonStub(Channel channel, CallOptions callOptions) {
            super(channel, callOptions);
        }

        @Override
        protected HelloJsonStub build(Channel channel, CallOptions callOptions) {
            return new HelloJsonStub(channel, callOptions);
        }

        public HelloReply sayHello(HelloRequest request) {
            return blockingUnaryCall(
                    getChannel(), METHOD_SAY_HELLO, getCallOptions(), request);
        }
    }
}



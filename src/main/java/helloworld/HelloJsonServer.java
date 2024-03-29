package helloworld;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static io.grpc.stub.ServerCalls.asyncUnaryCall;

public class HelloJsonServer {
    private static final Logger logger = Logger.getLogger(HelloJsonServer.class.getName());

    private Server server;
    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new GreeterImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    HelloJsonServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final HelloJsonServer server = new HelloJsonServer();
        server.start();
        server.blockUntilShutdown();
    }

    private static class GreeterImpl implements BindableService {

        private void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) throws Exception {
            HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
            KafkaProducerHello.runProducer("Hello " + req.getName());
        }

        @Override
        public ServerServiceDefinition bindService() {
            return io.grpc.ServerServiceDefinition
                    .builder(GreeterGrpc.getServiceDescriptor().getName())
                    .addMethod(HelloJsonClient.HelloJsonStub.METHOD_SAY_HELLO,
                            asyncUnaryCall(
                                    new ServerCalls.UnaryMethod<HelloRequest, HelloReply>() {
                                        @Override
                                        public void invoke(
                                                HelloRequest request, StreamObserver<HelloReply> responseObserver) {
                                            try {
                                                sayHello(request, responseObserver);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }))
                    .build();
        }
    }
}

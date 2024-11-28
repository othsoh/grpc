package org.sid.Server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.sid.Services.BankGrpcService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GrpcServer {
    Server server;

    private void start() throws IOException {
        int port = 9999;
        server = ServerBuilder.forPort(port)
                        .addService(new BankGrpcService())
                                .build()
                                        .start();

        System.out.println("Server started at " + port);
    }


    private void stop() throws InterruptedException {
        if (server != null){
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
        System.out.println("Server Shut Down");
    }
    private void blockUnitShutDown() throws InterruptedException {
        if (server != null){
            server.awaitTermination();
        }
        System.out.println("Server Shut Down Imm");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final GrpcServer server1 = new GrpcServer();
        server1.start();
        server1.blockUnitShutDown();
    }
}

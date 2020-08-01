package ru.ifmo.rain.yarlychenko.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Nikolay Yarlychenko
 */
public class HelloUDPServer implements HelloServer {
    private static final int TIMEOUT = 500;
    ExecutorService executors;
    DatagramSocket socket;

    @Override
    public void start(int port, int threads) {
        try {
            socket = new DatagramSocket(port);
            executors = Executors.newFixedThreadPool(threads);
            for (int i = 0; i < threads; ++i) {
                executors.submit(() -> {
                    while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
                        try {
                            DatagramPacket receivePacket = Utils.getReceivePacket(socket);
                            socket.receive(receivePacket);
                            Utils.setPacketData(receivePacket);
                            socket.send(receivePacket);
                        } catch (IOException ignored) {
                        }
                    }
                });
            }
        } catch (SocketException e) {
            System.out.println("Couldn't create socket with current port");
        }
    }

    @Override
    public void close() {
        socket.close();
        executors.shutdown();
        try {
            executors.awaitTermination(TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }
    }

    public static void main(String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.err.println("Wrong arguments, should be 2 integer numbers");
            return;
        }

        try (HelloServer server = new HelloUDPServer()) {
            server.start(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        } catch (NumberFormatException e) {
            System.err.println("Wrong arguments, should be 2 integer numbers");
        }
    }
}

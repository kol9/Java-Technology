package ru.ifmo.rain.yarlychenko.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Nikolay Yarlychenko
 */
public class HelloUDPClient implements HelloClient {
    private static final int TIMEOUT = 500;

    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        InetAddress address;
        try {
            address = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            System.err.println("Wrong host name " + e.getMessage());
            return;
        }
        SocketAddress socketAddress = new InetSocketAddress(address, port);
        ExecutorService executors = Executors.newFixedThreadPool(threads);
        for (int id = 0; id < threads; ++id) {
            int finalId = id;
            executors.submit(() -> {
                executorTask(socketAddress, finalId, prefix, requests);
            });
        }
        executors.shutdown();
        try {
            executors.awaitTermination(threads * requests, TimeUnit.MINUTES);
        } catch (InterruptedException ignored) {
        }
    }

    private void executorTask(SocketAddress socketAddress, int id, String prefix, int requests) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(TIMEOUT);
            DatagramPacket receivePacket = Utils.getReceivePacket(socket);
            for (int i = 0; i < requests; ++i) {
                String request = requestText(prefix, id, i);
                DatagramPacket sendPacket = Utils.getSendPacket(request, socketAddress);
                while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
                    try {
                        socket.send(sendPacket);
                        System.out.println("SENT: " + request);
                        socket.receive(receivePacket);
                        String text = Utils.getPacketData(receivePacket);
                        if (Utils.checkMessage(text, request)) {
                            System.out.println("RECEIVED: " + text);
                            break;
                        }
                    } catch (IOException e) {
                        System.err.println("Error in sending/receiving data" + e.getMessage());
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("Error in socket");
        }
    }

    String requestText(String prefix, int id, int request) {
        return prefix + id + "_" + request;
    }

    public static void main(String[] args) {
        if (args == null || args.length != 5) {
            return;
        }

        HelloUDPClient client = new HelloUDPClient();
        try {
            client.run(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
        } catch (NumberFormatException e) {
            System.err.println("Wrong arguments, should be \"host, port, prefix, threads, requests\"");
        }
    }
}

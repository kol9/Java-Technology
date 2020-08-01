package ru.ifmo.rain.yarlychenko.hello;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

/**
 * @author Nikolay Yarlychenko
 */
public class Utils {
    static DatagramPacket getSendPacket(String data, SocketAddress address) {
        return new DatagramPacket(data.getBytes(StandardCharsets.UTF_8), data.getBytes().length, address);
    }

    static DatagramPacket getReceivePacket(DatagramSocket socket) throws SocketException {
        return new DatagramPacket(new byte[socket.getReceiveBufferSize()], socket.getReceiveBufferSize());
    }

    static String getPacketData(DatagramPacket packet) {
        return new String(packet.getData(),
                packet.getOffset(),
                packet.getLength());
    }

    static boolean checkMessage(String received, String sent) {
        return received.contains("Hello, " + sent);
    }

    static void setPacketData(DatagramPacket packet) {
        packet.setData(("Hello, " + new String(packet.getData(),
                packet.getOffset(),
                packet.getLength())).getBytes(StandardCharsets.UTF_8));
    }
}

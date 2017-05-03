package com.zm.utils;

import java.io.IOException;
import java.net.*;

/**
 * Created by zhangmin on 2017/5/3.
 */
public class UDPClient {
    private static final int TIMEOUT = 1000;
    private static final int MAXNUM = 5;

    public static byte[] sendAndRec (String host, int port, byte[] data){
        byte[] buf = new byte[1024];
        DatagramPacket recPacket = new DatagramPacket(buf, buf.length);
        int tries = 0;
        boolean received = false;
        try {
            DatagramSocket client = new DatagramSocket();
            client.setSoTimeout(TIMEOUT);
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(host), port);

            while (tries < 5 && !received) {
                try {
                    client.send(sendPacket);
                    client.receive(recPacket);
                    received = true;
                } catch (IOException e) {
                    tries ++;
                }
            }
        } catch (Exception e) {
            return null;
        }
        if(received) {
            return BU.subByte(recPacket.getData(), 0, recPacket.getLength());
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(BU.bytes2HexGoodLook(UDPClient.sendAndRec("192.168.202.81", 9999, "hello".getBytes())));
    }
}

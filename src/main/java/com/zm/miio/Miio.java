package com.zm.miio;

import com.google.gson.Gson;
import com.zm.utils.UDPClient;

/**
 * Created by zhangmin on 2017/5/3.
 */
public class Miio {
    private String host = "";
    private int port = 54321;
    public static int id = 0;

    MiioPacket helloedPacket = new MiioPacket();

    public Miio(String host) {
        this.host = host;
        hello();
    }

    private void hello() {
        byte[] rec = UDPClient.sendAndRec(host, port, MiioPacket.hellohelloPacket());
        helloedPacket.decode(rec);
    }

    public void turnOn() {
        Gson gson = new Gson();
        MiioPacket turnOnPacket = new MiioPacket(helloedPacket);
        turnOnPacket.data = gson.toJson(new TurnOn(id ++));
        byte[] rec = UDPClient.sendAndRec(host, port, turnOnPacket.encode());
        MiioPacket recPacket = new MiioPacket();
        recPacket.decode(rec);
        Replay replay = gson.fromJson(recPacket.data, Replay.class);
        //TODO
        System.out.println(replay);
    }

    public void turnOff() {
        //TODO
    }
}

class TurnOn {
    int id;
    String method = "set_power";
    String[] params = {"on"};

    public TurnOn(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }
}

class TurnOff {
    int id;
    String method = "set_power";
    String[] params = {"off"};

    public TurnOff(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }
}

class Replay {
    String[] result = new String[]{};
    int id = 0;

    public String[] getResult() {
        return result;
    }

    public void setResult(String[] result) {
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
package com.zm.miio;

import com.google.gson.Gson;
import com.zm.utils.BU;
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
        command(new Gson().toJson(new TurnOn(++ id)));
    }


    public void turnOff() {
        command(new Gson().toJson(new TurnOff(++ id)));
    }

    public void getProperty() {
        command(new Gson().toJson(new GetProperty(++ id)));
    }

    private void command(String json) {
        MiioPacket turnOnPacket = new MiioPacket(helloedPacket);
        turnOnPacket.data = json;
        byte[] rec = UDPClient.sendAndRec(host, port, turnOnPacket.encode());
        if(rec != null) {
            MiioPacket recPacket = new MiioPacket(helloedPacket);
            recPacket.decode(rec);
            System.out.println(recPacket.data);
        }
    }

    public static void main(String[] args) {
        Miio miio = new Miio("192.168.31.181");
        /*miio.turnOn();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        miio.turnOff();*/
        //Miio miio = new Miio("192.168.31.181");
        miio.getProperty();
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

class GetProperty {
    int id;
    String method = "get_prop";
    String[] params = {"power", "temp_dec", "humidity", "aqi"};

    public GetProperty(int id) {
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
    int id = 0;
    String[] result = new String[]{};

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
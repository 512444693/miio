package com.zm.miio;

import com.zm.utils.BU;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by zhangmin on 2017/5/3.
 */
public class MiioPacket {
    public byte[] magic = BU.short2Bytes((short) 0x2131);
    public short len = 0;
    public byte[] unknown_id_stamp = new byte[12];
    public byte[] md5sum = new byte[16];
    public String data = "";
    private byte[] encryptedData;

    public byte[] token;
    public byte[] key;
    public byte[] iv;

    public MiioPacket() {
    }

    public MiioPacket(MiioPacket helloedPacket) {
        unknown_id_stamp = helloedPacket.unknown_id_stamp;
        this.md5sum = helloedPacket.token;
        this.key = helloedPacket.key;
        this.iv = helloedPacket.iv;
    }

    public static byte[] hellohelloPacket() {
        return BU.hex2Bytes("21310020ffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
    }

    public byte[] encode() {
        encrypt();
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        len =(short) (magic.length + 2 + unknown_id_stamp.length + md5sum.length + encryptedData.length);
        md5sum();
        try {
            ba.write(magic);
            ba.write(BU.short2Bytes(len));
            ba.write(unknown_id_stamp);
            ba.write(md5sum);
            ba.write(encryptedData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ba.toByteArray();
    }

    public void decode(byte[] packetData) {
        int index = 2;
        this.len = BU.bytes2Short(BU.subByte(packetData, index, 2));
        index += 2;
        this.unknown_id_stamp = BU.subByte(packetData, index, unknown_id_stamp.length);
        index += unknown_id_stamp.length;
        this.md5sum = BU.subByte(packetData, index, md5sum.length);
        index += md5sum.length;

        if (this.len == 0x20) { // replay from hello
            this.token = md5sum;
            initKey(token);
        } else {
            encryptedData = BU.subByte(packetData, index, packetData.length - 0x20);
            decrypt();
        }
    }

    private void initKey(byte[] token) {
        key = MD5(token);
        iv = MD5(BU.bytesMerger(key, token));
    }

    private void md5sum() {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        try {
            ba.write(magic);
            ba.write(BU.short2Bytes(len));
            ba.write(unknown_id_stamp);
            ba.write(md5sum);
            ba.write(encryptedData);
            this.md5sum = MD5(ba.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] MD5(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void encrypt() {
        try{
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(cipher.ENCRYPT_MODE, keySpec, ivSpec);
            encryptedData = cipher.doFinal(data.getBytes());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void decrypt() {
        try{
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(cipher.DECRYPT_MODE, keySpec, ivSpec);
            this.data = new String(cipher.doFinal(encryptedData), "UTF-8");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("magic :" + BU.bytes2Hex(magic) + "\r\n");
        sb.append("len :" + BU.bytes2Hex(BU.short2Bytes(len)) + "\r\n");
        sb.append("unknown_id_stamp :" + BU.bytes2Hex(unknown_id_stamp) + "\r\n");
        sb.append("md5sum :" + BU.bytes2Hex(md5sum) + "\r\n");
        if (encryptedData != null) {
            sb.append("data :" + BU.bytes2Hex(encryptedData) + "\r\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        byte[] data = BU.hex2Bytes("213100500000000000c0c904000479703376c311e8894a4e27ccbb705e132fdef1623747e3fa895b0628c0517e32eed5499b6abc0da40c0b9cc14beecb57478921b03b27e886e6b49fcd0500d45077ee");
        System.out.println(BU.bytes2Hex(new MiioPacket().MD5(data)));
    }
}

package com.zm.miio;


/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        if (args.length != 2) {
            System.out.println("Usage: ip command");
            return;
        }
        Miio miio = new Miio(args[0]);
        switch (args[1]) {
            case "on":
                miio.turnOn();
                break;
            case "off":
                miio.turnOff();
                break;
            case "prop":
                miio.getProperty();
                break;
            default:
                System.out.println(args[1] + " not support");
        }
    }
}

package com.rag.chat.service.impl;

import java.util.*; // LOW: wildcard import
import java.io.*;

public class badServiceCopy { // MID: not PascalCase, LOW: filename contains "Copy"

    private String password = "super_secret"; // HIGH: hardcoded credential
    private String apiKey = "12345-ABCDE"; // HIGH: hardcoded credential

    // LOW: numbered method name
    public void processData2(String a, String b, String c, String d, String e, String f) { // MID: too many params (>5)

        System.out.println("Starting process"); // MID: System.out usage

        try {
            Thread.sleep(1000); // HIGH: Thread.sleep in non-test code

            String query = "SELECT * FROM users WHERE name = '" + a + "'"; // HIGH: SQL injection

            FileInputStream fis = new FileInputStream("file.txt"); // HIGH: resource leak (no try-with-resources)
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // MID: System.out usage
            }

            Date date = new Date(); // MID: prefer java.time

        } catch (Exception e) { // HIGH: broad exception
            e.printStackTrace(); // HIGH: printStackTrace instead of logger
            // MID: missing proper structured logging
        }

        // LOW: TODO
        // TODO: refactor this method

        longMethod(); // MID: method length trigger (see below)
    }

    // HIGH: empty catch block
    public void silentFailure() {
        try {
            int x = 10 / 0;
        } catch (Exception e) {
            // intentionally empty
        }
    }

    // HIGH: raw generics
    public List getList() {
        List list = new ArrayList();
        list.add("test");
        return list;
    }

    // MID: class naming + method length heuristic
    public void longMethod() {

        int sum = 0;

        // artificially long method (>50 lines)
        for (int i = 0; i < 60; i++) {
            sum += i;
            System.err.println(i); // MID: System.err usage
        }

        // filler lines to exceed 50 lines
        int a1=1; int a2=2; int a3=3; int a4=4; int a5=5;
        int a6=6; int a7=7; int a8=8; int a9=9; int a10=10;
        int a11=11; int a12=12; int a13=13; int a14=14; int a15=15;
        int a16=16; int a17=17; int a18=18; int a19=19; int a20=20;
        int a21=21; int a22=22; int a23=23; int a24=24; int a25=25;
        int a26=26; int a27=27; int a28=28; int a29=29; int a30=30;
        int a31=31; int a32=32; int a33=33; int a34=34; int a35=35;
        int a36=36; int a37=37; int a38=38; int a39=39; int a40=40;
        int a41=41; int a42=42; int a43=43; int a44=44; int a45=45;
        int a46=46; int a47=47; int a48=48; int a49=49; int a50=50;

    }

    // LOW: missing Javadoc (public method in service-like class)
    public String getData(String input) {
        return "Hello " + input;
    }
}

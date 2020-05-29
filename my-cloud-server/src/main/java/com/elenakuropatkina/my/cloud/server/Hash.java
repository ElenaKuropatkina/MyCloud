package com.elenakuropatkina.my.cloud.server;

public class Hash {
    private static int hash;
    private static String pass;
    
    public static void main(String[] args) {
        pass = "pass3";
        System.out.println(hash = pass.hashCode());
    }
            

}

package com.bossien.adminservice.util;

public class CaesarCipher {
    private static int increment = 5;//增量
    private static int decrement = 21;//减量

    public static char encrypt(char b) {
        if (b >= 'a' && b <= 'u') {
            return (char)(b+increment);
        } else if (b >= 'v' && b <= 'z') {
            return (char)(b-decrement);
        } else if (b >= 'A' && b <= 'U') {
            return (char)(b+increment);
        } else if (b >= 'V' && b <= 'Z') {
            return (char)(b-decrement);
        } else {
            return b;
        }
    }

    public static char decrypt(char b) {
        if (b >= 'a' && b <= 'e') {
            return (char) (b + decrement);
        } else if (b >= 'f' && b <= 'z') {
            return (char) (b - increment);
        } else if (b >= 'A' && b <= 'E') {
            return (char) (b + decrement);
        } else if (b >= 'F' && b <= 'Z') {
            return (char) (b - increment);
        } else {
            return b;
        }
    }

    public static String encrypt(String input){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < input.length(); i++){
            char c = input.charAt(i);
            sb.append(encrypt(c));
        }
        return sb.toString();
    }

    public static String decrypt(String input){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < input.length(); i++){
            char c = input.charAt(i);
            sb.append(decrypt(c));
        }
        return sb.toString();
    }
}

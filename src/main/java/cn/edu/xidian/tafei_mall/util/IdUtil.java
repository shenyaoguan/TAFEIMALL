package cn.edu.xidian.tafei_mall.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class IdUtil {

    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRandomString(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(index));
        }
        return stringBuilder.toString();
    }


    public static String generateRandomId(){
        String s1 = generateRandomString(8);
        String s2 = generateRandomString(4);
        String s3 = generateRandomString(4);
        String s4 = generateRandomString(4);
        String s5 = generateRandomString(12);
        return s1 + "-" + s2 + "-" + s3 + "-" + s4 + "-" + s5;
    }

}

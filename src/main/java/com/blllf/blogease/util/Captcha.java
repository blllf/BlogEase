package com.blllf.blogease.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/*
* 验证码
* */
public class Captcha {

    public static String genCode(){

        ArrayList<Object> resultList = new ArrayList<>();

        //创建一个集合存方 a-z A-Z
        ArrayList<Character> list = new ArrayList<>();

        for (int i = 0; i < 26; i++) {
            list.add((char)('a' + i));
            list.add((char)('A' + i));
        }

        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            int index = random.nextInt(list.size());
            Character c = list.get(index);
            resultList.add(c);
        }

        // 三个数字
        for (int i = 0; i < 3; i++) {
            int num = random.nextInt(10);
            resultList.add(num);
        }

        Collections.shuffle(resultList);
        String result = "";
        for (int i = 0; i < resultList.size(); i++) {
            Object str =  resultList.get(i);
            result = result + str;
        }

        System.out.println(result);

        return result;

    }

}

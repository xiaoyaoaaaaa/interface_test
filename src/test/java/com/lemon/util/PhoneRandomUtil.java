package com.lemon.util;

import java.util.Random;

public class PhoneRandomUtil {
    public static void main(String[] args) {
        //方式一：先查询手机号字段，倒序排列，取得最大值+1
        //方式二：先去生成一个随机的手机号，查询数据库，如果有记录，再生成一个去对比，直到没有号码被注册
        System.out.println(getUnregisterPhone());
    }

    public static String getRandomPhone(){
        Random random =new Random();
        String phonePrefix="133";
        for (int i = 0; i < 8; i++) {
            int num = random.nextInt(9);
            phonePrefix = phonePrefix + num;
        }
        return phonePrefix;
    }

    public static String getUnregisterPhone(){
        String phone="";
        while (true){
            phone = getRandomPhone();
            Object result = JDBCUtils.querySingleData("select count(*) from member where mobile_phone=" + phone);
            System.out.println(result);
            if ((Long)result==0){

                break;
            }else{//这个else可以不写
                continue;
            }
        }
        return phone;
    }
}
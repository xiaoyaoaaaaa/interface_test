package com.test.day01;
/**
 * register：注册表，登记表
 *
 * */
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class C_HomeWork {
    //全局变量
    String mobilephone="13323987111";
    String pwd="123456";
    int type=1;
    int memberId;
    String token;

    @Test
    public void testRegister(){
        String json="拼接，后续会放map里面去";
        Response res=
                given().
                        body(json).
                        header("Content-Type","application/json").
                        header("X-Lemonban-Media-Type","lemonban.v2").
                when().
                        post("http://api.lemonban.com/futureloan/member/register").
                then().
                        log().all().
                        extract().response();


    }

    //登录
    @Test(dependsOnMethods = "testRegister")
    public void testLogin(){
        String json="{\"mobile_phone\":\""+mobilephone+"\",\"pwd\":"+pwd+"}";
        Response response1=
                given().
                        body(json).
                        header("Content-Type","application/json").
                        header("X-Lemonban-Media-Type","lemonban.v2").
                when().
                        post("http://api.lemonban.com/futureloan/member/login").
                then().
                        extract().response();
        //获取id
        memberId = response1.jsonPath().get("data.id");
        System.out.println(memberId);
        //获取token
        token = response1.jsonPath().get("data.token_info.token");
        System.out.println(token);
    }

    //充值
    @Test(dependsOnMethods = "testLogin")
    public void testRecharge(){
        //发起充值的接口请求
        String jsonData2="{\"member_id\":"+memberId+",\"amount\":100000.00}";
        Response response2=
                given().
                        body(jsonData2).
                        header("Content-Type","application/json").
                        header("X-Lemonban-Media-Type","lemonban.v2").
                        //上面的参数化在此处用
                        header("Authorization","Bearer "+token).
                when().
                        post("http://api.lemonban.com/futureloan/member/login").
                then().
                        extract().response();
        System.out.println("当前余额："+response2.jsonPath().get("data.leave_amount"));
    }

}

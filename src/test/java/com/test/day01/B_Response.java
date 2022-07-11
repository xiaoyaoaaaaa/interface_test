package com.test.day01;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;

public class B_Response {

    //得到响应头信息
    @Test
    public void getResponseHeader(){
        Response response=
        given().

        when().
                post("https://www.httpbin.org/post").
        then().
                log().all().extract().response();
        System.out.println(response.time());
        System.out.println(response.header("Content-Type"));//根据键得到值
    }


    //得到响应体为json的body信息
    @Test
    public void getResponseJson01(){
        String jsonData="{\"mobilephone\":\"13323234545\",\"pwd\":\"123456\"}";
        Response response1=
                given().
                        //请求参数
                        body(jsonData).
                        //请求头
                        //或者contentType("application/json").
                        header("Content-Type","application/json").
                        header("X-Lemonban-Media-Type","lemonban.v1").
                when().
                        post("http://api.lemonban.com/futureloan/member/login").
                then().
                        log().all().extract().response();
        //GPath是一种路径表达式语言，类似与xpath，GPath不仅可以应用于XML，还可以应用于JSON、HTML
        response1.jsonPath().get("data.id");
        //System.out.println((char[]) response1.jsonPath().get("data"));
    }


    //得到响应体为json的复杂的信息
    @Test
    public void getResponseJson02(){
        Response res=
                given().

                when().
                        get("http://www.httpbin.org/json").
                then().
                        log().all().extract().response();
        //1.返回字符串
        Object o = res.jsonPath().get("slideshow.slides.title");
        System.out.println(o);
        //2.返回list集合
        List<String> list = res.jsonPath().getList("slideshow.slides.title");
        System.out.println(list);
        System.out.println(list.get(0));
        System.out.println(list.get(1));


    }


    //得到响应体为HTML
    @Test
    public void getResponseHtml03(){
        Response res=
                given().

                when().
                        get("http://www.baidu.com").
                then().
                        log().all().extract().response();
        Object o = res.htmlPath().get("html.head.title");
        System.out.println(o);
        //获取属性.@content---字符串
        Object o1 = res.htmlPath().get("html.head.meta.@content");
        System.out.println(o1);
        //获取属性.@content---集合
        List<Object> list = res.htmlPath().getList("html.head.meta.@content");
       System.out.println(list);
    }



    //得到响应体为XML
    @Test
    public void getResponseXml04(){
        Response res=
                given().

                when().
                        get("http://www.httpbin.org/xml").
                then().
                        log().all().extract().response();
        Object o = res.xmlPath().get("slideshow.slide[1].title");
        Object o1 = res.xmlPath().get("slideshow.slide[1].@type");
        System.out.println("---------");
        System.out.println(o);
        System.out.println(o1);
    }


    //用Java得到postman那样的参数化
    @Test
    public void loginRecharge(){
        String jsonData="{\"mobilephone\":\"13323231111\",\"pwd\":\"12345678\"}";
        Response response1=
                given().
                        body(jsonData).
                        header("Content-Type","application/json").
                        header("X-Lemonban-Media-Type","lemonban.v2").
                when().
                        post("http://api.lemonban.com/futureloan/member/login").
                then().
                        extract().response();
        //获取id
        Object memberId = response1.jsonPath().get("data.id");
        System.out.println(memberId);
        //获取token
        Object token = response1.jsonPath().get("data.token_info.token");
        System.out.println(token);

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
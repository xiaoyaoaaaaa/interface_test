package com.test.day01;
/**
 *  repository:仓库，数据库   external:外部的  maven：内行，专家  extract:提取，取出
 *  0.maven是一个自动化构建java项目的工具，主要是用户管理jar包依赖
 *  1.绑定进线仓库下载速度很快：file-settings-maven-maven home path和下面两个值
 *  2.将这个代码复制到pom.xml中，就是把依赖引入进来，maven的pom.xml添加rest-assured依赖坐标
         <dependencies>
            <!-- https://mvnrepository.com/artifact/io.rest-assured/rest-assured -->
            <dependency>
                 <groupId>io.rest-assured</groupId>
                 <artifactId>rest-assured</artifactId>
                 <version>4.3.0</version>
                 <scope>test</scope>
            </dependency>

         </dependencies>
     3.given when then
     4.引入testNG

     5.post请求传参的四种方式（get请求不需要传参，比较简单）
     6.获取响应：extract().response()，请看第二个class
 * */
import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.given;

public class A_RestAssuredDemo {


    //1.基本请求
    @Test
    public void firstGetRequest(){
        given().
                //设置请求：请求头，请求体...

        when().
                //所要执行的操作（GET/POST请求）
                get("https://www.baidu.com").
        then().
                log().body();
                //请求之后要做什么事情
    }


    //2.get请求
    @Test
    public void getDemo01(){
        given().
                //设置请求：请求头，请求体...
                queryParam("mobilephone","13323234545").
                queryParam("pwd","123456").
        when().
                //所要执行的操作（GET/POST请求）
                get("https://www.httpbin.org/get").
        then().
                log().all();
                //请求之后要做什么事情
    }


    //3.post请求--01：表单传参类型
    @Test
    public void postDemo01(){
        given().
                //设置请求：请求头，请求体...，
                //识别到formParam，系统会自动添加请求头content-type:application/x-www-form-urlencoded，当然你可以再写上
                formParam("mobilephone","13323234545").
                formParam("pwd","123456").
                contentType("application/x-www-form-urlencoded").
        when().
                //所要执行的操作（GET/POST请求）
                post("https://www.httpbin.org/post").
        then().
                log().all();
        //请求之后要做什么事情
    }


    //3.post请求--02：json传参类型
    @Test
    public void postDemo02(){
        String jsonData="{\"mobilephone\":\"13323234545\",\"pwd\":\"123456\"}";
        given().
                //json数据是放在body里面
                body(jsonData).
                header("Content-Type","application/json").
                //contentType("application/json").
        when().
                //所要执行的操作（GET/POST请求）
                post("https://www.httpbin.org/post").
        then().
                log().all();
        //请求之后要做什么事情
    }


    //3.post请求--03：xml传参类型
    @Test
    public void postDemo03(){
        String xmlData="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<suite>\n"+
                "<class>测试xml</class>\n"+
                "</suite>";
        given().
                //xml数据是放在body里面
                body(xmlData).
                header("Content-Type","application/xml").
        when().
                //所要执行的操作（GET/POST请求）
                post("https://www.httpbin.org/post").
        then().
                log().body();
        //请求之后要做什么事情
    }


    //3.post请求--04：多参数表单，上传文件:multiPart方法
    @Test
    public void postDemo04(){

        given().
                multiPart(new File("C:\\Users\\包子\\Desktop\\1.txt")).
        when().
                //所要执行的操作（GET/POST请求）
                 post("https://www.httpbin.org/post").
        then().
                log().body();
        //请求之后要做什么事情
    }

}

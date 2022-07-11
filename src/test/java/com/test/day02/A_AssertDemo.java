package com.test.day02;

import io.restassured.RestAssured;
import io.restassured.config.XmlConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

public class A_AssertDemo {
    @Test
    public void testAssert(){
        //RestAssured全局配置，json小数返回类型是BIigDecimal
        RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        //BaseUrl全局配置
        RestAssured.baseURI="http://api.lemonban.com/futureloan";

        String jsonData="{\"mobilephone\":\"13323231111\",\"pwd\":\"12345678\"}";
        Response res=
                given().
                        //config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL))).
                        body(jsonData).
                        header("Content-Type","application/json").
                        header("X-Lemonban-Media-Type","lemonban.v2").
                when().
                        post("/member/login").
                then().
                        log().all().
                        extract().response();
        //1.响应结果断言
        int code=res.jsonPath().get("code");
        String msg = res.jsonPath().get("msg");
        Assert.assertEquals(code,1);
        //Assert.assertEquals(msg,"OK");

        //1.1注意：restassured里面如果返回json小数，那么返回的类型都是float，float存在精度丢失
        //1.2丢失精度问题解决方案：声明restassured返回json小数类型BigDecimal
        Object actual = res.jsonPath().get("data.leave_amount");
        //Assert.assertEquals(leaveAmount,10000.01);
        //1.3由于判断的时候小数的类型不一致，double和BigDecimal对比，也会出错，所以要把要传入的数值转换成BigDecimal
        BigDecimal expected = BigDecimal.valueOf(10000.01);
        Assert.assertEquals(actual,expected);



        //发起充值的接口请求
        int memberId = res.jsonPath().get("data.id");
        String token=res.jsonPath().get("data.token_info.token");
        String jsonData2="{\"member_id\":"+memberId+",\"amount\":10000.00}";

        Response response2=
                given().
                        //上面有全局配置，这里不用再写bigdecimal设置了
                        body(jsonData2).
                        header("Content-Type","application/json").
                        header("X-Lemonban-Media-Type","lemonban.v2").
                        //上面的参数化在此处用
                        header("Authorization","Bearer "+token).
                when().
                        post("/member/login").
                then().
                        extract().response();
        BigDecimal actual2=response2.jsonPath().get("data.leave_amount");
        BigDecimal expected2=BigDecimal.valueOf(20000.01);
        Assert.assertEquals(actual2,expected2);
    }
}

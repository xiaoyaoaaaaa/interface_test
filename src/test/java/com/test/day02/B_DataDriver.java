package com.test.day02;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSON;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
/**
 * provide:提供  annotation:注解，注释
 * */
public class B_DataDriver {
    
    @Test(dataProvider= "getLoginDatas2")
    public void testAssert(B_ExcelPojo excelPojo) {
        RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        RestAssured.baseURI = "http://api.lemonban.com/futureloan";
        //接口入参
        String inputParams = excelPojo.getInputParams();
        //接口地址
        String url = excelPojo.getUrl();
        //请求头
        String requestHeader = excelPojo.getRequestHeader();
        //把请求头转成map
        Map requestHeaderMap = (Map) JSON.parse(requestHeader);
        //期望的响应结果,转成map取判断
        String expected = excelPojo.getExpected();
        Map<String,Object> expectedMap = (Map) JSON.parse(expected);
        Response res =
                given().
                        //config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL))).
                        body(inputParams).
                        //headers里面的参数可以传入map类型的，避免了有多个header不好写的情况
                        headers(requestHeaderMap).

                when().
                        post(url).
                then().
                        log().all().
                        extract().response();
        //断言，循环变量响应map，取到里面的每一个key（实际就是我们设计的jsonPath表达式），然后在取到实际的结果去做对比
        Set<String> set = expectedMap.keySet();
        for(String key:set){
            //获取map里面的key
            System.out.println(key);
            //获取map里面的value,期望结果
            Object exceptValue = expectedMap.get(key);
            //获取接口返回的实际结果
            Object actualValue = res.jsonPath().get(key);
            Assert.assertEquals(exceptValue,actualValue);
        }


    }

    //数据驱动源，由DataProvider来声明必须返回一个object数组,一维数组，二维数组都可以
    @DataProvider
    public Object[] getLoginDatas2(){
        File file = new File("C:\\Users\\包子\\Desktop\\api_testcases_futureloan_v1.xlsx");
        //生成ImportParams对象，调用setStartSheetIndex()方法，定位那个sheet
        ImportParams importParams = new ImportParams();
        //参数1就代表第二个sheet
        importParams.setStartSheetIndex(1);
        //读取excel,三个参数，1为文件地址对象，2为文件属性class，3为上面设置的那个sheet对象
        List<B_ExcelPojo> listDatas = ExcelImportUtil.importExcel(file, B_ExcelPojo.class, importParams);
        for (Object excel:listDatas){
            System.out.println(excel);
        }
        //把集合转化为一维数组
        return listDatas.toArray();
    }


    //打印一下看看是啥
    public static void main(String[] args) {
        File file = new File("C:\\Users\\包子\\Desktop\\api_testcases_futureloan_v1.xlsx");
        //生成ImportParams对象，调用setStartSheetIndex()方法，定位那个sheet
        ImportParams importParams = new ImportParams();
        //参数1就代表第二个sheet
        importParams.setStartSheetIndex(1);
        //读取excel,三个参数，1为文件地址对象，2为文件属性class，3为上面设置的那个sheet对象
        List<B_ExcelPojo> listDatas = ExcelImportUtil.importExcel(file, B_ExcelPojo.class, importParams);
        for (B_ExcelPojo excel:listDatas){
            System.out.println(excel);
        }
    }
}

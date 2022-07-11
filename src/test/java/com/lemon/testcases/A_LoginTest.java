package com.lemon.testcases;

import com.alibaba.fastjson.JSONObject;
import com.lemon.common.A_BaseTest;
import com.lemon.data.A_Enviroment;
import com.lemon.data.Constant;
import com.lemon.pojo.A_ExcelPojo;
import com.lemon.util.PhoneRandomUtil;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;


import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
/**
 * Specify：明确指出，具体说明
 * */
public class A_LoginTest extends A_BaseTest {

    //前置条件，登陆之前的注册接口准备
    @BeforeClass
    public void setup(){

        //随机生成一个数据库没有的手机号复制到环境变量中
        String phone = PhoneRandomUtil.getUnregisterPhone();
        A_Enviroment.envData.put("phone",phone);


        List<A_ExcelPojo> listDatas = readSpecifyExcelData( 2, 1, 1);
        //因为上面手机号码是数据库对比出来的，还没有替换到第一列，现在替换
        A_ExcelPojo excelPojo = listDatas.get(0);
        //环境变量替换到第一行的{{phone}}
        excelPojo=casesReplace(excelPojo);
        Response request = request(excelPojo);
        extractToEnvironment(excelPojo,request);
    }



    //
    @Test(dataProvider= "getLoginDatas")
    public void testLogin(A_ExcelPojo excelPojo) {
        excelPojo = casesReplace(excelPojo);
        Response res = request(excelPojo);
        /**
        Map<String,Object> expectedMap = JSONObject.parseObject(excelPojo.getExpected(),Map.class);
        //断言，循环变量响应map，取到里面的每一个key（实际就是我们设计的jsonPath表达式），然后在取到实际的结果去做对比
        Set<String> set = expectedMap.keySet();
        for(String key:set){
            //获取map里面的key
            System.out.println(key);
            //获取map里面的value,期望结果
            Object exceptedValue = expectedMap.get(key);
            获取接口返回的实际结果
            Object actualValue = res.jsonPath().get(key);
            Object actualValue1 = null;

            actualValue1=actualValue;
            if(actualValue!=null && actualValue instanceof Float){
                BigDecimal bigDecimal1 = new BigDecimal(actualValue.toString());
                actualValue1=bigDecimal1;
            }else if(actualValue!=null && actualValue instanceof Double){
                BigDecimal bigDecimal1 = new BigDecimal(actualValue.toString());
                actualValue1=bigDecimal1;
            }
            Assert.assertEquals(exceptedValue,actualValue);
         */
        assertResponse(excelPojo,res);

    }

    //用例对应的数据，不包含前置条件,数据提供者
    @DataProvider
    public Object[] getLoginDatas(){
        //不写第四个参数则默认读到最后
        List<A_ExcelPojo> listDatas = readSpecifyExcelData( 2, 2);
        //把集合转化为一维数组
        return listDatas.toArray();
    }

}

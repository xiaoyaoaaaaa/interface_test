package com.lemon.testcases;

import com.alibaba.fastjson.JSONObject;
import com.lemon.common.A_BaseTest;
import com.lemon.data.A_Enviroment;
import com.lemon.pojo.A_ExcelPojo;
import com.lemon.util.JDBCUtils;
import com.lemon.util.PhoneRandomUtil;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

/**
 * Specify：明确指出，具体说明
 * 0.mvn找不到，去配置环境变量
 * 1.mvn clean
 * 2.mvn test
 * 3.mvn io.qameta.allure:allure-maven:serve
 * 以上三步会生成一个allure报表
 *
 * */
public class A_A_Register extends A_BaseTest {

    //前置条件，登陆之前的注册接口准备
    @BeforeClass
    public void setup(){
        //随机生成一个没有注册过的手机号码
        String phone1 = PhoneRandomUtil.getUnregisterPhone();
        String phone2 = PhoneRandomUtil.getUnregisterPhone();
        String phone3 = PhoneRandomUtil.getUnregisterPhone();
        //保存到环境变量当中去
        A_Enviroment.envData.put("phone1",phone1);
        A_Enviroment.envData.put("phone2",phone2);
        A_Enviroment.envData.put("phone3",phone3);

    }



    //
    @Test(dataProvider= "getRegisterDatas")
    public void testResgister(A_ExcelPojo excelPojo) throws FileNotFoundException {

        /**  封装到request里面去
        //1.每个接口生成日志-确定文件路径:全局重定向输出到指定文件中(通过REST-Assured过滤器实现)
        File file = new File(System.getProperty("user.dir") + "\\log");
        if (!file.exists()){
            file.mkdir();
        }
        //2.相对路径+id.log:测试用例日志单独保存(前提需要在REST-Assured请求和响应中添加log)
        String logFilePath=System.getProperty("user.dir")+"\\register_log\\test"+excelPojo.getCaseID()+".log";
        PrintStream fileOutPutStream =  new PrintStream(new File(logFilePath));
        RestAssured.config =
                RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(fileOutPutStream));
        */

        excelPojo = casesReplace(excelPojo);
        Response res = request(excelPojo);

        /**
         //3.Allure定制添加接口请求响应信息：再Allure的报表里面根据每一个接口生成一个对应的日志
        Allure.addAttachment("接口请求响应信息", new FileInputStream(logFilePath));
        */

        //响应断言
        assertResponse(excelPojo,res);
        //数据库断言
        assertSQL(excelPojo);

//        String dbAssert = excelPojo.getDbAssert();
//        if(dbAssert!=null){
//            Map<String,Object> map = JSONObject.parseObject(dbAssert, Map.class);
//            for(String key:map.keySet()) {
//                Integer exceptedValue = (Integer) map.get(key);
//                Object actuladValue = JDBCUtils.querySingleData(key);
//
//                System.out.println("exceptedValue:Integer " + exceptedValue + exceptedValue.getClass());
//                System.out.println("actuladValue: Long" + actuladValue + actuladValue.getClass());
//
//                long exceptedValue2 = exceptedValue.longValue();
//                Assert.assertEquals(actuladValue, exceptedValue2);
//            }
//        }

    }

    //用例对应的数据，不包含前置条件,数据提供者
    @DataProvider
    public Object[] getRegisterDatas(){
        //不写第四个参数则默认读到最后
        List<A_ExcelPojo> listDatas = readSpecifyExcelData( 1, 1);
        //把集合转化为一维数组
        return listDatas.toArray();
    }


//    //没有必要，但是这个方法可以这样用
//    @AfterTest
//    public void teardown(){
//        //你可以选择清空环境变量
//        A_Enviroment.envData.clear();
//    }

}

package com.lemon.testcases;

import com.alibaba.fastjson.JSONObject;
import com.lemon.common.A_BaseTest;
import com.lemon.data.A_Enviroment;
import com.lemon.data.Constant;
import com.lemon.pojo.A_ExcelPojo;
import com.lemon.util.JDBCUtils;
import com.lemon.util.PhoneRandomUtil;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.poi.hpsf.Decimal;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
/**
 * annotate:注释  report：报告
 * */
public class C_InvestFlowTest extends A_BaseTest {
    @BeforeClass
    public void setup(){

        //生成三个角色的手机号码（借款人+投资人+管理员）
        String borrowPhone = PhoneRandomUtil.getUnregisterPhone();
        String adminPhone = PhoneRandomUtil.getUnregisterPhone();
        String investPhone = PhoneRandomUtil.getUnregisterPhone();
        A_Enviroment.envData.put("borrower_phone",borrowPhone);
        A_Enviroment.envData.put("admin_phone",adminPhone);
        A_Enviroment.envData.put("invest_phone",investPhone);
        //读取用例数据第一条~第九条
        List<A_ExcelPojo> list = readSpecifyExcelData(4, 1, 9);

        for(int i=0;i<list.size();i++){
            A_ExcelPojo excelPojo = list.get(i);
            excelPojo = casesReplace(excelPojo);
            Response res = request(excelPojo);
            //判断是否要提取响应数据
            if(excelPojo.getExtract()!=null){
                extractToEnvironment(excelPojo,res);
            }
        }
    }

    @Test
    public void testInvest(){
        List<A_ExcelPojo> list = readSpecifyExcelData(4, 10);
        A_ExcelPojo excelPojo = list.get(0);
        excelPojo = casesReplace(excelPojo);
        Response res = request(excelPojo);
        //响应断言
        assertResponse(excelPojo,res);

        //数据库断言
        assertSQL(excelPojo);

    }

    @AfterTest
    public void teardown(){

    }
}

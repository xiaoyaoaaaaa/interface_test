package com.lemon.testcases;

import com.alibaba.fastjson.JSONObject;
import com.lemon.common.A_BaseTest;
import com.lemon.data.A_Enviroment;
import com.lemon.pojo.A_ExcelPojo;
import com.lemon.util.PhoneRandomUtil;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class D_AddloanTest extends A_BaseTest {
    @BeforeClass
    public void setup(){
        String borrowPhone = PhoneRandomUtil.getUnregisterPhone();
        String adminPhone = PhoneRandomUtil.getUnregisterPhone();
        A_Enviroment.envData.put("borrower_phone",borrowPhone);
        A_Enviroment.envData.put("admin_phone",adminPhone);
        //读取前面4条数据
        List<A_ExcelPojo> list = readSpecifyExcelData(4, 1, 4);
        for (int i = 0; i < list.size(); i++) {
            //发送请求
            A_ExcelPojo excelPojo = list.get(i);
            excelPojo = casesReplace(excelPojo);
            Response res = request(excelPojo);
            if(excelPojo.getExtract()!=null){
                extractToEnvironment(excelPojo,res);
            }
        }
    }

    @Test(dataProvider = "getAddLoanDatas")
    public void testAddLoan(A_ExcelPojo excelPojo){
        excelPojo=casesReplace(excelPojo);
        Response res = request(excelPojo);
        //断言
        assertResponse(excelPojo,res);
    }

    @DataProvider
    public Object[] getAddLoanDatas(){
        List<A_ExcelPojo> listDatas = readSpecifyExcelData(5, 5);
        return listDatas.toArray();
    }
}

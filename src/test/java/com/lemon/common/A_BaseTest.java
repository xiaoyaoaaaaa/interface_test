package com.lemon.common;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSONObject;
import com.lemon.data.A_Enviroment;
import com.lemon.data.Constant;
import com.lemon.pojo.A_ExcelPojo;
import com.lemon.util.JDBCUtils;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.apache.poi.hpsf.Decimal;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

/**
 * before suite
 * before test比class优先级更高
 * before class
 * before method
 *
 * */
public class A_BaseTest {
    @BeforeTest
    public void GlobalSetup() throws FileNotFoundException {
        //注意：restassured里面如果返回json小数，那么返回的类型都是float，float存在精度丢失
        //丢失精度问题解决方案：声明restassured返回json小数类型BigDecimal，RestAssured全局配置，json小数返回类型是BIigDecimal
        RestAssured.config=RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        RestAssured.baseURI = Constant.BASE_URI;

        //方式二：测试用例日志单独保存(前提需要在REST-Assured请求和响应中添加log),在每一个接口中生成变量保存
        /**
        PrintStream fileOutPutStream = new PrintStream(new File("log/testXX.log"));
        RestAssured.config =
                RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(fileOutPutStream));
        */

        /**
         //方式一：全局重定向输出到指定文件中(通过REST-Assured过滤器实现)---固定的一个文件里，不方便查找
        File file = new File(System.getProperty("user.dir") + "\\log");
        if (!file.exists()){
            file.mkdir();
        }
        PrintStream fileOutPutStream = new PrintStream(new File("log/test_all.log"));
        RestAssured.filters(new RequestLoggingFilter(fileOutPutStream),new
                ResponseLoggingFilter(fileOutPutStream));*/
    }

    //将请求封装成一个Response方法
    public Response request(A_ExcelPojo excelPojo){
        //判断一下是输出在控制台，还是输出到文件里面去
        String logFilePath;
        if (Constant.LOG_TO_FILE){
            File dirPath = new File(System.getProperty("user.dir")+"\\log\\"+excelPojo.getInterfaceName());
            if (!dirPath.exists()){
                //加个s可以创建多级目录
                dirPath.mkdirs();
            }
            logFilePath=dirPath+"\\test"+excelPojo.getCaseID()+".log";
            PrintStream fileOutPutStream = null;
            try {
                fileOutPutStream = new PrintStream(new File(logFilePath));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            RestAssured.config =RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(fileOutPutStream));
        }


        String url = excelPojo.getUrl();
        String method = excelPojo.getMethod();
        String headers = excelPojo.getRequestHeader();
        String params = excelPojo.getInputParams();
        //请求头转成Map,这是api规定这样，headers要求参数为map,而body要求参数为string
        Map<String,Object> headersMap= JSONObject.parseObject(headers, Map.class);
        //Map<String,Object> paramsMap= JSONObject.parseObject(params, Map.class);
        Response res=null;
        if ("get".equalsIgnoreCase(method)){
            res=given().log().all().headers(headersMap).when().get(url).then().log().all().extract().response();
        }else if ("post".equalsIgnoreCase(method)){
            res=given().log().all().headers(headersMap).body(params).when().post(url).then().log().all().extract().response();
        }else if ("patch".equalsIgnoreCase(method)){
            res=given().log().all().headers(headersMap).body(params).when().patch(url).then().log().all().extract().response();
        }

        //上面如果是输出到文件，我们这边才执行
        if(Constant.LOG_TO_FILE){
            try {
                Allure.addAttachment("接口请求响应信息", new FileInputStream(logFilePath));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }


        return res;
    }
    /**
     * 对响应结果进行封装的断言方法
     * */
    public void assertResponse(A_ExcelPojo excelPojo,Response res){
        Map<String,Object> expectedMap = JSONObject.parseObject(excelPojo.getExpected(),Map.class);
        if(expectedMap!=null){
            for(String key:expectedMap.keySet()){
                //获取map里面的key
                //System.out.println(key);
                //获取map里面的value,期望结果
                Object exceptedValue = expectedMap.get(key);
                //获取接口返回的实际结果
                Object actualValue = res.jsonPath().get(key);
                Assert.assertEquals(exceptedValue,actualValue);
            }
        }

    }




    //为了以后方便而封装方法,读取指定文件，指定sheet页
    public List<A_ExcelPojo> readAllExcelData( int sheetNum){
        File file = new File(Constant.EXCEL_FILE_PATH);
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(sheetNum-1);
        List<A_ExcelPojo> listDatas = ExcelImportUtil.importExcel(file, A_ExcelPojo.class, importParams);
        return listDatas;
    }
    //读取指定文件，指定sheet页,读取指定行
    public List<A_ExcelPojo> readSpecifyExcelData(int sheetNum,int startRow,int readRow){
        File file = new File(Constant.EXCEL_FILE_PATH);
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(sheetNum-1);
        importParams.setStartRows(startRow-1);
        importParams.setReadRows(readRow);
        List<A_ExcelPojo> listDatas = ExcelImportUtil.importExcel(file, A_ExcelPojo.class, importParams);
        return listDatas;
    }

    //读取指定文件，指定sheet页,读取到最后，方法重载
    public List<A_ExcelPojo> readSpecifyExcelData(int sheetNum,int startRow){
        File file = new File(Constant.EXCEL_FILE_PATH);
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(sheetNum-1);
        importParams.setStartRows(startRow-1);
        List<A_ExcelPojo> listDatas = ExcelImportUtil.importExcel(file, A_ExcelPojo.class, importParams);
        return listDatas;
    }


    //需要引用的值存到环境变量中：第一个参数为excel某一单元格的值提取到环境变量中，第二个参数是接口返回的Response对象
    public void extractToEnvironment(A_ExcelPojo excelPojo,Response res){
        Map<String,Object> extractMap = JSONObject.parseObject(excelPojo.getExtract(), Map.class);
        //再优化，循环遍历extractMap，key对应的就是环境变量中的key，Object value对一个的就是环境变量中的value
        for(String key:extractMap.keySet()){
            Object path=extractMap.get(key);
            //根据【提取返回数据】里面的路径表达式去提取实际接口对应返回字段的值，真实存到环境变量的具体值
            Object value =res.jsonPath().get(path.toString());
            //存到环境变量中
            A_Enviroment.envData.put(key,value);
        }
    }

    //封装的正则替换，从环境变量中取到的值进行正则替换，并返回替换之后的字符串（说白了就是把上个接口的返回值替换到现在这个接口的入参里面）
    public String regexReplace(String originalStr) {
        if(originalStr!=null) {
            //匹配的Java类Pattern:正则表达式匹配器,两次转义才可以把大括号转成普通的字符
            Pattern pattern = Pattern.compile("\\{\\{(.*?)}}");
            //得到一个匹配对象，有一个方法find(),他只会找一次，所以用循环找多次
            Matcher matcher = pattern.matcher(originalStr);
            //这个创建一个字符串对象，以备后面replace赋值
            String a = originalStr;
            while (matcher.find()) {
                //参数0表示获取到整个匹配内容，大括号也有：{{内容}}，参数1表示获取到大括号里面的内容，没有大括号：内容
                String outStr = matcher.group(0);
                String inStr = matcher.group(1);
                //因为有Map的环境变量了，直接环境变量得到replaceStr，不需要在传入第二个参数了
                Object replaceStr = A_Enviroment.envData.get(inStr);
                a = a.replace(outStr, replaceStr + "");
            }
            return a;
        }
        return originalStr;
    }


    public A_ExcelPojo casesReplace(A_ExcelPojo excelPojo){
        //正则替换-》参数
        String inputParams = regexReplace(excelPojo.getInputParams());
        excelPojo.setInputParams(inputParams);
        //正则替换-》请求头
        String requestHeader = regexReplace(excelPojo.getRequestHeader());
        excelPojo.setRequestHeader(requestHeader);
        //正则替换-》接口地址:比如地址里面需要传入具体的member_id
        String url = regexReplace(excelPojo.getUrl());
        excelPojo.setUrl(url);
        //正则替换-》期望的返回结果：比如说注册成功的一个号码，期望值也应该返回这个号码
        String excepted = regexReplace(excelPojo.getExpected());
        excelPojo.setExpected(excepted);
        //正则替换-》数据库
        String dbAssert = regexReplace(excelPojo.getDbAssert());
        excelPojo.setDbAssert(dbAssert);
        return excelPojo;
    }


        /**
         *数据库断言
         *
         * */
    public void assertSQL(A_ExcelPojo excelPojo){
        String dbAssert = excelPojo.getDbAssert();
        if(dbAssert!=null){
            Map<String,Object> map = JSONObject.parseObject(dbAssert, Map.class);
            for(String key:map.keySet()) {
                Object exceptedValue =map.get(key);

                if(exceptedValue instanceof Decimal ||exceptedValue instanceof Float || exceptedValue instanceof Double){
                    BigDecimal exceptedValue1 = (BigDecimal) exceptedValue;
                    Object actualValue = JDBCUtils.querySingleData(key);
                    BigDecimal actualValue1=(BigDecimal) actualValue;
                    Assert.assertEquals(exceptedValue1,actualValue1);

                }else if (exceptedValue instanceof Integer || exceptedValue instanceof Long){
                    long exceptedValue1 =((Integer)exceptedValue).longValue();
                    Object actualValue = JDBCUtils.querySingleData(key);
                    long actualValue1=(Long) actualValue;
                    Assert.assertEquals(exceptedValue1, actualValue1);
                }else {
                    Object actualValue = JDBCUtils.querySingleData(key);
                    Assert.assertEquals(exceptedValue,actualValue);
                }
            }
        }
    }

}

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
/**
 * pattern：模式，方式
 * compile：编译，汇编
 * */
public class B_RechargeTest extends A_BaseTest {

    //前置条件，读取Excel里面的前两条数据
    @BeforeClass
    public void setup(){

        //随机生成一个数据库没有的手机号复制到环境变量中
        String phone = PhoneRandomUtil.getUnregisterPhone();
        A_Enviroment.envData.put("phone",phone);

        List<A_ExcelPojo> listDatas = readSpecifyExcelData( 3, 1, 2);

        A_ExcelPojo excelPojo = listDatas.get(0);
        excelPojo = casesReplace(excelPojo);
        Response resRegister = request(excelPojo);
        extractToEnvironment(excelPojo,resRegister);


        //封装参数替换
        casesReplace(listDatas.get(1));
        //发起登录请求
        Response resLogin = request(listDatas.get(1));
        //为了以后方便更改路径，我们可以直接在excel里面做更改
        extractToEnvironment(listDatas.get(1),resLogin);

        /**
        //方式一:这里是普通的环境变量赋值方法
                Map<String,Object> extractMap = JSONObject.parseObject(extract, Map.class);
                Object memberIdPath = extractMap.get("member_id");
                memberId = resLogin.jsonPath().get(memberIdPath.toString());
                //这个以后要用，要设置成全局变量，上面首先生成全局变量，这边赋值,然后存到环境变量中去
                //memberId = resLogin.jsonPath().get("data.id");
                A_Enviroment.memberId=memberId;

                    上面的接口返回的"data.id"路径可能会发生变化，我们为了好维护代码，
                    所以在excel中在生成一列，直接写上这个路径，以后就算路径发生改变，我们只需要该excel就可以，而不用改代码
                    Object path=extractMap.get(key);
                    Object value =res.jsonPath().get(path.toString());

                Object tokenPath = extractMap.get("token");
                token = resLogin.jsonPath().get(tokenPath.toString());
                //token = resLogin.jsonPath().get("data.token_info.token");
                A_Enviroment.token=token;

        */

    }


    @Test(dataProvider = "getRechargeDatas")
    public void testRecharge(A_ExcelPojo excelPojo){
        //方式1.环境变量为确定值，而非Map集合的写法
        //String params=regexReplace(excelPojo.getInputParams(),A_Enviroment.memberId+"");
        //方式2.环境变量为Map集合，但是需要写死member_id，我们不希望写死
        //String params=regexReplace(excelPojo.getInputParams(),A_Enviroment.envData.get("member_id")+"");
        //方式3.环境变量为Map集合，然后把member_id参数化，需要修改一下regexReplace方法
        excelPojo=casesReplace(excelPojo);
        //因为参数excelPojo里面的params并没有更新我们这个params，所以要先更新数据
        //excelPojo.setInputParams(params);   因为上面有参数替换了，所以也不需要这个了
        Response res = request(excelPojo);

        assertResponse(excelPojo,res);
    }


    @DataProvider
    public Object[] getRechargeDatas(){
        List<A_ExcelPojo> listDatas = readSpecifyExcelData( 3, 3);
        //再把获取的数据转化成数组
        return listDatas.toArray();
    }

    //测试正则表达式的用法
    public static void main(String[] args) {
        //正则表达式测试
        String str="ddsfdsfs{{iphone}}fdsfdsf{{name}}\n"+"dsafsdfsfdfsd{{sex}}fdsfds";

        int memberId=101;
        String token="dfsadfdsffd";
        String phone="18888888888";

        //匹配的Java类Pattern:正则表达式匹配器,两次转义才可以把大括号转成普通的字符
        Pattern pattern = Pattern.compile("\\{\\{(.*?)}}");
        //得到一个匹配对象，有一个方法find(),他只会找一次，所以用循环找多次
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()){
            //参数0表示获取到整个匹配内容，大括号也有：{{内容}}
            //参数1表示获取到大括号里面的内容，没有大括号：内容
            String outStr = matcher.group(0);
            String inStr = matcher.group(1);
            if(outStr.equals("{{name}}")){
                System.out.println(str.replace(outStr,phone));
            }
        }
    }



}

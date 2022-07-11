package com.lemon.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;

//数据驱动：先设置表头属性，在导入Excel包注解，和该excel里面的列头一一匹配
public class A_ExcelPojo {
    @Excel(name = "提取返回数据")
    private String extract;
    @Excel(name = "序号")
    private int caseID;
    @Excel(name = "接口模块")
    private String interfaceName;
    @Excel(name = "用例标题")
    private String title;
    @Excel(name = "请求头")
    private String requestHeader;
    @Excel(name = "请求方式")
    private String method;
    @Excel(name = "接口地址")
    private String url;
    @Excel(name = "参数输入")
    private String inputParams;
    @Excel(name = "期望返回结果")
    private String expected;
    @Excel(name = "数据库校验")
    private String dbAssert;

    public String getExtract() {
        return extract;
    }

    public void setExtract(String extract) {
        this.extract = extract;
    }

    public int getCaseID() {
        return caseID;
    }

    public void setCaseID(int caseID) {
        this.caseID = caseID;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(String requestHeader) {
        this.requestHeader = requestHeader;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInputParams() {
        return inputParams;
    }

    public void setInputParams(String inputParams) {
        this.inputParams = inputParams;
    }

    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }

    public String getDbAssert() {
        return dbAssert;
    }

    public void setDbAssert(String dbAssert) {
        this.dbAssert = dbAssert;
    }

    @Override
    public String toString() {
        return "A_ExcelPojo{" +
                "extract='" + extract + '\'' +
                ", caseID=" + caseID +
                ", interfaceName='" + interfaceName + '\'' +
                ", title='" + title + '\'' +
                ", requestHeader='" + requestHeader + '\'' +
                ", method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", inputParams='" + inputParams + '\'' +
                ", expected='" + expected + '\'' +
                ", dbAssert='" + dbAssert + '\'' +
                '}';
    }
}
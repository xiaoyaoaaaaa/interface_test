package com.lemon.util;

import com.lemon.data.Constant;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * JDBC:Java Database Connectivity,是java语言中用来规范客户端如何来访问数据库的应用程序接口
 * 源生JDBC比较复杂，commons-dbutils简称DBUtils，它是对JDBC的简单封装，简化了JDBC操作
 * JDBC工具类放在util下面
 * 核心功能：
 *      1.QueryRunner中提供对sql语句操作的API
 *          执行insert updata delete：updata(Connection conn,String sql,Object... params);
 *          执行select操作：query(Connection conn,String sql,ResultSetHandler<T> rsh,Object... params);
 *      2.执行查询操作的ResultSetHandler有几个常用的实现类
 *          a:MapHandler将结果集中第一条记录封装到Map<String,Object>中，key就是字段名称，value就是字段值
 *          b:MapListHandler将结果集中每一条记录封装到Map<String,Object>中，...
 *          c:ScalarHandler用于单个数据，比如select cout(*) from datas;
 *
 * */
public class JDBCUtils {
    public static Connection getConnection() {
        //定义数据库连接
        //Oracle：jdbc:oracle:thin:@localhost:1521:DBName
        //SqlServer：jdbc:microsoft:sqlserver://localhost:1433; DatabaseName=DBName
        //MySql：jdbc:mysql://localhost:3306/DBName
        String url="jdbc:mysql://"+ Constant.DB_BASE_URI+Constant.DB_NAME+"?useUnicode=true&characterEncoding=utf-8";
        String user=Constant.DB_USERNAME;
        String password=Constant.DB_PWD;
        //定义数据库连接对象
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user,password);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }



    /**
     * 对上面进行封装:sql的更新操作（增删改）
     *
     */

    public static void update(String sql){
        //建立与数据库连接对象
        Connection connection = getConnection();
        QueryRunner queryRunner = new QueryRunner();
        try {
            queryRunner.update(connection,sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            //关闭数据连接，调用关闭数据库的方法
            closeConnection(connection);
        }
    }




    /**
     * 查询所有结果集
     *
     */
    public static List<Map<String, Object>> queryAll(String sql){
        Connection connection = getConnection();
        QueryRunner queryRunner = new QueryRunner();
        List<Map<String, Object>> result =null;
        try {
            result = queryRunner.query(connection, sql, new MapListHandler());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            //关闭数据连接，调用关闭数据库的方法
            closeConnection(connection);
        }
        return result;
    }


    /**
     * 查询所有结果（集）中的第一条
     *
     */
    public static Map<String, Object> queryOne(String sql){
        Connection connection = getConnection();
        QueryRunner queryRunner = new QueryRunner();
        Map<String, Object> result =null;
        try {
            result = queryRunner.query(connection, sql, new MapHandler());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            //关闭数据连接，调用关闭数据库的方法
            closeConnection(connection);
        }
        return result;
    }


    /**
     * 查询单个数据
     *      对数据库进行查询操作，查询结果只返回单个数据，如果返回的是数字，类型为long类型
     */
    public static Object querySingleData(String sql){
        Connection connection = getConnection();
        QueryRunner queryRunner = new QueryRunner();
        Object result =null;
        try {
            result = queryRunner.query(connection, sql, new ScalarHandler<>());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            //关闭数据连接，调用关闭数据库的方法
            closeConnection(connection);
        }
        return result;
    }


    public static void closeConnection(Connection connection){
        //判空
        if (connection!=null){
            //关闭数据库连接
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

package com.htyxkj.amb.dbutils;


import com.htyxkj.amb.cl.ICL;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;


/**
 * @Auther: fengzejiang1987@163.com
 * @Date : 2019/1/9 10:01
 */
public class HikariPoolManager {
    static Logger logger = LoggerFactory.getLogger(HikariPoolManager.class);

    //-- Hikari Datasource -->
    //driverClassName无需指定，除非系统无法自动识别
    private static String driverClassName;
    //database address
    private static String jdbcUrl;
    //useName 用户名
    private static String username;
    //password
    private static String password ;
    //连接只读数据库时配置为true， 保证安全 -->
    private static boolean readOnly;
    //等待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException， 缺省:30秒 -->
    private static int connectionTimeout;
    // 一个连接idle状态的最大时长（毫秒），超时则被释放（retired），缺省:10分钟 -->
    private static int idleTimeout;
    //一个连接的生命时长（毫秒），超时而且没被使用则被释放（retired），缺省:30分钟，建议设置比数据库超时时长少30秒，参考MySQL wait_timeout参数（show variables like '%timeout%';） -->
    private static int maxLifetime;
    // 连接池中允许的最大连接数。缺省值：10；推荐的公式：((core_count * 2) + effective_spindle_count) -->
    private static int maximumPoolSize = 10;
    private static String connectionInitSql;
    private static String schema ;
    static HikariDataSource hikariDataSource = new HikariDataSource();

    static {
        /**
         * 加载配置文件
         */
        readProperties("HikariConfig");
        /**
         * 设置数据源的参数
         */
        dataSourceConfig();

    }

    /**
     * @param propertitieFileName 配置文件名
     * @return
     * @Exception FileNotFoundException, IOException
     */
    private static void readProperties(String propertitieFileName) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(propertitieFileName);
            driverClassName = bundle.getString("driverClassName");
            jdbcUrl = bundle.getString("jdbcUrl");
            username = bundle.getString("username");
            password = bundle.getString("password");
            readOnly = Boolean.parseBoolean(bundle.getString("readOnly"));
            connectionTimeout = Integer.parseInt(bundle.getString("connectionTimeout"));
            idleTimeout = Integer.parseInt(bundle.getString("idleTimeout"));
            maxLifetime = Integer.parseInt(bundle.getString("maxLifetime"));
            maximumPoolSize = Integer.parseInt(bundle.getString("maximumPoolSize"));
            if(bundle.containsKey("connectionInitSql"))
                connectionInitSql = bundle.getString("connectionInitSql");
            if(bundle.containsKey("schema"))
                schema = bundle.getString("schema");
        } catch (Exception e) {
            logger.error("读取数据库参数出现问题：" + e);
            throw e;
        }
    }

    /**
     * 设置datasource各个属性值
     *
     * @param
     * @return Connection
     * @Exception Exception
     */
    private static void dataSourceConfig() {
        try {
            //driverClassName无需指定，除非系统无法自动识别
            //private static String driverClassName="";
            hikariDataSource.setDriverClassName(driverClassName);
            //database address
            hikariDataSource.setJdbcUrl(jdbcUrl);
            //useName 用户名
            hikariDataSource.setUsername(username);
            //password
            hikariDataSource.setPassword(password);
            //连接只读数据库时配置为true， 保证安全 -->
            hikariDataSource.setReadOnly(readOnly);
            //等待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException， 缺省:30秒 -->
            hikariDataSource.setConnectionTimeout(connectionTimeout);
            // 一个连接idle状态的最大时长（毫秒），超时则被释放（retired），缺省:10分钟 -->
            hikariDataSource.setIdleTimeout(idleTimeout);
            //一个连接的生命时长（毫秒），超时而且没被使用则被释放（retired），缺省:30分钟，建议设置比数据库超时时长少30秒，参考MySQL wait_timeout参数（show variables like '%timeout%';） -->
            hikariDataSource.setMaximumPoolSize(maxLifetime);
            // 连接池中允许的最大连接数。缺省值：10；推荐的公式：((core_count * 2) + effective_spindle_count) -->
            hikariDataSource.setMaximumPoolSize(maximumPoolSize);
            //测试连接sql
            hikariDataSource.setConnectionTestQuery(connectionInitSql);
            if(schema!=null&&schema.length()>0)
            hikariDataSource.setSchema(schema);
        } catch (Exception e) {
            logger.error("设置datasource各个属性值异常!" + e);
            throw e;
        }
    }
    /**
     * 取得数据库连接
     * @return
     * @throws Exception
     */
    public static Connection getConnection() throws Exception{
        return hikariDataSource.getConnection();
    }

    /**
     * 取得数据库连接
     * @return
     */
    public static int getDBType(){
        int type = ICL.DB_UN_KNOW;
        if(driverClassName!=null){
            if (driverClassName.toUpperCase().indexOf("MYSQL")!=-1){
                return ICL.DB_MYSQL;
            }else if(driverClassName.toUpperCase().indexOf("ORACLE")!=-1){
                return ICL.DB_ORACLE;
            }else if(driverClassName.toUpperCase().indexOf("SQL SERVER")!=-1||driverClassName.toUpperCase().indexOf("SQLSERVER")!=-1){
                return ICL.DB_SQLSERV;
            }else if(driverClassName.toUpperCase().indexOf("DB2")!=-1){
                return ICL.DB_DB2;
            }
        }
        return type;
    }

    /**
     * 释放数据库连接
     * @param connection
     * @throws Exception
     */
    public static void freeConnection(Connection connection) throws Exception{
        if (connection != null){
            try {
                connection.close();
            }catch(Exception e){
                logger.error("释放数据库连接时发生异常!"+ e.getMessage());
            }
        }
    }

    /**
     * @param stat
     * @throws Exception
     */
    public static void freeCallableStatement(PreparedStatement stat) throws Exception{
        if (stat != null){
            try {
                stat.close();
            }catch(Exception e){
                logger.error("释放数据库连接时发生异常!"+ e.getMessage());
            }
        }
    }

    /**
     * @param stat
     * @throws Exception
     */
    public static void freeResultSet(ResultSet stat) throws Exception{
        if (stat != null){
            try {
                stat.close();
            }catch(Exception e){
                logger.error("释放数据库连接时发生异常!"+ e.getMessage());
            }
        }
    }

    // 关闭连接
    public static void close(Connection conn, PreparedStatement stat, ResultSet rs) throws Exception{
        freeResultSet(rs);
        freeCallableStatement(stat);
        freeConnection(conn);
    }
}

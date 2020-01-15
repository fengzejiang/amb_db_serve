package com.htyxkj.amb;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.htyxkj.amb.dbutils.HikariPoolManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;

/**
 * @Auther: fengzejiang1987@163.com
 * @Date : 2020/1/11 14:26
 */
public class TT {
    public static void main(String[] args) throws Exception {
//        String sq = "{\"def_user\":\"YD_USER\",\"def_user1\":\"YD_USER\"}";
//        JSONObject jsons = JSON.parseObject(sq);
//        Iterator<String> it = jsons.keySet().iterator();
//        while(it.hasNext()){
//            String key = it.next();
//            String value = jsons.getString(key);
//            System.out.println(key.toUpperCase()+"==="+value);
//        }
        Connection con = HikariPoolManager.getConnection();
//        con.setSchema("MTC");
        String sql = "select * from test";
        Statement sts = con.createStatement();
        ResultSet rs = sts.executeQuery(sql);
        while (rs.next()){
            int id = rs.getInt(1);
            String name = rs.getString(2);
            System.out.println(id+"==="+name);
        }

    }
}

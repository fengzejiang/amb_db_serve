package com.htyxkj.amb.dbservice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.htyxkj.amb.dbo.ProcEntity;
import com.htyxkj.amb.dbo.ProcParamEntity;
import com.htyxkj.amb.dbo.ReqEntity;
import com.htyxkj.amb.dbo.RtnEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Auther: fengzejiang1987@163.com
 * @Date : 2020/1/10 18:26
 */
public abstract class AmbServiceI implements AmbDBService {
    private static Logger _log = LoggerFactory.getLogger(AmbServiceI.class);
    public static RtnEntity makeValues(ResultSet rs, RtnEntity rtnEntity) throws SQLException {
        ResultSetMetaData data = rs.getMetaData();
        ArrayList<String> cols = new ArrayList<>();
        for(int i=1;data!=null&&i<=data.getColumnCount();i++){
            String columnName = data.getColumnName(i);
            if(!cols.contains(columnName))
                cols.add(columnName);
        }
        rtnEntity.setCols(cols);
        ArrayList<JSONObject> rtnData = new ArrayList<>();
        while (rs.next()){
            JSONObject row = new JSONObject();
            for (String col:cols) {
                Object value = rs.getObject(col);
                row.put(col,value);
            }
            rtnData.add(row);
        }
        rtnEntity.setData(rtnData);
        return rtnEntity;
    }

    public CallableStatement init(ProcEntity proc, ReqEntity req, Connection conn) throws Exception{
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("{call ").append(proc.getAllName()).append("(");
        ArrayList<ProcParamEntity> list = proc.getList();
        String s1 = "";
        HashMap<String,String> params = req.getParams();
        ArrayList<ProcParamEntity> exits = new ArrayList<>();
        for (ProcParamEntity p:list) {
            if(params.containsKey(p.getName())){
                Object o0 = params.get(p.getName());
                if(o0!=null){
                    s1+="?,";
                    exits.add(p);
                }else{
                    if(p.isOut()){
                        s1+="?,";
                        exits.add(p);
                    }else{
                        s1+="default,";
                    }
                }
            }else{
                if(p.isOut()){
                    s1+="?,";
                    exits.add(p);
                }else{
                    s1+="default,";
                }
            }
        }
            if(s1.length()>1)
            s1 = s1.substring(0,s1.length()-1);
        stringBuffer.append(s1);
        stringBuffer.append(")}");
        CallableStatement cstmt = conn.prepareCall(stringBuffer.toString());
        for(int i=0;i<exits.size();i++){
            ProcParamEntity p = exits.get(i);
            if(p.isOut()){
                cstmt.registerOutParameter((i+1), p.getDataType());
            }else{
                String value = params.get(p.getName());
                cstmt.setObject((i+1),value);
            }
        }
        return cstmt;
    }

    /***
     * 调用并执行存储过程 过程中会抛出所有错误
     * @param req 请求参数信息
     * @return 返回执行结果
     * @throws Exception 抛出错误
     */
    @Override
    public RtnEntity excuteProc(ReqEntity req) throws Exception{
        ProcEntity proc = getProcInfoByName(req.getProcName());
        if(proc.getName()==null||proc.getName().equals("")){
            _log.info("存储过程不存在！！");
            return null;
        }
        _log.info(JSON.toJSONString(proc));
        return getExcuteProce(req, proc);
    }
    /**
     * 根据存储过程名称查询存储过程参数信息
     * 如果没有查询到参数，按照无参的处理
     * @param name 存储过程名称
     * @return 返回存储过程信息
     * @throws Exception
     */
    public abstract ProcEntity getProcInfoByName(String name) throws Exception;
    /**
     * 执行存储过程接口
     * @param req 请求参数对象
     * @param proc 存储过程对象，包含存储过程的参数信息
     * @return 返回执行结果
     * @throws Exception
     */
    public abstract  RtnEntity getExcuteProce(ReqEntity req, ProcEntity proc) throws Exception;

}

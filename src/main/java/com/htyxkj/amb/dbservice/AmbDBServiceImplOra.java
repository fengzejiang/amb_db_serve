package com.htyxkj.amb.dbservice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.htyxkj.amb.dbo.ProcEntity;
import com.htyxkj.amb.dbo.ProcParamEntity;
import com.htyxkj.amb.dbo.ReqEntity;
import com.htyxkj.amb.dbo.RtnEntity;
import com.htyxkj.amb.dbutils.HikariPoolManager;
import oracle.jdbc.OracleTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Auther: fengzejiang1987@163.com
 * @Date : 2020/1/10 11:02
 */
public class AmbDBServiceImplOra extends AmbServiceI {
    private static Logger _log = LoggerFactory.getLogger(AmbDBServiceImplOra.class);

    /**
     * 执行存储过程
     * @param req 请求参数对象
     * @param proc 存储过程对象，包含存储过程的参数信息
     * @return 返回执行结果
     * @throws Exception
     */
    public RtnEntity getExcuteProce(ReqEntity req, ProcEntity proc) throws Exception {
        Connection conn = null;
        CallableStatement cstmt = null;
        ResultSet rs = null;
        RtnEntity rtnEntity = new RtnEntity();
        try{
            conn = HikariPoolManager.getConnection();
            rtnEntity.setName(req.getProcName());
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("{call ").append(proc.getAllName()).append(" (");
            ArrayList<ProcParamEntity> list = proc.getList();
            String s1 = "";
            for (ProcParamEntity p:list) {
                s1+="?,";
            }
            if(s1.length()>1)
                s1 = s1.substring(0,s1.length()-1);
            stringBuffer.append(s1);
            stringBuffer.append(")}");
            _log.info(stringBuffer.toString());
            cstmt = conn.prepareCall(stringBuffer.toString());
            HashMap<String,String> params = req.getParams();
            for (ProcParamEntity p:list) {
                if(p.isOut()){
                    cstmt.registerOutParameter(p.getPosition(), p.getDataType());
                }else{
                    String value = params.get(p.getName());
                    cstmt.setObject(p.getPosition(),value==null?p.getDefaultV():value);
                }
            }
            cstmt.execute();
            ProcParamEntity p = proc.getOutParam();
            if(p!=null){
                switch (p.getDataType()){
                    case OracleTypes.CURSOR:
                        rs = (ResultSet) cstmt.getObject(p.getPosition());
                        if(rs!=null){
                            rtnEntity = makeValues(rs,rtnEntity);
                        }
                        break;
                        default:
                            Object value = cstmt.getObject(p.getPosition());
                            JSONObject o1 = new JSONObject();
                            o1.put(p.getName().toLowerCase(),value);
                            rtnEntity.addRow(o1);
                            break;
                }
            }
            _log.info("返回结果："+JSON.toJSONString(rtnEntity));
            return rtnEntity;
        }catch (Exception e){
            _log.error("出错了：",e);
            throw e;
        }finally {
            HikariPoolManager.close(conn,cstmt,rs);

        }
    }
    /**
     * 根据存储过程名称查询存储过程参数信息
     * 如果没有查询到参数，按照无参的处理
     * @param name 存储过程名称
     * @return 返回存储过程信息
     * @throws Exception
     */
    public ProcEntity getProcInfoByName(String name) throws Exception{
        String sql="select OBJECT_NAME,PACKAGE_NAME,ARGUMENT_NAME ,IN_OUT,DATA_TYPE,POSITION,DEFAULT_VALUE from USER_ARGUMENTS where OBJECT_NAME=? ORDER BY OBJECT_NAME,PACKAGE_NAME,OVERLOAD,SEQUENCE";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ProcEntity entity = new ProcEntity();
        try{
            conn = HikariPoolManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1,name);
            rs = stmt.executeQuery();
            ArrayList<ProcParamEntity> list = new ArrayList<>();
            entity.setName(name);
            int i=0;
            while (rs.next()){
                String obj_name = rs.getString(1);
                String pkg_name = rs.getString(2);
                if(i==0){
                    entity.setName(obj_name);
                    entity.setPackageName(pkg_name);
                }
                String aName = rs.getString(3);
                aName = aName.toUpperCase();
                String i_out = rs.getString(4);
                i_out = i_out.toUpperCase();
                String dtype = rs.getString(5);
                int position = rs.getInt(6);
                String def = rs.getString(7);
                boolean bout = "OUT".equals(i_out);
                int type = OracleTypes.VARCHAR;
                if(dtype.equals("VARCHAR2")||dtype.equals("VARCHAR")){
                    type = OracleTypes.VARCHAR;
                }else if(dtype.equals("REF CURSOR")){
                    type = OracleTypes.CURSOR;
                }
                ProcParamEntity entity1 = new ProcParamEntity(aName,bout,type,position);
                entity1.setDefaultV(def);
                list.add(entity1);
            }
            entity.setList(list);
        }catch (Exception e){
            _log.error("查询存储过程错误：",e);
            throw e;
        }finally {
            HikariPoolManager.close(conn,stmt,rs);
            return entity;
        }
    }

}

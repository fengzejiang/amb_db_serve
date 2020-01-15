package com.htyxkj.amb.dbservice;

import com.alibaba.fastjson.JSON;
import com.htyxkj.amb.dbo.ProcEntity;
import com.htyxkj.amb.dbo.ProcParamEntity;
import com.htyxkj.amb.dbo.ReqEntity;
import com.htyxkj.amb.dbo.RtnEntity;
import com.htyxkj.amb.dbutils.HikariPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;

/**
 * @Auther: fengzejiang1987@163.com
 * @Date : 2020/1/10 11:02
 */
public class AmbDBServiceImplDB2 extends AmbServiceI {
    private static Logger _log = LoggerFactory.getLogger(AmbDBServiceImplDB2.class);

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
            rtnEntity.setName(req.getProcName());
            conn = HikariPoolManager.getConnection();
            cstmt = init(proc,req,conn);
            cstmt.execute();
            rs = cstmt.getResultSet();
            if(rs==null){
                while (cstmt.getMoreResults()){
                    if(rs!=null)
                        rs.close();
                    rs = cstmt.getResultSet();
                    rtnEntity = makeValues(rs,rtnEntity);
                }
            }else{
                rtnEntity = makeValues(rs,rtnEntity);
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
        String sql="select PARMNAME,TYPEID,LENGTH,PARM_MODE,ORDINAL,PROCSCHEMA from SYSCAT.PROCPARMS where PROCNAME=? ORDER BY ORDINAL";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ProcEntity entity = new ProcEntity();
        try{
            conn = HikariPoolManager.getConnection();
//            conn.setSchema(null);
//            conn.setSchema("DB2_T");
            stmt = conn.prepareStatement(sql);
            stmt.setString(1,name);
            rs = stmt.executeQuery();
            ArrayList<ProcParamEntity> list = new ArrayList<>();
            entity.setName(name);
            int i=0;
            while (rs.next()){
                String aName = rs.getString(1);
//                aName = aName.toUpperCase().substring(1);
                if(i==0){
                    String pkg = rs.getString(6).trim();
                    entity.setPackageName(pkg);
                    i++;
                }
                boolean bout = rs.getString(3).equalsIgnoreCase("OUT");
                int dataType = rs.getInt(2);
                int position = rs.getInt(5);
                ProcParamEntity entity1 = new ProcParamEntity(aName,bout,dataType,position);
                list.add(entity1);
            }
            entity.setList(list);
            return entity;
        }catch (Exception e){
            _log.error("查询存储过程错误：",e);
            throw e;
        }finally {
            HikariPoolManager.close(conn,stmt,rs);
        }
    }

}

package com.htyxkj.amb.dbservice;

import com.alibaba.fastjson.JSON;
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
public class AmbDBServiceImplSQLServer extends AmbServiceI {
    private static Logger _log = LoggerFactory.getLogger(AmbDBServiceImplSQLServer.class);

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
        String sql="   select a.name,b.name type,a.length,a.colorder,a.isoutparam from syscolumns a,systypes b,sysobjects d where \n" +
                "   a.xtype=b.xusertype and a.id=d.id and d.xtype='P' and a.id =object_id(?)";
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
            while (rs.next()){
                String aName = rs.getString(1);
                aName = aName.toUpperCase().substring(1);
                boolean bout = rs.getInt(5)==1;
                String dtype = rs.getString(2);
                int dataType = Types.VARCHAR;
                if("int".equals(dtype)){
                    dataType = Types.INTEGER;
                }
                int position = rs.getInt(4);
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

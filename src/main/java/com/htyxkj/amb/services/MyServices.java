package com.htyxkj.amb.services;


import com.htyxkj.amb.cl.ICL;
import com.htyxkj.amb.dbo.ReqEntity;
import com.htyxkj.amb.dbo.RtnEntity;
import com.htyxkj.amb.dbservice.*;
import com.htyxkj.amb.dbutils.HikariPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 * @Auther: fengzejiang1987@163.com
 * @Date : 2020/1/9 16:00
 */
@Service
public class MyServices {
    private static int dbType;
    static {
        dbType = HikariPoolManager.getDBType();
    }
    private AmbDBService service;
    public RtnEntity execProc(ReqEntity req) throws Exception{
        switch (dbType){
            case ICL.DB_ORACLE:
                service = new AmbDBServiceImplOra();
                break;
            case ICL.DB_SQLSERV:
                service = new AmbDBServiceImplSQLServer();
                break;
            case ICL.DB_MYSQL:
                service = new AmbDBServiceImplMySQL();
                break;
            case ICL.DB_DB2:
                service = new AmbDBServiceImplDB2();
                break;
        }
        if(service!=null)
            return service.excuteProc(req);
        return new RtnEntity();
    }
}

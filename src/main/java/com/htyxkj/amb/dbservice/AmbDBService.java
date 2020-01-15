package com.htyxkj.amb.dbservice;

import com.htyxkj.amb.dbo.ReqEntity;
import com.htyxkj.amb.dbo.RtnEntity;

/**
 * @Auther: fengzejiang1987@163.com
 * @Date : 2020/1/10 10:30
 */
public interface AmbDBService {
    RtnEntity excuteProc(ReqEntity entity) throws Exception;



}

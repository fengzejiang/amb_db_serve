package com.htyxkj.amb.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.htyxkj.amb.cl.ICL;
import com.htyxkj.amb.dbo.ReqEntity;
import com.htyxkj.amb.dbo.RtnEntity;
import com.htyxkj.amb.services.MyServices;
import oracle.jdbc.proxy.annotation.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * @Auther: fengzejiang1987@163.com
 * @Date : 2020/1/9 15:32
 */
@RestController
public class DBProcController {
    private static Logger _log = LoggerFactory.getLogger(DBProcController.class);

    @Autowired
    private MyServices services;

    @Value("${server.port}")
    String port;    //通过这种方式也能获取注册服务的信息
    @Value("${spring.application.name}")
    String serverid;
    @RequestMapping(value = "/{id}/{name}",method = {RequestMethod.POST, RequestMethod.GET},produces = "application/json;charset=utf-8")
    public RtnEntity order(@PathVariable String id,@PathVariable String name, HttpServletRequest request) throws Exception {
        if(ICL.P_R.equals(id)){
            ReqEntity reqEntity = new ReqEntity();
            reqEntity.setProcName(name);
            Enumeration<String> names = request.getParameterNames();
            HashMap<String,String> params = new HashMap<>();
            while (names.hasMoreElements()){
                String p1 = names.nextElement();
                String v1 = request.getParameter(p1);
                if(v1!=null){
                    params.put(p1.toUpperCase(),v1);
                }
            }
            params = getBodyParams(request,params);
            reqEntity.setParams(params);
            _log.info("请求参数"+ JSON.toJSONString(reqEntity));
            return services.execProc(reqEntity);
        }else{
            return new RtnEntity();
        }

    }

    private HashMap<String,String> getBodyParams(HttpServletRequest request,HashMap<String,String> params) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        _log.info(sb.toString());
        if(sb.length()>3){
            JSONObject jsons = JSON.parseObject(sb.toString());
            Iterator<String> it = jsons.keySet().iterator();
            while(it.hasNext()){
                String key = it.next();
                String value = jsons.getString(key);
                params.put(key.toUpperCase(),value);
            }
        }
        _log.info("body:"+ sb.toString());
        return params;

    }
}

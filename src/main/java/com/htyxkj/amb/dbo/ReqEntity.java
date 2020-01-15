package com.htyxkj.amb.dbo;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @Auther: fengzejiang1987@163.com
 * @Date : 2020/1/10 10:32
 */
public class ReqEntity implements Serializable {
    public ReqEntity(){}
    private String procName;
    private HashMap<String,String> params;

    public String getProcName() {
        return procName;
    }

    public void setProcName(String procName) {
        this.procName = procName;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }
}

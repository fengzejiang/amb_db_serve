package com.htyxkj.amb.dbo;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @Auther: fengzejiang1987@163.com
 * @Date : 2020/1/10 11:53
 */
public class ProcEntity implements Serializable {
    private String name;
    private String packageName;
    private ArrayList<ProcParamEntity> list;

    public ProcEntity() {
    }
    public ProcEntity(String name) {
        this.name = name;
    }
    public ProcEntity(String name, String packageName) {
        this.name = name;
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAllName(){
        if(packageName!=null&&packageName.length()>0){
            return packageName+"."+name;
        }
        return name;
    }

    @JSONField(serialize = false)
    public ProcParamEntity getOutParam(){
        ProcParamEntity p = null;
        for (ProcParamEntity p1:list) {
            if(p1.isOut()){
                p = p1;
                break;
            }
        }
        return p;
    }

    public ArrayList<ProcParamEntity> getList() {
        return list;
    }

    public void setList(ArrayList<ProcParamEntity> list) {
        this.list = list;
    }
}

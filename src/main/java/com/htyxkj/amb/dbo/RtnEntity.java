package com.htyxkj.amb.dbo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @Auther: fengzejiang1987@163.com
 * @Date : 2020/1/10 9:15
 */
public class RtnEntity implements Serializable {
    public RtnEntity(){}

    private String Name;

    private ArrayList<JSONObject> data;

    private ArrayList<String> cols;
    @JSONField(name = "Name")
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
    @JSONField(name = "data")
    public ArrayList<JSONObject> getData() {
        return data;
    }

    public void setData(ArrayList<JSONObject> data) {
        this.data = data;
    }
    @JSONField(serialize = false)
    public ArrayList<String> getCols() {
        return cols;
    }

    public void setCols(ArrayList<String> cols) {
        this.cols = cols;
    }

    public void addRow(JSONObject o1) {
        if(data==null){
            data = new ArrayList<>();
        }
        if(o1!=null)
            data.add(o1);
    }
}

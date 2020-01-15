package com.htyxkj.amb.dbo;

import java.io.Serializable;

/**
 * @Auther: fengzejiang1987@163.com
 * @Date : 2020/1/10 11:54
 */
public class ProcParamEntity implements Serializable {
    private String name;
    private String defaultV;
    private boolean out;
    private int dataType;
    private int position;
    public ProcParamEntity(){}
    public ProcParamEntity(String name, int dataType, int position) {
        this.name = name;
        this.dataType = dataType;
        this.position = position;
    }

    public ProcParamEntity(String name, boolean out, int dataType, int position) {
        this.name = name;
        this.out = out;
        this.dataType = dataType;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOut() {
        return out;
    }

    public void setOut(boolean out) {
        this.out = out;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getDefaultV() {
        return defaultV;
    }

    public void setDefaultV(String defaultV) {
        this.defaultV = defaultV;
    }
}

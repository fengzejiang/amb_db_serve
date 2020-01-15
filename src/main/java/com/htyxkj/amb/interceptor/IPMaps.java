package com.htyxkj.amb.interceptor;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.ResourceBundle;

public class IPMaps {
    public static HashMap<String,String> ips;
    static {
        readProperties("IPConfig");
    }

    private static void readProperties(String ipConfigFile) {
        if(ips==null){
            ips = new HashMap<>();
        }
        ips.clear();
        ResourceBundle bundle = ResourceBundle.getBundle(ipConfigFile);
        if(bundle.containsKey("ips")){
            String ipStr = bundle.getString("ips");
            if(!StringUtils.isEmpty(ipStr)){
                String[] iip = ipStr.split(";");
                for (String ip:iip) {
                    ips.put(ip,ip);
                }
            }
        }
        ips.put("127.0.0.1","127.0.0.1");
        ips.put("0:0:0:0:0:0:0:1","0:0:0:0:0:0:0:1");
    }

}

package com.htyxkj.amb.interceptor;

import com.htyxkj.amb.utils.IPUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IPInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ipAddress= IPUtils.getRealIP(request);
        if(!StringUtils.isNotBlank(ipAddress)){
            throw new RuntimeException("No Auth");
        }else{
            if(IPMaps.ips.containsKey(ipAddress)){
                return true;
            }else{
                throw new RuntimeException("No Auth");
            }
        }
    }
}

package com.nieyue.interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.nieyue.bean.Role;
import com.nieyue.exception.MySellerSessionException;
import com.nieyue.token.TokenManager;

/**
 * 用户session控制拦截器
 * @author yy
 *
 */
public class AdminControllerSessionInterceptor implements HandlerInterceptor {
	@Autowired
    private TokenManager manager;

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
	
		
		//如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        
        //自定义token
//        if(method.getName().equals("loginAdmin")||method.getName().equals("isloginAdmin")||method.getName().equals("tokenAdmin")){
//        	return true;
//        }else if (manager.checkToken("XuDeOAadmin", manager.getToken("XuDeOAadmin", request), request, response)) {
//        	//验证token成功
//            return true;
//        }
        if(method.getName().equals("loginAdmin")||method.getName().equals("isloginAdmin")||method.getName().equals("tokenAdmin")){
        	return true;
        }else if (request.getSession().getAttribute("admin")!=null) {
        	//确定角色存在
        	if(request.getSession().getAttribute("role")!=null ){
        	//超级管理员
        	if(((Role)request.getSession().getAttribute("role")).getName().equals("超级管理员")){
        		return true;
        	}
        	if(((Role)request.getSession().getAttribute("role")).getName().equals("主管")){
        		
        		return true;
        	}
        	if(((Role)request.getSession().getAttribute("role")).getName().equals("普通员工")){
        		//允许任何人修改任务
        		if(request.getRequestURI().indexOf("/task/update")>-1){
        			return true;
        		}
        		//只许修改自己的值
        		if(request.getRequestURI().indexOf("/admin")>-1){
        			//不许删除/增加
        			if( request.getRequestURI().indexOf("/delete")>-1 || request.getRequestURI().indexOf("/add")>-1){
        				throw new MySellerSessionException();
        			}
        			return true;
        		}
        		//不许更新和删除
        		if(request.getRequestURI().indexOf("/update")>-1 || request.getRequestURI().indexOf("/delete")>-1){
        			throw new MySellerSessionException();
        		}
        		
        		return true;
        	}
        	
        	}
        	
        	//验证token成功
        	return true;
        }
        //如果验证token失败
       throw new MySellerSessionException();
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
	}
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
	}

}

package com.itheima.reggie_takeout.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie_takeout.common.BaseContext;
import com.itheima.reggie_takeout.common.R;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 */
@Slf4j
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    // 路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //强转
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取本次请求的URI
        String requestURI = request.getRequestURI();

        // 这里{}表示占位符，输出的时候用实际值进行替换
        log.info("拦截到请求：{}", requestURI);

        //定义不需要处理的请求，将这些请求直接放行
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**"
        };

        //2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3.如果不需要处理，则直接放行
        if (check) {
            log.info("本次请求：{}，不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        //4.运行到这里代表该请求需要处理。进行登录状态的判断，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户已登录，id为{}", request.getSession().getAttribute("employee"));

            Long empId = (Long) request.getSession().getAttribute("employee");
            // 设置当前登录用户id到当前线程中
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request, response);
            return;
        }

        //5. 运行到这里代表该请求需要处理，而且用户未登录（没有在session中找到employee属性对应的对象ID）。此时返回未登录结果,通过输出流方式向客户端页面响应数据
        log.info("用户未登录");
        log.info("用户id为：{}", request.getSession().getAttribute("employee"));
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

    }

    // 封装一个方法，用于判断请求是否需要处理
    public boolean check(String[] urls, String requestURI) {
        // 加强for循环遍历urls数组
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                //匹配
                return true;
            }
        }
        //不匹配
        return false;
    }
}

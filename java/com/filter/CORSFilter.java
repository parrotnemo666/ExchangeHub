//package com.filter;
//
//import java.io.IOException;
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//// 注意：在 Servlet 3.1 中，使用 javax 而不是 jakarta
//public class CORSFilter implements Filter {
//    
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        // 初始化時不需要做任何事
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        HttpServletResponse httpResponse = (HttpServletResponse) response;
//
//        // 設置 CORS 標頭
//        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
//        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
//        httpResponse.setHeader("Access-Control-Max-Age", "3600");
//
//        // 處理 OPTIONS 請求
//        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
//            httpResponse.setStatus(HttpServletResponse.SC_OK);
//        } else {
//            chain.doFilter(request, response);
//        }
//    }
//
//    @Override
//    public void destroy() {
//        // 銷毀時不需要做任何事
//    }
//}
package com.percap.infra.security;

import jakarta.servlet.*;

import java.io.IOException;

public class SecurityFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Implement your security filtering logic here

        // Continue the filter chain
        chain.doFilter(request, response);
    }
}

package com.saikumar.spboot.spboot.filters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.TeeOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saikumar.spboot.spboot.User;
import com.saikumar.spboot.spboot.UserController;
import com.saikumar.spboot.spboot.models.AuthenticationRequest;

@Component
public class RequestResponseLoggers implements Filter {
	
	Logger logger = LoggerFactory.getLogger(RequestResponseLoggers.class);
	
	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		MyCustomHttpRequestWrapper requestWrapper = new MyCustomHttpRequestWrapper((HttpServletRequest) request);

		String uri = requestWrapper.getRequestURI();
		logger.info("Request URI : {}", uri);
		logger.info("Request Method : {}", requestWrapper.getMethod());
		
		String requestData = new String(requestWrapper.getByteArray()).replaceAll("\n", " ");
		
		if("/start/all".equalsIgnoreCase(uri)){
			AuthenticationRequest req = objectMapper.readValue(requestData, AuthenticationRequest.class);
			req.setUsername("*****");
            requestData = objectMapper.writeValueAsString(req);
        }
		
		logger.info("Request BOdy : {}", requestData);
		
		MyCustomHttpResponseWrapper responseWrapper = new MyCustomHttpResponseWrapper((HttpServletResponse) response);

        chain.doFilter(requestWrapper, responseWrapper);
        
        String responseResult = new String(responseWrapper.getBaos().toByteArray());
//        if("/start/all".equalsIgnoreCase(uri)){
//            User user = objectMapper.readValue(responseResult, User.class);
//            user.setEmail("****");
//
//            responseResult = objectMapper.writeValueAsString(user);
//        }
        
        logger.info("Response Status : {}", responseWrapper.getStatus());
		logger.info("Response BOdy : {}", responseResult);
	}
	
	
	private class MyCustomHttpRequestWrapper extends HttpServletRequestWrapper {

        private byte[] byteArray;

        public MyCustomHttpRequestWrapper(HttpServletRequest request) {
            super(request);
            try {
                byteArray = IOUtils.toByteArray(request.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException("Issue while reading request stream");
            }
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {

            return new MyDelegatingServletInputStream(new ByteArrayInputStream(byteArray));

        }

        public byte[] getByteArray() {
            return byteArray;
        }
    }
	
	private class MyCustomHttpResponseWrapper extends HttpServletResponseWrapper {

        private ByteArrayOutputStream baos = new ByteArrayOutputStream();

        private PrintStream printStream = new PrintStream(baos);

        public ByteArrayOutputStream getBaos() {
            return baos;
        }

        public MyCustomHttpResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return new MyDelegatingServletOutputStream(new TeeOutputStream(super.getOutputStream(), printStream));
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return new PrintWriter(new TeeOutputStream(super.getOutputStream(), printStream));
        }
    }

}

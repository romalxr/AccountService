package account.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        System.out.println("watafaka Locked " + request.getRequestURI());

        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> responseMSG = new LinkedHashMap<>();
        responseMSG.put("timestamp", new Date());
        responseMSG.put("status", HttpStatus.UNAUTHORIZED.value());
        responseMSG.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        responseMSG.put("message", "User account is locked");
        responseMSG.put("path", request.getRequestURI());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(mapper.writeValueAsString(responseMSG));
    }
}

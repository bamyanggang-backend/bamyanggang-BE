package jjon.bamyanggang.login.service;

import java.util.Date;
import java.util.Enumeration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jjon.bamyanggang.login.entity.RefreshEntity;
import jjon.bamyanggang.login.jwt.JwtUtil;
import jjon.bamyanggang.login.repository.RefreshRepository;

@Service
public class ReissueService {

	private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public ReissueService(JwtUtil jwtUtil, RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        // get refresh token
    	Enumeration<String> headerNames = request.getHeaderNames();
    	while (headerNames.hasMoreElements()) {
    	    String headerName = headerNames.nextElement();
    	    String headerValue = request.getHeader(headerName);
    	    System.out.println(headerName + ": " + headerValue);
    	}
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        
        
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
      
                refresh = cookie.getValue();
               
            }
        }

        if (refresh == null) {
            // response status code
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }   

        // expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            // response status code
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }
        
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if(!isExist) {
            // response body
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        // make new JWT
        String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);
        
        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(username, refresh, 86400000L);
        
        response.setHeader("access", newAccess);
        response.addHeader("Authorization", newRefresh);     	
        response.addCookie(createCookie("refresh", newRefresh));
        
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);
        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());
        refreshRepository.save(refreshEntity);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
	    cookie.setSecure(true);
	    //cookie.setPath("/");
	    cookie.setAttribute("SameSite", "None");
	    cookie.setHttpOnly(true);
	    
	    return cookie;
    }
}


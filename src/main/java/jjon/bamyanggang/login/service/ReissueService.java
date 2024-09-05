package jjon.bamyanggang.login.service;

import java.util.Date;
import java.util.Enumeration;

import org.springframework.http.HttpStatus;
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
// JWT를 재발급하는 서비스 클래스입니다.
public class ReissueService {

	private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public ReissueService(JwtUtil jwtUtil, RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
    	
    	// HTTP 요청에서 헤더의 이름을 모두 가져와서 확인하는 메서드입니다.
    	Enumeration<String> headerNames = request.getHeaderNames();
    	while (headerNames.hasMoreElements()) {
    	    String headerName = headerNames.nextElement();
    	    String headerValue = request.getHeader(headerName);
    	    System.out.println(headerName + ": " + headerValue);
    	}
    	
    	// 요청에서 쿠키를 추출하여 refresh 토큰을 가져오는 부분입니다.
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
      
                refresh = cookie.getValue();
               
            }
        }
        // refresh 토큰이 존재하지 않으면 400 에러를 반환합니다.
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
        // 새로운 access와 refresh 토큰을 생성하여 응답 헤더에 설정하고, 쿠키에 새로운 refresh 토큰을 저장하는 부분입니다.
        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        // response 헤더에 새로운 access 토큰과 authorization 정보를 설정하고, 쿠키에 새로운 refresh 토큰을 저장하여 응답합니다.
        String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);
        
        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(username, refresh, 86400000L);
        
        // HTTP 응답에 access 헤더와 Authorization 헤더를 설정합니다.
        response.setHeader("access", newAccess);
        response.addHeader("Authorization", newRefresh);
        // HTTP 응답에 refresh 토큰을 쿠키로 설정합니다.
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
	    cookie.setPath("/");
	    cookie.setAttribute("SameSite", "None");
	    cookie.setHttpOnly(true);
	    /*
	     * httponly 쿠키를 사용시 SameSite를  None 으로 두고 Secure를 true로 하여 사용해야한다.
	     * 
	     */
	    
	    return cookie;
    }
}


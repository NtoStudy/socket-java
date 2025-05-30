package com.socket.socketjava.utils.utils;
import javax.crypto.SecretKey;
import com.socket.socketjava.result.ResultCodeEnum;
import com.socket.socketjava.utils.exception.socketException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class JwtUtil {
    private static SecretKey secretKey = Keys.hmacShaKeyFor("CY29Eb04RPNyQPxACH2jBNWFGn0ypMhc".getBytes());
    public static String createToken(Integer userId, String number) {
        return Jwts.builder()
                .setSubject("LOGIN_USER")
                .setExpiration(new Date(System.currentTimeMillis() + 3600000 * 24 * 365L))
                .claim("userId",userId)
                .claim("number", number)

                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims parseToken(String token) {
        if (token == null) {
            throw new socketException(ResultCodeEnum.ADMIN_LOGIN_AUTH);
        }

        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return claimsJws.getBody();
        } catch (ExpiredJwtException e) {
            throw new socketException(ResultCodeEnum.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new socketException(ResultCodeEnum.TOKEN_INVALID);
        }
    }

}
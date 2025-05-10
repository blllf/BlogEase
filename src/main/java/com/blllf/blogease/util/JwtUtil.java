package com.blllf.blogease.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtil {

    private static final String KEY = "blllf";
	
	//接收业务数据,生成token并返回
    public static String genToken(Map<String, Object> claims) {
        return JWT.create()
                //添加一个名为"claims"的声明（或叫载荷、负载、payload）。但这里有一点需要注意：通常，我们不会将整个载荷部分命名为一个单一的声明键（如"claims"）。
                // 相反，我们会在载荷中添加多个键值对，每个键值对代表一个特定的声明。
                .withClaim("claims", claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60  * 60 * 5 ))
                .sign(Algorithm.HMAC256(KEY));
    }

	//接收token,验证token,并返回业务数据
    public static Map<String, Object> parseToken(String token) {
        return JWT.require(Algorithm.HMAC256(KEY))
                .build()
                .verify(token)
                .getClaim("claims")
                .asMap();
    }

}

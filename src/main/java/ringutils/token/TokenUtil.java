package ringutils.token;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.Claim;

import ringutils.encrypt.MD5Util;

/**
 * JWT由头部、载荷和签名组成
 * 头部：{"type":"JWT","alg":"HS256"}
 * 载荷：标准中注册的声明、公共的声明和私有的声明
 * 签名： （base64后的）header和（base64后的）payload,secret
 * @author ring
 * @date 2017年12月1日 上午8:25:50
 * @version V1.0
 */
public class TokenUtil {
	
	private static Logger log = LoggerFactory.getLogger(MD5Util.class);
	
	/**
	 * 加密代码
	 * @param payload	载荷
	 * @param secret	钥匙
	 * @return
	 * @throws Exception 
	 * @author ring
	 * @date 2017年12月1日 上午8:25:04
	 * @version V1.0
	 */
	public static String encodeToken(JSONObject payload,String secret) throws Exception{
		try {
			Map<String, Object> headerClaims = new HashMap<String, Object>();
			headerClaims.put("typ", "JWT");
			headerClaims.put("alg", "HS256");
			Builder builder = JWT.create().withHeader(headerClaims);
			Iterator<String> its = headerClaims.keySet().iterator();
			while(its.hasNext()){
				String key = its.next();
				String value = payload.getString(key);
				builder.withClaim(key, value);
			}
			return builder.sign(Algorithm.HMAC256(secret));
		} catch (IllegalArgumentException | JWTCreationException | UnsupportedEncodingException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	
	/**
	 * 解码代码
	 * @param token		加密代码
	 * @param secret	钥匙
	 * @return
	 * @throws Exception 
	 * @author ring
	 * @date 2017年12月1日 上午8:32:27
	 * @version V1.0
	 */
	public static Map<String, Claim> decodedToken(String token,String secret) throws Exception{
		try {
			JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secret)).build();
			return jwtVerifier.verify(token).getClaims();
		} catch (IllegalArgumentException | UnsupportedEncodingException e) {
			log.error(e.getMessage(),e);
			throw e;
		}		
	}
	
}

package app.config;

import io.jsonwebtoken.SignatureAlgorithm;

public class JwtConfig {

	//Parâmetros para geração do token
	public static final String SECRET_KEY = "b4cE9yz6t1T4jxGkXj9qXr5FJuyTGhnOPW1BMv6Z4V73gkCxgHv1fVSu10tKv";
	public static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
	public static final int TOKEN_EXPIRATION_TIME = 1;

}

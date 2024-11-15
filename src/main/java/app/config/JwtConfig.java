package app.config;

import io.jsonwebtoken.SignatureAlgorithm;

public class JwtConfig {

	//Parâmetros para geração do token
	public static final String SECRET_KEY = "UMACHAVESECRETADASUAAPIAQUIUMACHAVESECRETADASUAAPIAQUIUMACHAVESECRETADASUAAPIAQUIUMACHAVESECRETADASUAAPIAQUI";
	public static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
	public static final int TOKEN_EXPIRATION_TIME = 1;

}

package org.api_sync.adapter.inbound.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class EncryptionUtil {

	private final SecretKeySpec secretKey;
	
	public EncryptionUtil(@Value("${encryption.secret-key}") String secret) {
		this.secretKey = new SecretKeySpec(secret.getBytes(), "AES");
	}
	
	public String encrypt(String strToEncrypt) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			throw new RuntimeException("Error al encriptar", e);
		}
	}
	
	public String decrypt(String strToDecrypt) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
			throw new RuntimeException("Error al desencriptar", e);
		}
	}
}
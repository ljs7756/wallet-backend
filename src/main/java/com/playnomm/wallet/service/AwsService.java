package com.playnomm.wallet.service;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.PropertiesFileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.services.kms.model.EncryptResult;
import com.amazonaws.services.kms.model.EncryptionAlgorithmSpec;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author : hzn
 * @date : 2022/12/14
 * @description :
 */
@Service
public class AwsService {
	@Value("${cloud.aws.kms.encrypt.keyId}")
	private       String encKeyId;

	public String awsKmsEncrypt (String plaintext) {
		AWSKMS kmsClient = AWSKMSClientBuilder.standard ().withRegion (Regions.AP_NORTHEAST_2)
//				.withCredentials (new ClasspathPropertiesFileCredentialsProvider ("application.properties"))
				.build ();
		EncryptRequest request = new EncryptRequest ();
		request.withKeyId (encKeyId);
		request.withPlaintext (ByteBuffer.wrap (plaintext.getBytes (StandardCharsets.UTF_8)));
		request.withEncryptionAlgorithm (EncryptionAlgorithmSpec.RSAES_OAEP_SHA_256);
		EncryptResult result = kmsClient.encrypt (request);
		ByteBuffer ciphertextBlob = result.getCiphertextBlob ();
		return new String (Base64.encodeBase64 (ciphertextBlob.array ()));
	}

	public String awsKmsDecrypt (String encryptedText) {
		AWSKMS kmsClient = AWSKMSClientBuilder.standard ().withRegion (Regions.AP_NORTHEAST_2)
//				.withCredentials (new ClasspathPropertiesFileCredentialsProvider ("application.properties"))
				.build ();
		DecryptRequest request = new DecryptRequest ();
		request.withCiphertextBlob (ByteBuffer.wrap (org.apache.commons.codec.binary.Base64.decodeBase64 (encryptedText)));
		request.withKeyId (encKeyId);
		request.withEncryptionAlgorithm (EncryptionAlgorithmSpec.RSAES_OAEP_SHA_256);
		ByteBuffer plainText = kmsClient.decrypt (request).getPlaintext ();
		return new String (plainText.array ());
	}
}

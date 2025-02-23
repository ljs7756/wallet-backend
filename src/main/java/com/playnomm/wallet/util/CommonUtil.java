package com.playnomm.wallet.util;

import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.exception.WalletException;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author : hzn
 * @date : 2022/11/07
 * @description :
 */
public class CommonUtil {
	private static final DateTimeFormatter bcFormatter = DateTimeFormatter.ofPattern ("yyyy-MM-dd'T'HH:mm:ss");
	private static final DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern ("yyyy-MM-dd HH:mm:ss");

	private static final Random rn;

	static {
		try {
			rn = SecureRandom.getInstanceStrong ();
		} catch (NoSuchAlgorithmException e) {
			throw new WalletException (StatusCode.INTERNAL_SERVER_ERROR);
		}
	}


	public static String getClientIP (HttpServletRequest request) {
		String ip = request.getHeader ("X-Forwarded-For");
		if (ip == null) ip = request.getHeader ("Proxy-Client-IP");
		if (ip == null) ip = request.getHeader ("WL-Proxy-Client-IP");
		if (ip == null) ip = request.getHeader ("HTTP_CLIENT_IP");
		if (ip == null) ip = request.getHeader ("HTTP_X_FORWARDED_FOR");
		if (ip == null) ip = request.getRemoteAddr ();
		return ip;
	}

	public static String getUUID (String type) {
		switch (type) {
			case "L":
				return UUID.randomUUID ().toString ().toLowerCase ();
			case "U":
				return UUID.randomUUID ().toString ().toUpperCase ();
			default:
				String uuid = UUID.randomUUID ().toString ().toUpperCase ();

				Random rn = new Random ();
				String[] arr = uuid.split ("(?<!^)");
				for (int i = 0; i <= 16; i++) {
					int num = rn.nextInt (35);
					arr[num] = arr[num].toLowerCase ();
				}
				return Arrays.stream (arr).collect (Collectors.joining ());
		}
	}

	public static String getRandomCode (int len) {
		char[] charaters = {
				'A',
				'B',
				'C',
				'D',
				'E',
				'F',
				'G',
				'H',
				'I',
				'J',
				'K',
				'L',
				'M',
				'N',
				'O',
				'P',
				'Q',
				'R',
				'S',
				'T',
				'U',
				'V',
				'W',
				'X',
				'Y',
				'Z',
				'a',
				'b',
				'c',
				'd',
				'e',
				'f',
				'g',
				'h',
				'i',
				'j',
				'k',
				'l',
				'm',
				'n',
				'o',
				'p',
				'q',
				'r',
				's',
				't',
				'u',
				'v',
				'w',
				'x',
				'y',
				'z',
				'0',
				'1',
				'2',
				'3',
				'4',
				'5',
				'6',
				'7',
				'8',
				'9'
		};
		StringBuffer sb = new StringBuffer ();
		Random rn = new Random ();
		for (int i = 0; i < len; i++) {
			sb.append (charaters[rn.nextInt (charaters.length)]);
		}
		return sb.toString ();
	}

	/**
	 * 소수 자리수 padding
	 *
	 * @param input
	 * @param scale
	 * @return
	 */
	public static String padding (String input, int scale) {
		if (input == null) return null;

		String[] temp = input.split ("\\.");
		StringBuilder sb = new StringBuilder (temp[0]);
		if (temp.length > 1) {
			String decimal = temp[1];
			int padCount = scale - decimal.length ();
			if (padCount > 0) {
				sb.append (".").append (decimal);
				for (int i = 0; i < padCount; i++) {
					sb.append (0);
				}
			} else if (padCount < 0) {
				char[] chars = decimal.toCharArray ();
				for (int i = 0; i < scale; i++) {
					sb.append (chars[i]);
				}
			} else {
				sb.append (".").append (decimal);
			}
		} else {
			for (int i = 0; i < scale; i++) {
				sb.append (0);
			}
		}

		String[] temp2 = sb.toString ().split ("\\.");
		if (temp2.length > 1) {
			if (temp2[0].equals ("0")) {
				sb.setLength (0);
				sb.append (temp2[1]);
			}
		}

		return sb.toString ();
	}

	/**
	 * 소수 자리수 unPadding
	 *
	 * @param input
	 * @return
	 */
	public static String unPadding (String input) {
		if (input == null) return null;

		String[] temp = input.split ("\\.");
		StringBuilder sb = new StringBuilder (temp[0]);
		if (temp.length > 1) {
			String decimal = temp[1].replaceAll ("0", "");
			if (!ObjectUtils.isEmpty (decimal)) sb.append (".").append (decimal);
		}

		return sb.toString ();
	}

	public static LocalDateTime now () {
		return LocalDateTime.now (ZoneId.systemDefault ());
	}

	public static String bcNow () {
		return LocalDateTime.now (ZoneId.systemDefault ()).format (bcFormatter).concat ("Z");
	}

	public static String bcNow (LocalDateTime now) {
		return now.format (bcFormatter).concat ("Z");
	}

	public static String dbNow () {
		return LocalDateTime.now (ZoneId.systemDefault ()).format (dbFormatter);
	}

	public static String dbNow (LocalDateTime now) {
		return now.format (dbFormatter);
	}

	/**
	 * 가상자산 거래 번호 생성
	 *
	 * @param tradeTyCode
	 * @param localDateTime
	 * @param userSn
	 * @return
	 */
	public static String createCxTradeNo (String tradeTyCode, LocalDateTime localDateTime, String userSn) {
		StringBuilder sb = new StringBuilder ();
		sb.append (tradeTyCode).append (localDateTime.format (DateTimeFormatter.ofPattern ("yyyyMMddHHmmss"))).append ("-");
		sb.append (rn.nextInt (10000)).append (userSn);
		return sb.toString ();
	}

	/**
	 * 전송 거래 유형 상세 코드
	 *
	 * @param fromNetworkId
	 * @param toNetworkId
	 * @param type
	 * @return
	 */
	public static String getTransferTradeTyDetailCode (String fromNetworkId, String toNetworkId, String type, String tradeTyCode) {
		String result = "";
		if ("1002".equals (fromNetworkId) && "1001".equals (toNetworkId)) {
			if ("from".equals (type)) result = "BEF";
			else result = "BET";
		} else if ("1002".equals (fromNetworkId) && "1000".equals (toNetworkId)) {
			if ("from".equals (type)) result = "BLF";
			else result = "BLT";
		} else if ("1002".equals (fromNetworkId) && "1002".equals (toNetworkId)) {
			if ("from".equals (type)) result = "BBF";
			else result = "BBT";
		} else if ("1001".equals (fromNetworkId) && "1002".equals (toNetworkId)) {
			if ("from".equals (type)) result = "EBF";
			else result = "EBT";
		} else if ("1001".equals (fromNetworkId) && "1000".equals (toNetworkId)) {
			if ("from".equals (type)) result = "ELF";
			else result = "ELT";
		} else if ("1001".equals (fromNetworkId) && "1001".equals (toNetworkId)) {
			if ("from".equals (type)) result = "EEF";
			else result = "EET";
		} else if ("1000".equals (fromNetworkId) && "1002".equals (toNetworkId)) {
			if ("from".equals (type)) result = "LBF";
			else result = "LBT";
		} else if ("1000".equals (fromNetworkId) && "1001".equals (toNetworkId)) {
			if ("from".equals (type)) result = "LEF";
			else result = "LET";
		} else if ("1000".equals (fromNetworkId) && "1000".equals (toNetworkId)) {
			if ("from".equals (type)) result = "LLF";
			else result = "LLT";
		}
		return tradeTyCode + result;
	}

	private static String bytesToHex(byte[] bytes) {
		StringBuilder builder = new StringBuilder();
		for (byte b : bytes) {
			builder.append(String.format("%02x", b));
		}
		return builder.toString();
	}

	public static String getSHA256(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(text.getBytes(StandardCharsets.UTF_8));

			return bytesToHex(md.digest());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public static String modifyWalletAddress(String cxwaletAdres){

		String rs = "";

		if(cxwaletAdres.substring(0,2).equals("0x")){
			rs = cxwaletAdres.substring(2);
		}else{
			rs = cxwaletAdres;
		}
		return rs;
	}

}

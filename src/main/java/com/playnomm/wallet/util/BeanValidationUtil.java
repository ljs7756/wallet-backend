package com.playnomm.wallet.util;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanValidationUtil {

	public static Map<String, String> getErrorMap (Errors errors) {
		Map<String, String> errorMap = new HashMap<> ();
		List<FieldError> fieldErrorList = errors.getFieldErrors ();
		fieldErrorList.stream ().forEach (error -> errorMap.put (error.getField (), error.getDefaultMessage ()));
		return errorMap;
	}
}

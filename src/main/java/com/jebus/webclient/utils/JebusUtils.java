package com.jebus.webclient.utils;

import java.util.Random;
import jakarta.faces.context.FacesContext;
import jakarta.el.ValueExpression;
import jakarta.el.ExpressionFactory;
import jakarta.el.ELContext;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class JebusUtils {
	
	private static final Random rng = new Random();

	public static synchronized String generate(int length) {
		StringBuilder sb = new StringBuilder();
		for (int c = 0; c < length; c++) {
			sb.append(rng.nextInt(10));
		}
		return sb.toString();
	}
	
	public static synchronized <T> T evaluateVE(FacesContext fc, String expr, Class<T> rType) {
		ELContext ectx = fc.getELContext();
		ExpressionFactory factory = fc.getApplication().getExpressionFactory();
		ValueExpression ve = factory.createValueExpression(ectx, expr, rType);
		Object result = ve.getValue(ectx);
		return rType.cast(result);
	}
	
	public static synchronized String hash(String input) {
		StringBuilder hash = new StringBuilder("\\x");
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] data = md.digest(input.getBytes());
            for (byte b : data) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
        } catch (NoSuchAlgorithmException ex) {
            // handle exception
        }
        return hash.toString();
	}

}

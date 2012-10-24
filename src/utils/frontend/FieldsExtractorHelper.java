package com.s5a.utils.frontend;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class FieldsExtractorHelper {

	private static final String PUBLIC_PREFIX = "public ";
	private static final String GET = "get";
	private static final String IS = "is";
	private static final String FIELDS = "fields";

	/**
	 * Gets the getters of a pojo as a map of {@link String} as key and {@link Method} as value.
	 */
	public static Map<String, String> getFields(Class<?> pojoClass) {
		HashMap<String, String> keyValues = new HashMap<String, String>();
		fillGetterMethods(pojoClass, keyValues);

		if (keyValues.isEmpty())
			return null;
		else
			return keyValues;
	}

	private static void fillGetterMethods(Class<?> pojoClass, Map<String, String> baseMap) {
		if (pojoClass != null && pojoClass.getSuperclass() != Object.class)
			fillGetterMethods(pojoClass.getSuperclass(), baseMap);

		if (pojoClass != null && pojoClass.getDeclaredMethods() != null) {

			List<String> fieldList = new ArrayList<String>();
			Field[] fields = pojoClass.getFields();
			for (Field field : fields) {
				fieldList.add(field.getName());
			}

			if (fieldList.size() > 0)
				baseMap.put(FIELDS, fieldList.toString());

			Method[] methods = pojoClass.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++) {
				Method m = methods[i];
				if (!Modifier.isStatic(m.getModifiers()) && m.getParameterTypes().length == 0
						&& m.getReturnType() != null && Modifier.isPublic(m.getModifiers())) {
					String name = m.getName();
					String value = m.toGenericString().toString().replace(PUBLIC_PREFIX, StringUtils.EMPTY);
					if (name.startsWith(IS))
						baseMap.put(toProperty(IS.length(), name), value);
					else if (name.startsWith(GET))
						baseMap.put(toProperty(GET.length(), name), value);
				}
			}

		}
	}

	/**
	 * Converts a method name into a camel-case field name, starting from {@code start}.
	 */
	private static String toProperty(int start, String methodName) {
		char[] prop = new char[methodName.length() - start];
		methodName.getChars(start, methodName.length(), prop, 0);
		int firstLetter = prop[0];
		prop[0] = (char) (firstLetter < 91 ? firstLetter + 32 : firstLetter);
		return new String(prop);
	}
}

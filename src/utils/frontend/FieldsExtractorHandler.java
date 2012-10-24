package com.s5a.utils.frontend;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang.StringUtils;

import com.s5a.checkout.utils.JsonUtils;
import com.s5a.logging.Logger;
import com.s5a.logging.LoggerFactory;

public class FieldsExtractorHandler {

	private static final Logger LOGGER = LoggerFactory.get().getLogger(FieldsExtractorHandler.class);

	private static final String CLASS_EXTENSION = ".class";
	private static final String EXCLUDE_TEST_CLASSES = "Test";
	private final static String packageName = "com.s5a";

	// These classes need to be in the "black list" because they blow up the logic since they
	// depends on BM directly
	private static final List<String> BLACK_LIST = new ArrayList<String>() {

		private static final long serialVersionUID = 7537495041895060650L;
		{
			add("com.s5a.cache.CacheInfo");
			add("com.s5a.cache.CacheOperations");
			add("com.s5a.checkout.domain.AdditionalProductInfo");
			add("com.s5a.checkout.domain.BillingAddress");
			add("com.s5a.checkout.domain.GiftCardTest");
			add("com.s5a.checkout.domain.Gifting");
			add("com.s5a.checkout.domain.GiftWrap");
			add("com.s5a.checkout.domain.SaksAddress");
			add("com.s5a.checkout.domain.SaksUpsell");
			add("com.s5a.checkout.domain.ShippingAddress");
			add("com.s5a.constants.Links");
			add("com.s5a.fedex.action.FedExDeliveryDateCalculator");
			add("com.s5a.fedex.FedexConstants");
			add("com.s5a.htmlapp.SaksContent");
			add("com.s5a.inventory.webservice.YIFWebService_PortType");
			add("com.s5a.inventory.webservice.YIFWebServiceException");
			add("com.s5a.inventory.webservice.YIFWebServiceSoapBindingStub");
			add("com.s5a.tokenization.TokenizationService");
		}
	};

	private final static String BEGIN_EXCLUSION = "common."; 
			
	private final static ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	private final static String path = packageName.replace('.', '/');

	public static Map<String, String> exposeObjectsAndFields() {
		
		Map<String, String> map = new TreeMap<String, String>();
		
		List<String> list = exposeObjectsFromJar();
		for(String clazz: list) {
			clazz = clazz.replace(CLASS_EXTENSION, StringUtils.EMPTY);
			String json = showFields(clazz);
			if (json != null && !json.contains("null")) {
				map.put(clazz, json);
			}
		}
		
		return map;
	}
	
	public static List<String> exposeObjects() {

		try {
			// Gets all sources under the package com.s5a
			final List<File> dirs = extractResources();

			// For every source extract the package + class name
			final List<String> classes = new ArrayList<String>();
			for (File directory : dirs) {
				classes.addAll(findClasses(directory, packageName));
			}

			return classes;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static List<File> extractResources() throws IOException {
		final List<File> dirs = new ArrayList<File>();
		Enumeration<URL> resources = classLoader.getResources(path);
		while (resources.hasMoreElements()) {
			final URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		return dirs;
	}

	public static List<String> exposeObjectsFromJar() {

		try {
			final List<File> dirs = extractResources();

			// For every source extract the package + class name
			final List<String> classes = new ArrayList<String>();
			for (File directory : dirs) {
				if (directory.getPath().contains(".jar")) {
					String path  = directory.getPath().replace("file:", StringUtils.EMPTY).split("!")[0];
					JarFile jarFile = new JarFile(path);
		            Enumeration<JarEntry> allEntries = jarFile.entries();
		            while (allEntries.hasMoreElements()) {
		                JarEntry entry = (JarEntry) allEntries.nextElement();
		                String name = entry.getName();
		                
		                if (name.endsWith(CLASS_EXTENSION)) {
		                	classes.add(name.replace("/", "."));
		                }
		            }					
				}
			}

			return classes;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static String showFields(String clazz) {
		try {
			if (!BLACK_LIST.contains(clazz) && !clazz.endsWith(EXCLUDE_TEST_CLASSES)
					&& !clazz.startsWith(BEGIN_EXCLUSION)) {
				final Class<?> theClass = Class.forName(clazz);

				if (!theClass.isInterface()) {
					// Convert class into map that brings the fields and methods
					final Map<String, String> map = FieldsExtractorHelper.getFields(theClass);

					return JsonUtils.toJsonString(map);
				}
			}
		} catch (Exception e) {
			LOGGER.info(FieldsExtractorHandler.class, "Invalid object package or name: " + clazz);
		}

		return null;
	}

	/**
	 * Given a prefix package returns a class list
	 * 
	 * @param directory
	 * @param packageName
	 * @return
	 * @throws ClassNotFoundException
	 */
	private static List<String> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<String> classes = new ArrayList<String>();
		if (!directory.exists()) {
			return classes;
		}

		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(CLASS_EXTENSION)) {
				classes.add(packageName + '.' + file.getName().replace(CLASS_EXTENSION, StringUtils.EMPTY));
			}
		}
		return classes;
	}

}

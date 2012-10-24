package com.s5a.utils.frontend;

import java.util.List;

import junit.framework.TestCase;

public class FieldsExtractorHandlerTest extends TestCase {

	public void testShouldBringFieldsList() {

		List<String> list = FieldsExtractorHandler.exposeObjects();

		assertNotNull(list);
		assertTrue(list.size() > 0);
	}

	public void testShouldBringObjectAsJson() {

		String json = FieldsExtractorHandler.showFields("com.s5a.assortments.domain.AssortmentBean");

		assertNotNull(json);
		assertTrue(json.length() > 0);
	}

	public void testShouldNotBringObjectAsJsonWhenPackageIsWrong() {

		String json = FieldsExtractorHandler.showFields("com.s5a.assortments.domain.AssortmentBeanss");
		assertNull(json);
	}

	public void testShouldNotBringObjectAsJsonWhenIsNull() {

		String json = FieldsExtractorHandler.showFields(null);
		assertNull(json);
	}

	public void testShouldNotBringObjectAsJsonWhenIsEmpty() {

		String json = FieldsExtractorHandler.showFields("");
		assertNull(json);
	}

	public void testShouldBringFieldsListWhenCalledFromJar() {

		List<String> list = FieldsExtractorHandler.exposeObjectsFromJar();
		
		assertNotNull(list);
		assertTrue(list.size() > 0);
	}
	
}

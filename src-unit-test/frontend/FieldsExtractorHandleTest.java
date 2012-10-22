package com.s5a.utils.frontend;

import java.util.List;

import junit.framework.TestCase;

public class FieldsExtractorHandleTest extends TestCase {

	public void testShouldBringFieldsList() {

		List<String> list = FieldsExtractorHandle.exposeObjects();

		assertNotNull(list);
		assertTrue(list.size() > 0);
	}

	public void testShouldBringObjectAsJson() {

		String json = FieldsExtractorHandle.showFields("com.s5a.assortments.domain.AssortmentBean");

		assertNotNull(json);
		assertTrue(json.length() > 0);
	}

	public void testShouldNotBringObjectAsJsonWhenPackageIsWrong() {

		String json = FieldsExtractorHandle.showFields("com.s5a.assortments.domain.AssortmentBeanss");
		assertNull(json);
	}

	public void testShouldNotBringObjectAsJsonWhenIsNull() {

		String json = FieldsExtractorHandle.showFields(null);
		assertNull(json);
	}

	public void testShouldNotBringObjectAsJsonWhenIsEmpty() {

		String json = FieldsExtractorHandle.showFields("");
		assertNull(json);
	}
	
}

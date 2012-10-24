package com.s5a.utils.frontend;

import java.util.Map;

import junit.framework.TestCase;

import com.s5a.assortments.domain.AssortmentBean;

public class FieldsExtractorHelperTest extends TestCase {

	public void testShouldBringMapWithFields() {

		Map<String, String> map = FieldsExtractorHelper.getFields(AssortmentBean.class);
		
		assertNotNull(map);
		assertTrue(map.size() > 0);
		assertTrue(map.containsKey("name"));
	}

	public void testNotShouldBringMapWhenIsNull() {

		Map<String, String> map = FieldsExtractorHelper.getFields(null);

		assertNull(map);
	}

	public void testShouldBringMapWhenPassedObject() {

		Map<String, String> map = FieldsExtractorHelper.getFields(Object.class);

		assertNotNull(map);
		assertTrue(map.containsKey("class"));
	}

}

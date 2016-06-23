package com.wnc.toutiao;

import com.wnc.basic.BasicDateUtil;
import com.wnc.tools.SoupUtil;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	public AppTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	public void testApp() {
		System.out.println("test:" + BasicDateUtil.getCurrentDateFormatString("yyyy-MM-dd"));
		System.out.println("test:" + BasicDateUtil.getDateBeforeDayDateString("20150909", 1));
		System.out.println(SoupUtil.getDoc("http://toutiao.io/j/lpqy1a"));
		assertTrue(true);
	}
}

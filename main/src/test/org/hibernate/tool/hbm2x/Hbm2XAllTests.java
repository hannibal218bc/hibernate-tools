package org.hibernate.tool.hbm2x;

import org.hibernate.tool.test.jdbc2cfg.identity.H2IdentityTest;
import org.hibernate.tool.test.jdbc2cfg.identity.HSQLIdentityTest;
import org.hibernate.tool.test.jdbc2cfg.identity.MySQLIdentityTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class Hbm2XAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.hibernate.tool.hbm2x");
		
		suite.addTestSuite(H2IdentityTest.class);
		suite.addTestSuite(MySQLIdentityTest.class);
		suite.addTestSuite(HSQLIdentityTest.class);
		return suite;
	}

}

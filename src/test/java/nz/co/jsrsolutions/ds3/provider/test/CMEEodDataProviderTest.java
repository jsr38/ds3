package nz.co.jsrsolutions.ds3.provider.test;

import static org.junit.Assert.*;

import nz.co.jsrsolutions.ds3.provider.CMEEodDataProvider;
import nz.co.jsrsolutions.ds3.provider.CMEEodDataProviderExchangeDescriptor;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CMEEodDataProviderTest {

	private CMEEodDataProviderExchangeDescriptor[] _descriptors;
	private CMEEodDataProvider _provider;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		CMEEodDataProviderExchangeDescriptor nymexFutures = new CMEEodDataProviderExchangeDescriptor(
				"NYMEX-Futures", "nymex_future.csv");

		_descriptors = new CMEEodDataProviderExchangeDescriptor[] { nymexFutures };
		_provider = new CMEEodDataProvider("ftp.cmegroup.com", "/pub/settle",
				_descriptors);

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCMEEodDataProvider() {
		
	}

	@Test
	public void testGetExchangeMonths() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetQuotesStringStringCalendarCalendarString() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetExchangeDateRange() {
		fail("Not yet implemented");
	}

}

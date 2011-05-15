package net.czpilar.jdbf.fields;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import net.czpilar.jdbf.context.JDBFContext;
import net.czpilar.jdbf.enums.JDBFSupportedDbaseVersion;

import org.junit.Test;

public class JRowTest {

	private static final String ENCODING = JDBFContext.getDBFEncoding(JDBFSupportedDbaseVersion.DBASE_VII);

	@Test
	public void testAsLong() {

		Long actual = JRow.asLong(null);
		assertNull(actual);

		actual = JRow.asLong(new byte[] {});
		assertNull(actual);

		byte[] bytes = new byte[] { -128, 0, 65 };
		actual = JRow.asLong(bytes);
		assertNull(actual);

		bytes = new byte[] { -128, 0, 65, 95 };
		actual = JRow.asLong(bytes);
		assertNotNull(actual);
		assertEquals(16735, actual.longValue());

		bytes = new byte[] { 0, 0, 65, 95 };
		actual = JRow.asLong(bytes);
		assertNotNull(actual);
		assertEquals(-16735, actual.longValue());
	}

	@Test
	public void testAsLongString() {

		Long actual = JRow.asLongString(null);
		assertNull(actual);

		actual = JRow.asLongString(new byte[] {});
		assertNull(actual);

		// ABCD
		byte[] bytes = new byte[] { 65, 66, 67, 68 };
		actual = JRow.asLongString(bytes);
		assertNull(actual);

		// 1234
		bytes = new byte[] { 49, 50, 51, 52 };
		actual = JRow.asLongString(bytes);
		assertNotNull(actual);
		assertEquals(1234, actual.longValue());
	}

	@Test
	public void testAsCalendar() {

		Calendar actual = JRow.asCalendar(null);
		assertNull(actual);

		actual = JRow.asCalendar(new byte[] {});
		assertNull(actual);

		byte[] bytes = new byte[] { 50, 48, 49, 48, 49, 50, 50 };
		actual = JRow.asCalendar(bytes);
		assertNull(actual);

		// 28.12.2010
		bytes = new byte[] { 50, 48, 49, 48, 49, 50, 50, 56 };
		Calendar expected = Calendar.getInstance();
		expected.clear();
		expected.set(2010, 12 - 1, 28);
		actual = JRow.asCalendar(bytes);
		assertNotNull(actual);
		assertEquals(expected, actual);
	}

	@Test
	public void testAsDouble() {

		Double actual = JRow.asDouble(null);
		assertNull(actual);

		actual = JRow.asDouble(new byte[] {});
		assertNull(actual);

		byte[] bytes = new byte[] { -64, 121, 64, 0, 0 };
		actual = JRow.asDouble(new byte[] {});
		assertNull(actual);

		// 404
		bytes = new byte[] { -64, 121, 64, 0, 0, 0, 0, 0 };
		actual = JRow.asDouble(bytes);
		assertNotNull(actual);
		assertEquals(404, actual.doubleValue(), 0);

		// 127.97
		bytes = new byte[] { -64, 95, -2, 20, 122, -31, 71, -82 };
		actual = JRow.asDouble(bytes);
		assertNotNull(actual);
		assertEquals(127.97, actual.doubleValue(), 0);

		// 1041.87
		bytes = new byte[] { -64, -112, 71, 122, -31, 71, -82, 21 };
		actual = JRow.asDouble(bytes);
		assertNotNull(actual);
		assertEquals(1041.87, actual.doubleValue(), 0);
	}

	@Test
	public void testAsDoubleString() {

		Double actual = JRow.asDoubleString(null);
		assertNull(actual);

		actual = JRow.asDoubleString(new byte[] {});
		assertNull(actual);

		// ABCD
		byte[] bytes = new byte[] { 65, 66, 67, 68 };
		actual = JRow.asDoubleString(bytes);
		assertNull(actual);

		// 1234.56
		bytes = new byte[] { 49, 50, 51, 52, 46, 53, 54 };
		actual = JRow.asDoubleString(bytes);
		assertNotNull(actual);
		assertEquals(1234.56, actual.doubleValue(), 0);
	}

	@Test
	public void testAsString() {

		String actual = JRow.asString(null, ENCODING);
		assertNull(actual);

		actual = JRow.asString(new byte[] {}, ENCODING);
		assertNull(actual);

		byte[] bytes = new byte[] { 116, 101, 115, 116, 111, 118, 97, 99, 105, 32, 115, 116, 114, 105, 110,
				103 };
		actual = JRow.asString(bytes, ENCODING);
		assertNotNull(actual);
		assertEquals("testovaci string", actual);
	}

	@Test
	public void testAsBoolean() {

		Boolean actual = JRow.asBoolean(null);
		assertNull(actual);

		actual = JRow.asBoolean(new byte[] {});
		assertNull(actual);

		actual = JRow.asBoolean(new byte[] { 84, 84 });
		assertNull(actual);

		// A
		byte[] bytes = new byte[] { 65 };
		actual = JRow.asBoolean(bytes);
		assertNull(actual);

		// T
		bytes = new byte[] { 84 };
		actual = JRow.asBoolean(bytes);
		assertNotNull(actual);
		assertTrue(actual.booleanValue());

		// F
		bytes = new byte[] { 70 };
		actual = JRow.asBoolean(bytes);
		assertNotNull(actual);
		assertFalse(actual.booleanValue());
	}
}

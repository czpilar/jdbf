package net.czpilar.jdbf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class JDBFReaderTest {

	@Test
	public void testJDBFReaderStringDbase7() {

		JDBFReader reader7 = new JDBFReader("data/2010-02-27_dbase_7/Zbozi_A.dbf");
		assertNotNull(reader7);
		assertEquals(33, reader7.getHeaderSize());
		assertEquals(18, reader7.getRowCount());
	}

	@Test
	public void testJDBFReaderStringDbase5() {

		JDBFReader reader5 = new JDBFReader("data/2010-03-25_dbase_5/Zbozi_A.dbf");
		assertNotNull(reader5);
		assertEquals(33, reader5.getHeaderSize());
		assertEquals(23, reader5.getRowCount());
	}

}

package net.czpilar.jdbf;

import net.czpilar.jdbf.enums.JDBFSupportedDbaseVersion;
import net.czpilar.jdbf.exceptions.JDBFException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JDBFReaderTest {

    private static final Map<JDBFSupportedDbaseVersion, String> DBF_ENCODING = Map.of(
            JDBFSupportedDbaseVersion.DBASE_V, "CP852",
            JDBFSupportedDbaseVersion.DBASE_VII, "CP1250"
    );

    private final JDBFCharsetProvider provider = DBF_ENCODING::get;

    @Test
    public void testJDBFReaderStringDbase7() {
        JDBFReader reader7 = new JDBFReader.Builder("src/test/resources/data/2010-02-27_dbase_7/Zbozi_A.dbf")
                .withCharsetProvider(provider)
                .build();

        assertNotNull(reader7);
        assertEquals(33, reader7.getHeaderSize());
        assertEquals(18, reader7.getRowCount());
    }

    @Test
    public void testJDBFReaderStringDbase5() {
        JDBFReader reader5 = new JDBFReader.Builder("src/test/resources/data/2010-03-25_dbase_5/Zbozi_A.dbf")
                .withCharsetProvider(provider)
                .build();

        assertNotNull(reader5);
        assertEquals(33, reader5.getHeaderSize());
        assertEquals(23, reader5.getRowCount());
    }

    @Test
    public void testJDBFReaderStringDbase7EndByteProblem() {
        JDBFReader reader = new JDBFReader.Builder("src/test/resources/data/2012-04-16_dbase_7/Zbozi_Anef.dbf")
                .withCharsetProvider(provider)
                .build();

        assertNotNull(reader);
        assertEquals(33, reader.getHeaderSize());
        assertEquals(24, reader.getRowCount());
    }

    @Test
    public void testJDBFReaderStringDbase7EndByteProblem2() {
        JDBFReader reader = new JDBFReader.Builder("src/test/resources/data/2012-04-16_dbase_7/Zbozi_A.dbf")
                .withCharsetProvider(provider)
                .build();

        assertNotNull(reader);
        assertEquals(33, reader.getHeaderSize());
        assertEquals(24, reader.getRowCount());
    }

    @Test
    public void testJDBFReaderReadWithFile() {
        JDBFReader reader7 = new JDBFReader.Builder(new File("src/test/resources/data/2010-02-27_dbase_7/Zbozi_A.dbf"))
                .withCharsetProvider(provider)
                .build();

        assertNotNull(reader7);
        assertEquals(33, reader7.getHeaderSize());
        assertEquals(18, reader7.getRowCount());
    }

    @Test
    public void testJDBFReaderReadWithBytes() throws IOException {
        JDBFReader reader7 = new JDBFReader.Builder(Files.readAllBytes(new File("src/test/resources/data/2010-02-27_dbase_7/Zbozi_A.dbf").toPath()))
                .withCharsetProvider(provider)
                .build();

        assertNotNull(reader7);
        assertEquals(33, reader7.getHeaderSize());
        assertEquals(18, reader7.getRowCount());
    }

    @Test
    public void testJDBFReaderReadWithBytesNoBuilder() throws IOException {
        JDBFReader reader7 = new JDBFReader(Files.readAllBytes(new File("src/test/resources/data/2010-02-27_dbase_7/Zbozi_A.dbf").toPath()), provider);

        assertNotNull(reader7);
        assertEquals(33, reader7.getHeaderSize());
        assertEquals(18, reader7.getRowCount());
    }

    @Test
    public void testJDBFReaderWithDefaultProvider() {
        JDBFReader reader7 = new JDBFReader.Builder("src/test/resources/data/2010-02-27_dbase_7/Zbozi_A.dbf").build();

        assertNotNull(reader7);
        assertEquals(33, reader7.getHeaderSize());
        assertEquals(18, reader7.getRowCount());
    }

    @Test
    public void testJDBFReaderWithNullInputBytes() {
        assertThrows(JDBFException.class, () -> new JDBFReader(null, provider));
    }

    @Test
    public void testJDBFReaderWithNullEmptyInputBytes() {
        assertThrows(JDBFException.class, () -> new JDBFReader(new byte[]{}, provider));
    }

}

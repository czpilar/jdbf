package net.czpilar.jdbf;

import net.czpilar.jdbf.context.JDBFContext;
import net.czpilar.jdbf.enums.JDBFSupportedDbaseVersion;
import net.czpilar.jdbf.exceptions.JDBFException;
import net.czpilar.jdbf.fields.JDBFCharsetProvider;
import net.czpilar.jdbf.fields.JHeaderField;
import net.czpilar.jdbf.fields.JRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static net.czpilar.jdbf.context.JDBFContext.BYTE_HEADER_END;
import static net.czpilar.jdbf.context.JDBFContext.DEFAULT_CHARSET_PROVIDER;

public class JDBFReader {

    private static final Logger LOG = LoggerFactory.getLogger(JDBFReader.class);

    public static class Builder {

        private File file;
        private byte[] bytes;
        private JDBFCharsetProvider charsetProvider = DEFAULT_CHARSET_PROVIDER;

        public Builder(String fileName) {
            this(new File(fileName));
        }

        public Builder(File file) {
            this.file = file;
        }

        public Builder(byte[] bytes) {
            this.bytes = bytes;
        }

        public Builder withCharsetProvider(JDBFCharsetProvider charsetProvider) {
            this.charsetProvider = charsetProvider;
            return this;
        }

        private byte[] getBytes() {
            try {
                bytes = Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                throw new JDBFException(e);
            }
            return bytes;
        }

        public JDBFReader build() {
            return new JDBFReader(getBytes(), charsetProvider);
        }
    }

    private final List<JHeaderField> headers = new ArrayList<>();
    private final List<JRow> rows = new ArrayList<>();

    public JDBFReader(byte[] bytes, JDBFCharsetProvider charsetProvider) {
        if (bytes == null || bytes.length == 0) {
            throw new JDBFException("Byte array is not provided");
        }

        JDBFSupportedDbaseVersion dbaseVersion = JDBFContext.isDbaseVersionSupported(bytes[0]);
        if (dbaseVersion == null) {
            throw new JDBFException("Unsupported dBase file: " + bytes[0] + "... Supported only DBASE V ["
                    + JDBFSupportedDbaseVersion.DBASE_V.getVersion() + "], DBASE VII ["
                    + JDBFSupportedDbaseVersion.DBASE_VII.getVersion() + "]");
        }

        LOG.debug("Found dbase version in byte: {}", dbaseVersion);

        String charset = charsetProvider.provide(dbaseVersion);
        if (charset == null) {
            throw new JDBFException("Charset not provided for DBASE " + dbaseVersion);
        }

        // read header
        int rowLength = 0;

        for (int i = JDBFContext.getOffsetHeaderStart(dbaseVersion); i < bytes.length; i += JDBFContext
                .getOffsetHeaderLengthTotal(dbaseVersion)) {

            // end of header
            if (bytes[i] == BYTE_HEADER_END) {
                break;
            }

            JHeaderField jHeader = JHeaderField.getInstance(bytes, i, dbaseVersion);
            headers.add(jHeader);

            rowLength += jHeader.getLength();
        }

        // read rows
        int offsetRow = JDBFContext.getOffsetHeaderStart(dbaseVersion) + headers.size()
                * JDBFContext.getOffsetHeaderLengthTotal(dbaseVersion)
                + JDBFContext.getOffsetHeaderDelimiterLength(dbaseVersion);

        JRow row;
        while ((row = JRow.getInstance(bytes, offsetRow, headers, charset)) != null) {
            rows.add(row);
            offsetRow += rowLength + JDBFContext.getOffsetRowDeletedMarkLength(dbaseVersion);
        }
    }

    /**
     * Get row values of rowNum.
     *
     * @param rowNum
     * @return
     */
    private List<Object> getRowValues(int rowNum) {
        if (rowNum < 0 || rowNum >= rows.size()) {
            return null;
        }
        return rows.get(rowNum).getValues();
    }

    /**
     * Returns header size.
     *
     * @return
     */
    public int getHeaderSize() {
        return headers.size();
    }

    /**
     * Returns header field position by name, if no name found returns -1.
     *
     * @param name
     * @return
     */
    public int getHeaderPosition(String name) {
        return IntStream.range(0, headers.size())
                .filter(i -> headers.get(i).getName().equals(name))
                .findFirst()
                .orElse(-1);
    }

    /**
     * Returns header object by name.
     *
     * @param name
     * @return
     */
    public JHeaderField getHeader(String name) {
        return getHeader(getHeaderPosition(name));
    }

    /**
     * Returns header object by position.
     *
     * @param pos
     * @return
     */
    public JHeaderField getHeader(int pos) {
        if (pos < 0 || pos >= headers.size()) {
            return null;
        }
        return headers.get(pos);
    }

    /**
     * Returns count rows.
     *
     * @return
     */
    public int getRowCount() {
        return rows.size();
    }

    /**
     * Returns value by position within header and rowNum.
     *
     * @param pos
     * @param rowNum
     * @return
     */
    public Object getValue(int pos, int rowNum) {
        if (pos < 0 || pos >= headers.size()) {
            return null;
        }
        List<Object> rowValues = getRowValues(rowNum);
        return rowValues == null ? null : rowValues.get(pos);
    }

    /**
     * Returns value by header name and rowNum.
     *
     * @param name
     * @param rowNum
     * @return
     */
    public Object getValue(String name, int rowNum) {
        return getValue(getHeaderPosition(name), rowNum);
    }
}

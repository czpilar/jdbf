package net.czpilar.jdbf;

import static net.czpilar.jdbf.context.JDBFContext.BYTE_HEADER_END;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.czpilar.jdbf.context.JDBFContext;
import net.czpilar.jdbf.enums.JDBFSupportedDbaseVersion;
import net.czpilar.jdbf.exceptions.JDBFException;
import net.czpilar.jdbf.fields.JHeaderField;
import net.czpilar.jdbf.fields.JRow;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class JDBFReader {

	private static final Logger logger = Logger.getLogger(JDBFReader.class);

	private List<JHeaderField> headers = new ArrayList<JHeaderField>();
	private List<JRow> rows = new ArrayList<JRow>();

	public JDBFReader(String filename) {

		this(new File(filename));
	}

	public JDBFReader(File file) {

		try {
			byte[] bytes = FileUtils.readFileToByteArray(file);
			createJDBF(bytes);
		} catch (IOException e) {
			throw new JDBFException(e);
		}
	}

	public JDBFReader(byte[] bytes) {

		createJDBF(bytes);
	}

	private void createJDBF(byte[] bytes) {

		JDBFSupportedDbaseVersion dbaseVersion = JDBFContext.isDbaseVersionSupported(bytes[0]);

		if (dbaseVersion == null) {
			throw new JDBFException("Unsupported dBase file: " + bytes[0] + "... Supported only DBASE V ["
					+ JDBFSupportedDbaseVersion.DBASE_V.getVersion() + "], DBASE VII ["
					+ JDBFSupportedDbaseVersion.DBASE_VII.getVersion() + "]");
		}

		logger.debug("Found dbase version in byte: " + dbaseVersion);

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

		JRow row = null;
		while ((row = JRow.getInstance(bytes, offsetRow, headers)) != null) {
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

		for (int i = 0; i < headers.size(); i++) {
			if (headers.get(i).getName().equals(name)) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Returns header object by name.
	 * 
	 * @param name
	 * @return
	 */
	public JHeaderField getHeader(String name) {

		int pos = getHeaderPosition(name);

		return getHeader(pos);
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

		int pos = getHeaderPosition(name);
		return getValue(pos, rowNum);
	}
}

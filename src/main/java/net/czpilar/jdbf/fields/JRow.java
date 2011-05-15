package net.czpilar.jdbf.fields;

import static net.czpilar.jdbf.context.JDBFContext.BYTE_END_OF_FILE;
import static net.czpilar.jdbf.context.JDBFContext.BYTE_ROW_DELETED;
import static net.czpilar.jdbf.context.JDBFContext.OUTPUT_ENCODING;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.czpilar.jdbf.context.JDBFContext;
import net.czpilar.jdbf.enums.JDBFSupportedDbaseVersion;
import net.czpilar.jdbf.exceptions.JDBFException;

import org.apache.commons.lang.StringUtils;

public class JRow {

	private List<Object> values = new ArrayList<Object>();
	private boolean deleted;

	private JRow() {

	}

	/**
	 * Creates instance of row.
	 * 
	 * @param bytes
	 * @param start
	 * @param headers
	 * @return
	 */
	public static JRow getInstance(byte[] bytes, int start, List<JHeaderField> headers) {

		JDBFSupportedDbaseVersion dbaseVersion = headers.get(0).getDbaseVersion();

		JRow row = new JRow();

		int offset = start;

		byte[] val = getBytes(bytes, offset, JDBFContext.getOffsetRowDeletedMarkLength(dbaseVersion));

		// no more rows - end of file
		if (val[0] == BYTE_END_OF_FILE) {
			return null;
		}

		row.setDeleted(val[0] == BYTE_ROW_DELETED);
		offset += JDBFContext.getOffsetRowDeletedMarkLength(dbaseVersion);
		String encoding = JDBFContext.getDBFEncoding(dbaseVersion);

		for (JHeaderField jHeader : headers) {

			val = getBytes(bytes, offset, jHeader.getLength());
			Object o;
			switch (jHeader.getType()) {
				case CHARACTER:
					o = asString(val, encoding);
					break;
				case NUMERIC:
					o = asDoubleString(val);
					break;
				case FLOAT:
					o = asDoubleString(val);
					break;
				case LONG:
					o = asLong(val);
					break;
				case DOUBLE:
					o = asDouble(val);
					break;
				case DATE:
					o = asCalendar(val);
					break;
				case LOGICAL:
					o = asBoolean(val);
					break;
				default:
					throw new JDBFException("Unsupported type " + jHeader.getType());
			}

			row.addValue(o);
			offset += jHeader.getLength();
		}

		return row;
	}

	private static byte[] getBytes(byte[] bytes, int offset, int length) {

		byte[] ret = new byte[length];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = bytes[i + offset];
		}

		return ret;
	}

	private void addValue(Object val) {

		values.add(val);
	}

	public List<Object> getValues() {

		return values;
	}

	public boolean isDeleted() {

		return deleted;
	}

	public void setDeleted(boolean deleted) {

		this.deleted = deleted;
	}

	/**
	 * 4 bytes. Leftmost bit used to indicate sign, 0 negative.
	 * 
	 * @param bytes
	 * @return
	 */
	public static Long asLong(byte[] bytes) {

		if (bytes == null || bytes.length != 4) {
			return null;
		}

		long res = 0;
		for (int i = 0; i < bytes.length; i++) {
			int by = bytes[i] & (i == 0 ? 0x7f : 0xff);
			res |= by << 8 * (bytes.length - 1 - i);
		}
		boolean isNegative = (bytes[0] & 0x80) == 0;

		return Long.valueOf(isNegative ? -res : res);
	}

	/**
	 * Number stored as a string, right justified, and padded with blanks to the width of the field.
	 * 
	 * @param bytes
	 * @return
	 */
	public static Long asLongString(byte[] bytes) {

		try {
			String s = StringUtils.trimToNull(new String(bytes));
			return s == null ? null : Long.valueOf(s);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 8 bytes - date stored as a string in the format YYYYMMDD.
	 * 
	 * @param bytes
	 * @return
	 */
	public static Calendar asCalendar(byte[] bytes) {

		if (bytes == null || bytes.length != 8) {
			return null;
		}

		String s = StringUtils.trimToNull(new String(bytes));

		try {
			int year = Integer.parseInt(s.substring(0, 4));
			int month = Integer.parseInt(s.substring(4, 6));
			int day = Integer.parseInt(s.substring(6, 8));

			Calendar cal = Calendar.getInstance();
			cal.clear();
			cal.set(year, month - 1, day);

			return cal;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 8 bytes - no conversions, stored as a double.
	 * 
	 * @param bytes
	 * @return
	 */
	public static Double asDouble(byte[] bytes) {

		if (bytes == null || bytes.length != 8) {
			return null;
		}

		long res = 0;
		for (int i = 0; i < bytes.length; i++) {
			long by = bytes[i] & (i == 0 ? 0x7f : 0xff);
			res |= by << 8 * (bytes.length - 1 - i);
		}

		boolean isNegative = (bytes[0] & 0x80) == 0;

		double d = Double.longBitsToDouble(res);
		BigDecimal bd = BigDecimal.valueOf(d);
		bd = bd.setScale(8, RoundingMode.HALF_UP);

		return Double.valueOf(isNegative ? bd.negate().doubleValue() : bd.doubleValue());
	}

	/**
	 * Number stored as a string, right justified, and padded with blanks to the width of the field.
	 * 
	 * @param bytes
	 * @return
	 */
	public static Double asDoubleString(byte[] bytes) {

		try {
			String s = StringUtils.trimToNull(new String(bytes));
			return s == null ? null : Double.valueOf(s);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * All OEM code page characters - padded with blanks to the width of the field.
	 * 
	 * @param bytes
	 * @return
	 */
	public static String asString(byte[] bytes, String encoding) {

		try {
			String s = new String(bytes, encoding);
			return StringUtils.trimToNull(new String(s.getBytes(OUTPUT_ENCODING), OUTPUT_ENCODING));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 1 byte - initialized to 0x20 (space) otherwise T or F.
	 * 
	 * @param bytes
	 * @return
	 */
	public static Boolean asBoolean(byte[] bytes) {

		if (bytes == null || bytes.length != 1) {
			return null;
		}

		String s = StringUtils.trimToNull(new String(bytes));
		return "T".equals(s) ? Boolean.TRUE : "F".equals(s) ? Boolean.FALSE : null;
	}

}

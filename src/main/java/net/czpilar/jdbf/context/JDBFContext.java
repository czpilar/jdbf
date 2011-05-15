package net.czpilar.jdbf.context;

import java.util.HashMap;
import java.util.Map;

import net.czpilar.jdbf.enums.JDBFSupportedDbaseVersion;
import net.czpilar.jdbf.exceptions.JDBFException;

public abstract class JDBFContext {

	public static final String OUTPUT_ENCODING = "UTF-8";

	public static final byte BYTE_HEADER_END = 0x0D;
	public static final byte BYTE_ROW_DELETED = 0x2A;
	public static final byte BYTE_ROW_NOT_DELETED = 0x20;
	public static final byte BYTE_END_OF_FILE = 0x1A;

	private static final Map<JDBFSupportedDbaseVersion, String> DBF_ENCODING;

	private static final Map<JDBFSupportedDbaseVersion, Integer> OFFSET_HEADER_START;
	private static final Map<JDBFSupportedDbaseVersion, Integer> OFFSET_HEADER_NAME_LENGTH;
	private static final Map<JDBFSupportedDbaseVersion, Integer> OFFSET_HEADER_TYPE_LENGTH;
	private static final Map<JDBFSupportedDbaseVersion, Integer> OFFSET_HEADER_LENGTH_LENGTH;

	private static final Map<JDBFSupportedDbaseVersion, Integer> OFFSET_HEADER_DELIMITER_LENGTH;
	private static final Map<JDBFSupportedDbaseVersion, Integer> OFFSET_ROW_DELETED_MARK_LENGTH;

	static {
		DBF_ENCODING = new HashMap<JDBFSupportedDbaseVersion, String>();
		DBF_ENCODING.put(JDBFSupportedDbaseVersion.DBASE_V, "CP852");
		DBF_ENCODING.put(JDBFSupportedDbaseVersion.DBASE_VII, "CP1250");

		OFFSET_HEADER_START = new HashMap<JDBFSupportedDbaseVersion, Integer>();
		OFFSET_HEADER_START.put(JDBFSupportedDbaseVersion.DBASE_V, Integer.valueOf(32));
		OFFSET_HEADER_START.put(JDBFSupportedDbaseVersion.DBASE_VII, Integer.valueOf(68));

		OFFSET_HEADER_NAME_LENGTH = new HashMap<JDBFSupportedDbaseVersion, Integer>();
		OFFSET_HEADER_NAME_LENGTH.put(JDBFSupportedDbaseVersion.DBASE_V, Integer.valueOf(11));
		OFFSET_HEADER_NAME_LENGTH.put(JDBFSupportedDbaseVersion.DBASE_VII, Integer.valueOf(32));

		OFFSET_HEADER_TYPE_LENGTH = new HashMap<JDBFSupportedDbaseVersion, Integer>();
		OFFSET_HEADER_TYPE_LENGTH.put(JDBFSupportedDbaseVersion.DBASE_V, Integer.valueOf(5));
		OFFSET_HEADER_TYPE_LENGTH.put(JDBFSupportedDbaseVersion.DBASE_VII, Integer.valueOf(1));

		OFFSET_HEADER_LENGTH_LENGTH = new HashMap<JDBFSupportedDbaseVersion, Integer>();
		OFFSET_HEADER_LENGTH_LENGTH.put(JDBFSupportedDbaseVersion.DBASE_V, Integer.valueOf(16));
		OFFSET_HEADER_LENGTH_LENGTH.put(JDBFSupportedDbaseVersion.DBASE_VII, Integer.valueOf(15));

		OFFSET_HEADER_DELIMITER_LENGTH = new HashMap<JDBFSupportedDbaseVersion, Integer>();
		OFFSET_HEADER_DELIMITER_LENGTH.put(JDBFSupportedDbaseVersion.DBASE_V, Integer.valueOf(1));
		OFFSET_HEADER_DELIMITER_LENGTH.put(JDBFSupportedDbaseVersion.DBASE_VII, Integer.valueOf(1));

		OFFSET_ROW_DELETED_MARK_LENGTH = new HashMap<JDBFSupportedDbaseVersion, Integer>();
		OFFSET_ROW_DELETED_MARK_LENGTH.put(JDBFSupportedDbaseVersion.DBASE_V, Integer.valueOf(1));
		OFFSET_ROW_DELETED_MARK_LENGTH.put(JDBFSupportedDbaseVersion.DBASE_VII, Integer.valueOf(1));
	}

	public static final String getDBFEncoding(JDBFSupportedDbaseVersion dbaseVersion) {

		return DBF_ENCODING.get(dbaseVersion);
	}

	public static final int getOffsetHeaderStart(JDBFSupportedDbaseVersion dbaseVersion) {

		return OFFSET_HEADER_START.get(dbaseVersion).intValue();
	}

	public static final int getOffsetHeaderNameLength(JDBFSupportedDbaseVersion dbaseVersion) {

		return OFFSET_HEADER_NAME_LENGTH.get(dbaseVersion).intValue();
	}

	public static final int getOffsetHeaderTypeLength(JDBFSupportedDbaseVersion dbaseVersion) {

		return OFFSET_HEADER_TYPE_LENGTH.get(dbaseVersion).intValue();
	}

	public static final int getOffsetHeaderLengthLength(JDBFSupportedDbaseVersion dbaseVersion) {

		return OFFSET_HEADER_LENGTH_LENGTH.get(dbaseVersion);
	}

	public static final int getOffsetHeaderLengthTotal(JDBFSupportedDbaseVersion dbaseVersion) {

		return getOffsetHeaderNameLength(dbaseVersion) + getOffsetHeaderTypeLength(dbaseVersion)
				+ getOffsetHeaderLengthLength(dbaseVersion);
	}

	public static final int getOffsetHeaderDelimiterLength(JDBFSupportedDbaseVersion dbaseVersion) {

		return OFFSET_HEADER_DELIMITER_LENGTH.get(dbaseVersion);
	}

	public static final int getOffsetRowDeletedMarkLength(JDBFSupportedDbaseVersion dbaseVersion) {

		return OFFSET_ROW_DELETED_MARK_LENGTH.get(dbaseVersion);
	}

	public static final JDBFSupportedDbaseVersion isDbaseVersionSupported(byte dbaseVersion) {

		try {
			return JDBFSupportedDbaseVersion.valueOf(dbaseVersion);
		} catch (JDBFException e) {
			return null;
		}
	}
}

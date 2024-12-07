package net.czpilar.jdbf.context;

import net.czpilar.jdbf.enums.JDBFSupportedDbaseVersion;
import net.czpilar.jdbf.exceptions.JDBFException;

import java.util.Map;

public abstract class JDBFContext {

    public static final String OUTPUT_ENCODING = "UTF-8";

    public static final byte BYTE_HEADER_END = 0x0D;
    public static final byte BYTE_ROW_DELETED = 0x2A;
    public static final byte BYTE_ROW_NOT_DELETED = 0x20;
    public static final byte BYTE_END_OF_FILE = 0x1A;

    private static final Map<JDBFSupportedDbaseVersion, String> DBF_ENCODING = Map.of(
            JDBFSupportedDbaseVersion.DBASE_V, "CP852",
            JDBFSupportedDbaseVersion.DBASE_VII, "CP1250"
    );
    private static final Map<JDBFSupportedDbaseVersion, Integer> OFFSET_HEADER_START = Map.of(
            JDBFSupportedDbaseVersion.DBASE_V, 32,
            JDBFSupportedDbaseVersion.DBASE_VII, 68
    );
    private static final Map<JDBFSupportedDbaseVersion, Integer> OFFSET_HEADER_NAME_LENGTH = Map.of(
            JDBFSupportedDbaseVersion.DBASE_V, 11,
            JDBFSupportedDbaseVersion.DBASE_VII, 32
    );
    private static final Map<JDBFSupportedDbaseVersion, Integer> OFFSET_HEADER_TYPE_LENGTH = Map.of(
            JDBFSupportedDbaseVersion.DBASE_V, 5,
            JDBFSupportedDbaseVersion.DBASE_VII, 1
    );
    private static final Map<JDBFSupportedDbaseVersion, Integer> OFFSET_HEADER_LENGTH_LENGTH = Map.of(
            JDBFSupportedDbaseVersion.DBASE_V, 16,
            JDBFSupportedDbaseVersion.DBASE_VII, 15
    );
    private static final Map<JDBFSupportedDbaseVersion, Integer> OFFSET_HEADER_DELIMITER_LENGTH = Map.of(
            JDBFSupportedDbaseVersion.DBASE_V, 1,
            JDBFSupportedDbaseVersion.DBASE_VII, 1
    );
    private static final Map<JDBFSupportedDbaseVersion, Integer> OFFSET_ROW_DELETED_MARK_LENGTH = Map.of(
            JDBFSupportedDbaseVersion.DBASE_V, 1,
            JDBFSupportedDbaseVersion.DBASE_VII, 1
    );

    public static String getDBFEncoding(JDBFSupportedDbaseVersion dbaseVersion) {
        return DBF_ENCODING.get(dbaseVersion);
    }

    public static int getOffsetHeaderStart(JDBFSupportedDbaseVersion dbaseVersion) {
        return OFFSET_HEADER_START.get(dbaseVersion);
    }

    public static int getOffsetHeaderNameLength(JDBFSupportedDbaseVersion dbaseVersion) {
        return OFFSET_HEADER_NAME_LENGTH.get(dbaseVersion);
    }

    public static int getOffsetHeaderTypeLength(JDBFSupportedDbaseVersion dbaseVersion) {
        return OFFSET_HEADER_TYPE_LENGTH.get(dbaseVersion);
    }

    public static int getOffsetHeaderLengthLength(JDBFSupportedDbaseVersion dbaseVersion) {
        return OFFSET_HEADER_LENGTH_LENGTH.get(dbaseVersion);
    }

    public static int getOffsetHeaderLengthTotal(JDBFSupportedDbaseVersion dbaseVersion) {
        return getOffsetHeaderNameLength(dbaseVersion) + getOffsetHeaderTypeLength(dbaseVersion)
                + getOffsetHeaderLengthLength(dbaseVersion);
    }

    public static int getOffsetHeaderDelimiterLength(JDBFSupportedDbaseVersion dbaseVersion) {
        return OFFSET_HEADER_DELIMITER_LENGTH.get(dbaseVersion);
    }

    public static int getOffsetRowDeletedMarkLength(JDBFSupportedDbaseVersion dbaseVersion) {
        return OFFSET_ROW_DELETED_MARK_LENGTH.get(dbaseVersion);
    }

    public static JDBFSupportedDbaseVersion isDbaseVersionSupported(byte dbaseVersion) {
        try {
            return JDBFSupportedDbaseVersion.valueOf(dbaseVersion);
        } catch (JDBFException e) {
            return null;
        }
    }
}

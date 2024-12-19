package net.czpilar.jdbf.fields;

import net.czpilar.jdbf.context.JDBFContext;
import net.czpilar.jdbf.enums.JDBFSupportedDbaseVersion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static net.czpilar.jdbf.context.JDBFContext.BYTE_END_OF_FILE;
import static net.czpilar.jdbf.context.JDBFContext.BYTE_ROW_DELETED;

public class JRow {

    private final List<Object> values = new ArrayList<>();
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
    public static JRow getInstance(byte[] bytes, int start, List<JHeaderField> headers, String charset) {
        JDBFSupportedDbaseVersion dbaseVersion = headers.getFirst().getDbaseVersion();

        JRow row = new JRow();

        int offset = start;

        byte[] val = getBytes(bytes, offset, JDBFContext.getOffsetRowDeletedMarkLength(dbaseVersion));

        // no more rows - end of file
        if (val == null || val[0] == BYTE_END_OF_FILE) {
            return null;
        }

        row.setDeleted(val[0] == BYTE_ROW_DELETED);
        offset += JDBFContext.getOffsetRowDeletedMarkLength(dbaseVersion);

        for (JHeaderField jHeader : headers) {

            val = getBytes(bytes, offset, jHeader.getLength());
            Object o = switch (jHeader.getType()) {
                case CHARACTER -> asString(val, charset);
                case NUMERIC, FLOAT -> asDoubleString(val);
                case LONG -> asLong(val);
                case DOUBLE -> asDouble(val);
                case DATE -> asDate(val);
                case LOGICAL -> asBoolean(val);
            };

            row.addValue(o);
            offset += jHeader.getLength();
        }

        return row;
    }

    private static byte[] getBytes(byte[] bytes, int offset, int length) {
        if (bytes.length == offset) {
            return null;
        }

        byte[] ret = new byte[length];
        System.arraycopy(bytes, offset, ret, 0, ret.length);
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
            res |= (long) by << 8 * (bytes.length - 1 - i);
        }
        boolean isNegative = (bytes[0] & 0x80) == 0;

        return isNegative ? -res : res;
    }

    /**
     * Number stored as a string, right justified, and padded with blanks to the width of the field.
     *
     * @param bytes
     * @return
     */
    public static Long asLongString(byte[] bytes) {
        try {
            String s = trimToNull(new String(bytes));
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
    public static Date asDate(byte[] bytes) {
        if (bytes == null || bytes.length != 8) {
            return null;
        }

        String s = trimToNull(new String(bytes));

        try {
            int year = Integer.parseInt(s.substring(0, 4));
            int month = Integer.parseInt(s.substring(4, 6));
            int day = Integer.parseInt(s.substring(6, 8));

            Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.set(year, month - 1, day);

            return cal.getTime();
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

        return isNegative ? bd.negate().doubleValue() : bd.doubleValue();
    }

    /**
     * Number stored as a string, right justified, and padded with blanks to the width of the field.
     *
     * @param bytes
     * @return
     */
    public static Double asDoubleString(byte[] bytes) {
        try {
            String s = trimToNull(new String(bytes));
            return s == null ? null : Double.valueOf(s);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * All OEM code page characters - padded with blanks to the width of the field.
     *
     * @param bytes
     * @param charset
     * @return
     */
    public static String asString(byte[] bytes, String charset) {
        try {
            String s = new String(bytes, charset);
            return trimToNull(new String(s.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
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

        String s = trimToNull(new String(bytes));
        return "T".equals(s) ? Boolean.TRUE : "F".equals(s) ? Boolean.FALSE : null;
    }

    private static String trimToNull(String str) {
        return str == null || str.isEmpty() ? null : str.trim();
    }


}

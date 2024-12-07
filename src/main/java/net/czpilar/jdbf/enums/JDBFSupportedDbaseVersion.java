package net.czpilar.jdbf.enums;

import net.czpilar.jdbf.exceptions.JDBFException;

import java.text.MessageFormat;

public enum JDBFSupportedDbaseVersion {

    DBASE_V(0x03), DBASE_VII(0x04);

    private final byte version;

    JDBFSupportedDbaseVersion(int version) {
        this.version = (byte) version;
    }

    public byte getVersion() {
        return version;
    }

    public static JDBFSupportedDbaseVersion valueOf(byte val) {
        for (JDBFSupportedDbaseVersion version : JDBFSupportedDbaseVersion.values()) {
            if (version.getVersion() == val) {
                return version;
            }
        }

        throw new JDBFException(MessageFormat.format("No enum const {0} for value {1}", JDBFSupportedDbaseVersion.class.getName(), val));
    }
}

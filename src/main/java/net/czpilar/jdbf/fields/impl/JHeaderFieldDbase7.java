package net.czpilar.jdbf.fields.impl;

import net.czpilar.jdbf.enums.JDBFSupportedDbaseVersion;
import net.czpilar.jdbf.fields.JHeaderField;

public class JHeaderFieldDbase7 extends JHeaderField {

    public JHeaderFieldDbase7(byte[] bytes, int offset) {

        super(bytes, offset);
    }

    @Override
    protected JDBFSupportedDbaseVersion getDbaseVersion() {

        return JDBFSupportedDbaseVersion.DBASE_VII;
    }

}

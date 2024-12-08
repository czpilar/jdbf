package net.czpilar.jdbf.fields.impl;

import net.czpilar.jdbf.enums.JDBFSupportedDbaseVersion;
import net.czpilar.jdbf.fields.JHeaderField;

public class JHeaderFieldDbase5 extends JHeaderField {

    public JHeaderFieldDbase5(byte[] bytes, int offset) {

        super(bytes, offset);
    }

    @Override
    protected JDBFSupportedDbaseVersion getDbaseVersion() {

        return JDBFSupportedDbaseVersion.DBASE_V;
    }

}

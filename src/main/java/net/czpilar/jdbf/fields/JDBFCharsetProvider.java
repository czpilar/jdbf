package net.czpilar.jdbf.fields;

import net.czpilar.jdbf.enums.JDBFSupportedDbaseVersion;

@FunctionalInterface
public interface JDBFCharsetProvider {

    String provide(JDBFSupportedDbaseVersion version);
}

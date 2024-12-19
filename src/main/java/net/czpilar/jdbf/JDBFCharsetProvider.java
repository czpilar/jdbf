package net.czpilar.jdbf;

import net.czpilar.jdbf.enums.JDBFSupportedDbaseVersion;

@FunctionalInterface
public interface JDBFCharsetProvider {

    /**
     * Provides character set encoding for given dBASE version.
     *
     * @param version
     * @return
     */
    String provide(JDBFSupportedDbaseVersion version);
}

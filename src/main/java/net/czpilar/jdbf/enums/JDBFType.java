package net.czpilar.jdbf.enums;

import net.czpilar.jdbf.exceptions.JDBFException;

import java.text.MessageFormat;

public enum JDBFType {

    CHARACTER("C"), DATE("D"), NUMERIC("N"), LOGICAL("L"), LONG("I"), FLOAT("F"), DOUBLE("O");

    private final String type;

    JDBFType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static JDBFType valueOfType(String val) {
        for (JDBFType type : JDBFType.values()) {
            if (type.getType().equals(val)) {
                return type;
            }
        }

        throw new JDBFException(MessageFormat.format("No enum const {0} for value {1}", JDBFType.class.getName(), val));
    }
}

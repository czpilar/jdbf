package net.czpilar.jdbf.enums;

import java.text.MessageFormat;

import net.czpilar.jdbf.exceptions.JDBFException;

public enum JDBFType {

	CHARACTER("C"), DATE("D"), NUMERIC("N"), LOGICAL("L"), LONG("I"), FLOAT("F"), DOUBLE("O");

	private String type;

	private JDBFType(String type) {

		this.type = type;
	}

	public String getType() {

		return type;
	}

	public static final JDBFType valueOfType(String val) {

		for (JDBFType type : JDBFType.values()) {

			if (type.getType().equals(val)) {
				return type;
			}
		}

		throw new JDBFException(MessageFormat.format("No enum const {0} for value {1}", JDBFType.class
				.getName(), val));
	}
}

package net.czpilar.jdbf.fields;

import net.czpilar.jdbf.context.JDBFContext;
import net.czpilar.jdbf.enums.JDBFSupportedDbaseVersion;
import net.czpilar.jdbf.enums.JDBFType;
import net.czpilar.jdbf.exceptions.JDBFException;
import net.czpilar.jdbf.fields.impl.JHeaderFieldDbase5;
import net.czpilar.jdbf.fields.impl.JHeaderFieldDbase7;

public abstract class JHeaderField {

	private final String name;
	private final JDBFType type;
	private final int length;

	protected JHeaderField(byte[] bytes, int offset) {

		this.name = new String(bytes, offset, JDBFContext.getOffsetHeaderNameLength(getDbaseVersion()))
				.trim();
		String type = String.valueOf((char) bytes[offset
				+ JDBFContext.getOffsetHeaderNameLength(getDbaseVersion())]);
		this.type = JDBFType.valueOfType(type);
		this.length = bytes[offset + JDBFContext.getOffsetHeaderNameLength(getDbaseVersion())
				+ JDBFContext.getOffsetHeaderTypeLength(getDbaseVersion())];

	}

	public static JHeaderField getInstance(byte[] bytes, int offset, JDBFSupportedDbaseVersion dbaseVersion) {

		JHeaderField jHeader = null;

		switch (dbaseVersion) {
			case DBASE_V:
				jHeader = new JHeaderFieldDbase5(bytes, offset);
				break;
			case DBASE_VII:
				jHeader = new JHeaderFieldDbase7(bytes, offset);
				break;
			default:
				throw new JDBFException("Unsupported dBase: " + dbaseVersion);
		}

		return jHeader;
	}

	protected abstract JDBFSupportedDbaseVersion getDbaseVersion();

	public String getName() {

		return name;
	}

	public JDBFType getType() {

		return type;
	}

	public int getLength() {

		return length;
	}
}
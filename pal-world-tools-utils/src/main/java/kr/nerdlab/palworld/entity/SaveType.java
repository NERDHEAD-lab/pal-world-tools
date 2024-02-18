package kr.nerdlab.palworld.entity;

public enum SaveType {
	SINGLE_ZLIB(0x31), //49
	DOUBLE_ZLIB(0x32); //50

	private final byte value;

	SaveType(int value) {
		this.value = (byte) value;
	}

	public byte getValue() {
		return value;
	}

	public static SaveType fromValue(byte value) {
		for (SaveType type : SaveType.values()) {
			if (type.getValue() == value) {
				return type;
			}
		}
		return null;
	}
}

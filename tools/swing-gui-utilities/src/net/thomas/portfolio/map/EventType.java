package net.thomas.portfolio.map;

public enum EventType {
	A(100, 50, 0), B(50, 100, 0), C(50, 50, 0);

	public static final int[] BASE_COLOR = { 150, 150, 0 };
	private static final double RATIO_TO_MAX = 1.0 / 10.0;

	private final int rLimit;
	private final int gLimit;
	private final int bLimit;

	private EventType(int rLimit, int gLimit, int bLimit) {
		this.rLimit = rLimit;
		this.gLimit = gLimit;
		this.bLimit = bLimit;
	}

	public byte[] calculateColor(int count) {
		final byte[] color = new byte[] { (byte) rLimit, (byte) gLimit, (byte) bLimit };
		final int multiplyer = (int) (count * RATIO_TO_MAX);
		if (multiplyer < 1.0) {
			color[0] *= multiplyer;
			color[1] *= multiplyer;
			color[2] *= multiplyer;
		}
		return color;
	}
}

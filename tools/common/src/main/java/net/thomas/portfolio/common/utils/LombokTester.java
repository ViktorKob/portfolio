package net.thomas.portfolio.common.utils;

import lombok.Data;

public class LombokTester {
	public static void main(String[] args) {
		final TestObject left = new TestObject(1);
		final TestObject right = new TestObject(1);
		final TestObject other = new TestObject(2);
		System.out.println(left);
		System.out.println(left.equals(right));
		System.out.println(left.equals(other));
	}
}

@Data
class TestObject {
	private final int value;
}
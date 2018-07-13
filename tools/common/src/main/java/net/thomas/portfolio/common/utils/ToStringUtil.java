package net.thomas.portfolio.common.utils;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.StandardToStringStyle;

public class ToStringUtil {
	public static String asString(Object object) {
		final StandardToStringStyle style = new StandardToStringStyle();
		style.setFieldSeparator(", ");
		style.setUseClassName(false);
		style.setUseIdentityHashCode(false);
		return ReflectionToStringBuilder.toString(object, style);
	};
}

package net.thomas.portfolio.hbase_index.schema.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.thomas.portfolio.shared_objects.hbase_index.schema.util.SimpleRepresentationParser;

/***
 * Indicated that this field should be included during key generation
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface SimpleRepresentable {
	Class<? extends SimpleRepresentationParser> parser();

	String field() default "";
}
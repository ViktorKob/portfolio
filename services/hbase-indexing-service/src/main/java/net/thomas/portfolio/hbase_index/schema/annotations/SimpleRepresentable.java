package net.thomas.portfolio.hbase_index.schema.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.thomas.portfolio.hbase_index.schema.simple_rep.parsers.SimpleRepresentationParserImpl;

/***
 * Indicated that this field should be included during key generation
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface SimpleRepresentable {
	Class<? extends SimpleRepresentationParserImpl> parser();

	String field() default "";
}
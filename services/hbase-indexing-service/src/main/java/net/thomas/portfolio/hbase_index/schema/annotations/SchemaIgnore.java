package net.thomas.portfolio.hbase_index.schema.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/***
 * Indicated that this field should be included during key generation
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface SchemaIgnore {
}
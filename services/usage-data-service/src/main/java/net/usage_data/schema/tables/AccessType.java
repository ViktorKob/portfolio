/*
 * This file is generated by jOOQ.
*/
package net.usage_data.schema.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import net.usage_data.schema.Indexes;
import net.usage_data.schema.Keys;
import net.usage_data.schema.UsageData;
import net.usage_data.schema.tables.records.AccessTypeRecord;

import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.UInteger;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.7"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AccessType extends TableImpl<AccessTypeRecord> {

    private static final long serialVersionUID = -1168461993;

    /**
     * The reference instance of <code>usage_data.access_type</code>
     */
    public static final AccessType ACCESS_TYPE = new AccessType();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AccessTypeRecord> getRecordType() {
        return AccessTypeRecord.class;
    }

    /**
     * The column <code>usage_data.access_type.id</code>.
     */
    public final TableField<AccessTypeRecord, UInteger> ID = createField("id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false).identity(true), this, "");

    /**
     * The column <code>usage_data.access_type.name</code>.
     */
    public final TableField<AccessTypeRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR(45).nullable(false), this, "");

    /**
     * Create a <code>usage_data.access_type</code> table reference
     */
    public AccessType() {
        this(DSL.name("access_type"), null);
    }

    /**
     * Create an aliased <code>usage_data.access_type</code> table reference
     */
    public AccessType(String alias) {
        this(DSL.name(alias), ACCESS_TYPE);
    }

    /**
     * Create an aliased <code>usage_data.access_type</code> table reference
     */
    public AccessType(Name alias) {
        this(alias, ACCESS_TYPE);
    }

    private AccessType(Name alias, Table<AccessTypeRecord> aliased) {
        this(alias, aliased, null);
    }

    private AccessType(Name alias, Table<AccessTypeRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return UsageData.USAGE_DATA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.ACCESS_TYPE_ACCESS_TYPE_NAME, Indexes.ACCESS_TYPE_NAME_UNIQUE, Indexes.ACCESS_TYPE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<AccessTypeRecord, UInteger> getIdentity() {
        return Keys.IDENTITY_ACCESS_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<AccessTypeRecord> getPrimaryKey() {
        return Keys.KEY_ACCESS_TYPE_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<AccessTypeRecord>> getKeys() {
        return Arrays.<UniqueKey<AccessTypeRecord>>asList(Keys.KEY_ACCESS_TYPE_PRIMARY, Keys.KEY_ACCESS_TYPE_NAME_UNIQUE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessType as(String alias) {
        return new AccessType(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessType as(Name alias) {
        return new AccessType(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public AccessType rename(String name) {
        return new AccessType(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public AccessType rename(Name name) {
        return new AccessType(name, null);
    }
}

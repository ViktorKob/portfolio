/*
 * This file is generated by jOOQ.
*/
package net.usage_data.schema.tables.records;


import java.sql.Timestamp;

import javax.annotation.Generated;

import net.usage_data.schema.tables.UserAccessedDocument;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;
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
public class UserAccessedDocumentRecord extends UpdatableRecordImpl<UserAccessedDocumentRecord> implements Record6<UInteger, String, String, UInteger, UInteger, Timestamp> {

    private static final long serialVersionUID = -660599979;

    /**
     * Setter for <code>usage_data.user_accessed_document.row</code>.
     */
    public void setRow(UInteger value) {
        set(0, value);
    }

    /**
     * Getter for <code>usage_data.user_accessed_document.row</code>.
     */
    public UInteger getRow() {
        return (UInteger) get(0);
    }

    /**
     * Setter for <code>usage_data.user_accessed_document.document_type</code>.
     */
    public void setDocumentType(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>usage_data.user_accessed_document.document_type</code>.
     */
    public String getDocumentType() {
        return (String) get(1);
    }

    /**
     * Setter for <code>usage_data.user_accessed_document.document_uid</code>.
     */
    public void setDocumentUid(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>usage_data.user_accessed_document.document_uid</code>.
     */
    public String getDocumentUid() {
        return (String) get(2);
    }

    /**
     * Setter for <code>usage_data.user_accessed_document.user_id</code>.
     */
    public void setUserId(UInteger value) {
        set(3, value);
    }

    /**
     * Getter for <code>usage_data.user_accessed_document.user_id</code>.
     */
    public UInteger getUserId() {
        return (UInteger) get(3);
    }

    /**
     * Setter for <code>usage_data.user_accessed_document.access_type_id</code>.
     */
    public void setAccessTypeId(UInteger value) {
        set(4, value);
    }

    /**
     * Getter for <code>usage_data.user_accessed_document.access_type_id</code>.
     */
    public UInteger getAccessTypeId() {
        return (UInteger) get(4);
    }

    /**
     * Setter for <code>usage_data.user_accessed_document.time_of_access</code>.
     */
    public void setTimeOfAccess(Timestamp value) {
        set(5, value);
    }

    /**
     * Getter for <code>usage_data.user_accessed_document.time_of_access</code>.
     */
    public Timestamp getTimeOfAccess() {
        return (Timestamp) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<UInteger> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<UInteger, String, String, UInteger, UInteger, Timestamp> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<UInteger, String, String, UInteger, UInteger, Timestamp> valuesRow() {
        return (Row6) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field1() {
        return UserAccessedDocument.USER_ACCESSED_DOCUMENT.ROW;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return UserAccessedDocument.USER_ACCESSED_DOCUMENT.DOCUMENT_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return UserAccessedDocument.USER_ACCESSED_DOCUMENT.DOCUMENT_UID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field4() {
        return UserAccessedDocument.USER_ACCESSED_DOCUMENT.USER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field5() {
        return UserAccessedDocument.USER_ACCESSED_DOCUMENT.ACCESS_TYPE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field6() {
        return UserAccessedDocument.USER_ACCESSED_DOCUMENT.TIME_OF_ACCESS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger component1() {
        return getRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getDocumentType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getDocumentUid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger component4() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger component5() {
        return getAccessTypeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component6() {
        return getTimeOfAccess();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value1() {
        return getRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getDocumentType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getDocumentUid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value4() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value5() {
        return getAccessTypeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value6() {
        return getTimeOfAccess();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAccessedDocumentRecord value1(UInteger value) {
        setRow(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAccessedDocumentRecord value2(String value) {
        setDocumentType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAccessedDocumentRecord value3(String value) {
        setDocumentUid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAccessedDocumentRecord value4(UInteger value) {
        setUserId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAccessedDocumentRecord value5(UInteger value) {
        setAccessTypeId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAccessedDocumentRecord value6(Timestamp value) {
        setTimeOfAccess(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAccessedDocumentRecord values(UInteger value1, String value2, String value3, UInteger value4, UInteger value5, Timestamp value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached UserAccessedDocumentRecord
     */
    public UserAccessedDocumentRecord() {
        super(UserAccessedDocument.USER_ACCESSED_DOCUMENT);
    }

    /**
     * Create a detached, initialised UserAccessedDocumentRecord
     */
    public UserAccessedDocumentRecord(UInteger row, String documentType, String documentUid, UInteger userId, UInteger accessTypeId, Timestamp timeOfAccess) {
        super(UserAccessedDocument.USER_ACCESSED_DOCUMENT);

        set(0, row);
        set(1, documentType);
        set(2, documentUid);
        set(3, userId);
        set(4, accessTypeId);
        set(5, timeOfAccess);
    }
}

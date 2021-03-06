/*
 * This file is generated by jOOQ.
 */
package net.thomas.portfolio.usage_data.schema;


import javax.annotation.Generated;

import net.thomas.portfolio.usage_data.schema.tables.AccessType;
import net.thomas.portfolio.usage_data.schema.tables.User;
import net.thomas.portfolio.usage_data.schema.tables.UserAccessedDocument;
import net.thomas.portfolio.usage_data.schema.tables.records.AccessTypeRecord;
import net.thomas.portfolio.usage_data.schema.tables.records.UserAccessedDocumentRecord;
import net.thomas.portfolio.usage_data.schema.tables.records.UserRecord;

import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code></code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<AccessTypeRecord, Integer> IDENTITY_ACCESS_TYPE = Identities0.IDENTITY_ACCESS_TYPE;
    public static final Identity<UserRecord, Integer> IDENTITY_USER = Identities0.IDENTITY_USER;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<AccessTypeRecord> PK_ACCESS_TYPE = UniqueKeys0.PK_ACCESS_TYPE;
    public static final UniqueKey<AccessTypeRecord> SQLITE_AUTOINDEX_ACCESS_TYPE_1 = UniqueKeys0.SQLITE_AUTOINDEX_ACCESS_TYPE_1;
    public static final UniqueKey<UserRecord> PK_USER = UniqueKeys0.PK_USER;
    public static final UniqueKey<UserRecord> SQLITE_AUTOINDEX_USER_1 = UniqueKeys0.SQLITE_AUTOINDEX_USER_1;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<UserAccessedDocumentRecord, UserRecord> FK_USER_ACCESSED_DOCUMENT_USER_1 = ForeignKeys0.FK_USER_ACCESSED_DOCUMENT_USER_1;
    public static final ForeignKey<UserAccessedDocumentRecord, AccessTypeRecord> FK_USER_ACCESSED_DOCUMENT_ACCESS_TYPE_1 = ForeignKeys0.FK_USER_ACCESSED_DOCUMENT_ACCESS_TYPE_1;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 {
        public static Identity<AccessTypeRecord, Integer> IDENTITY_ACCESS_TYPE = Internal.createIdentity(AccessType.ACCESS_TYPE, AccessType.ACCESS_TYPE.ID);
        public static Identity<UserRecord, Integer> IDENTITY_USER = Internal.createIdentity(User.USER, User.USER.ID);
    }

    private static class UniqueKeys0 {
        public static final UniqueKey<AccessTypeRecord> PK_ACCESS_TYPE = Internal.createUniqueKey(AccessType.ACCESS_TYPE, "pk_access_type", AccessType.ACCESS_TYPE.ID);
        public static final UniqueKey<AccessTypeRecord> SQLITE_AUTOINDEX_ACCESS_TYPE_1 = Internal.createUniqueKey(AccessType.ACCESS_TYPE, "sqlite_autoindex_access_type_1", AccessType.ACCESS_TYPE.NAME);
        public static final UniqueKey<UserRecord> PK_USER = Internal.createUniqueKey(User.USER, "pk_user", User.USER.ID);
        public static final UniqueKey<UserRecord> SQLITE_AUTOINDEX_USER_1 = Internal.createUniqueKey(User.USER, "sqlite_autoindex_user_1", User.USER.NAME);
    }

    private static class ForeignKeys0 {
        public static final ForeignKey<UserAccessedDocumentRecord, UserRecord> FK_USER_ACCESSED_DOCUMENT_USER_1 = Internal.createForeignKey(net.thomas.portfolio.usage_data.schema.Keys.PK_USER, UserAccessedDocument.USER_ACCESSED_DOCUMENT, "fk_user_accessed_document_user_1", UserAccessedDocument.USER_ACCESSED_DOCUMENT.USER_ID);
        public static final ForeignKey<UserAccessedDocumentRecord, AccessTypeRecord> FK_USER_ACCESSED_DOCUMENT_ACCESS_TYPE_1 = Internal.createForeignKey(net.thomas.portfolio.usage_data.schema.Keys.PK_ACCESS_TYPE, UserAccessedDocument.USER_ACCESSED_DOCUMENT, "fk_user_accessed_document_access_type_1", UserAccessedDocument.USER_ACCESSED_DOCUMENT.ACCESS_TYPE_ID);
    }
}

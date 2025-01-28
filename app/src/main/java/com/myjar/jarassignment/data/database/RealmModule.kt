package com.myjar.jarassignment.data.database

import android.content.Context
import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmMigration
import io.realm.RealmSchema

private const val SCHEMA_VERSION = 1L

object RealmModule {
    fun provideRealmInstance(applicationContext: Context): Realm {
        Realm.init(applicationContext)
        val realmConfiguration = RealmConfiguration.Builder()
            .name("jarassignment.realm")
            .allowWritesOnUiThread(true)
            .schemaVersion(SCHEMA_VERSION)
            .migration(Migration())
            .build()
        return Realm.getInstance(realmConfiguration)
    }
}

class Migration : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema: RealmSchema = realm.schema
        var currentVersion = oldVersion

        if (currentVersion == 0L) {
            schema.create("MainPhoneListResponse")
                .addField("itemId", Long::class.java, FieldAttribute.PRIMARY_KEY)
                .addField("computerItemData", String::class.java)
            currentVersion++
        }
    }

    override fun hashCode(): Int {
        return Migration::class.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is Migration
    }
}
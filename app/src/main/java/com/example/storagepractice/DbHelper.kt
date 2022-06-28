package com.example.storagepractice

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

object FeedEntry : BaseColumns {
    const val TABLE_NAME = "usernamepasswordtable"
    const val COLUMN_NAME_USERNAME = "username"
    const val COLUMN_NAME_PASSWORD = "password"
}


private const val SQL_CREATE_ENTRIES =
    "CREATE TABLE ${FeedEntry.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${FeedEntry.COLUMN_NAME_USERNAME} TEXT," +
            "${FeedEntry.COLUMN_NAME_PASSWORD} TEXT)"

private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${FeedEntry.TABLE_NAME}"

private const val DB_NAME = "Storage Database"

private const val DB_VERSION = 1


class DbHelper(context: Context): SQLiteOpenHelper(context, DB_NAME, null , DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
}
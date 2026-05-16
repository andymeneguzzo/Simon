package com.andres.simon_project

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MatchDBHelper(context: Context) : SQLiteOpenHelper(
    context, DB_NAME, null, DB_VERSION
) {

    companion object {
        private const val DB_NAME = "simon.db"
        private const val DB_VERSION = 1
        private const val TABLE = "matches"
        private const val COLUMN_ID = "id"
        private const val COLUMN_MAX_CORRECT_LENGTH = "max_correct_length"
        private const val COLUMN_ERROR_SEQUENCE = "error_sequence"
        private const val COLUMN_ERROR_INDEX = "error_index"
        private const val COLUMN_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase?) {

    }
}
package com.andres.simon_project

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
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
        // create the database table: id, max_correct_length, error_sequence, error_index, created_at
        val createTableQuery = """
            CREATE TABLE $TABLE (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_MAX_CORRECT_LENGTH INTEGER NOT NULL,
                $COLUMN_ERROR_SEQUENCE TEXT NOT NULL,
                $COLUMN_ERROR_INDEX INTEGER NOT NULL,
                $COLUMN_CREATED_AT INTEGER NOT NULL
            )
        """.trimIndent()

        // execute the query defined above
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        // when updating, drop the table if it exists and recreate it
        db?.execSQL("DROP TABLE IF EXISTS $TABLE")
        onCreate(db)
    }

    /* CRUD OPERATIONS required -> save match (insert), then get matches and get matches by ID */
    // insert match object in db
    fun insertMatch(match: GameSession.Match) : Long {
        val valuesToInsert = ContentValues().apply {
            put(COLUMN_MAX_CORRECT_LENGTH, match.maxCorrectLength)
            put(COLUMN_ERROR_SEQUENCE, joinSequence(match.errorSequence)) // could not put it as a raw sequence, but as a joined sequence
            put(COLUMN_ERROR_INDEX, match.errorIndex)
            put(COLUMN_CREATED_AT, match.createdAt)
        }

        return writableDatabase.insert(TABLE, null, valuesToInsert)
    }
    private fun joinSequence(sequence: List<String>) : String {
        return sequence.joinToString(",")
    }
    private fun separateSequence(joinedSequence: String) : List<String> {
        if (joinedSequence.isBlank()) return emptyList()

        return joinedSequence.split(",")
    }

    // retrieve all matches
    fun getAllMatches() : List<GameSession.Match> {
        val matches = mutableListOf<GameSession.Match>() // list of read matches to return

        // query from a readableDB, use a cursor to basically go through every entry of the database, in this case in descending order
        val cursor = readableDatabase.query(
            TABLE,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_CREATED_AT DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                // add the match object returned from the method, so the match entry read from the cursor
                matches.add(readFromCursor(it))
            }
        }

        return matches
    }
    private fun readFromCursor(cursor: Cursor) : GameSession.Match {
        // get all the data from the cursor
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val maxCorrectLength = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MAX_CORRECT_LENGTH))
        val errorSequenceJoined = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ERROR_SEQUENCE))
        val errorIndex = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ERROR_INDEX))
        val createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))

        return GameSession.Match(
            id = id,
            maxCorrectLength = maxCorrectLength,
            errorSequence = separateSequence(errorSequenceJoined),
            errorIndex = errorIndex,
            createdAt = createdAt
        )
    }

    // retrieve match by its ID
    fun getMatchByID(matchID: Long) : GameSession.Match? {
        val cursor = readableDatabase.query(
            TABLE,
            null,
            "$COLUMN_ID = ?",
            arrayOf(matchID.toString()),
            null,
            null,
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                // match with ID found, so return it
                return readFromCursor(it)
            }
        }

        // found nothing, return null
        return null
    }
}
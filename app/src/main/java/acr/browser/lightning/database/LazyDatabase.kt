package acr.browser.lightning.database

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * A class that caches a [SQLiteDatabase] object for the consumer, reopening it whenever it is
 * provided if it has been closed between the last time it was accessed.
 *
 * Created by anthonycr on 9/16/17.
 */
class LazyDatabase(private val sqLiteOpenHelper: SQLiteOpenHelper) {

    private var sqLiteDatabase: SQLiteDatabase? = null

    /**
     * Returns the current database object or opens a new one if the current one has been closed.
     */
    fun db(): SQLiteDatabase {
        val currentDb = sqLiteDatabase

        return if (currentDb?.isOpen != true) {
            val newDb = sqLiteOpenHelper.writableDatabase
            sqLiteDatabase = newDb
            newDb
        } else {
            currentDb
        }
    }

}
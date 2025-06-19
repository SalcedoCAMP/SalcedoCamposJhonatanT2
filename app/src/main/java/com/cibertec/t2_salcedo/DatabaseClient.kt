package com.cibertec.t2_salcedo

import android.content.Context

object DatabaseClient {
    private var database: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            database ?: AppDatabase.getDatabase(context).also { database = it }
        }
    }
}
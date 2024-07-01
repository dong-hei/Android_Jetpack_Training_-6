package com.dk.roomandcoroutineflow.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TextEntity::class], version = 1)
abstract class TextDb : RoomDatabase() {

    abstract fun textDao() : TextDao

    companion object{

        @Volatile
        private var INSTANCE : TextDb? = null
        fun getDb(context : Context) : TextDb{
            return INSTANCE ?: synchronized(this){
                val instance=  Room.databaseBuilder(
                    context.applicationContext,
                    TextDb::class.java,
                    "text_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
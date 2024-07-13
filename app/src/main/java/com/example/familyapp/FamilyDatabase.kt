package com.example.familyapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ContactModel::class], version = 1, exportSchema = false)
public abstract class FamilyDatabase : RoomDatabase() {

    abstract fun ContactDao():ContactDao


    companion object{
        @Volatile
        private var INSTANCE:FamilyDatabase?= null
        fun getDatabase(context: Context):FamilyDatabase {

            INSTANCE?.let {
                return it
            }

            return synchronized(FamilyDatabase::class.java) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FamilyDatabase::class.java,
                    "my_family_db"
                ).build()

                INSTANCE = instance

                instance
            }
        }

    }

}
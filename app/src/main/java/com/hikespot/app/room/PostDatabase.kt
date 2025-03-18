package com.hikespot.app.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hikespot.app.model.Post
import com.hikespot.app.utils.Converters

@Database(entities = [Post::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PostDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao

    companion object {
        @Volatile
        private var INSTANCE: PostDatabase? = null

        fun getDatabase(context: Context): PostDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PostDatabase::class.java,
                    "hike_spot_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

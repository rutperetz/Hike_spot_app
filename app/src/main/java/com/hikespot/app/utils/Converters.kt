package com.hikespot.app.utils

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromList(list: MutableList<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toList(data: String): MutableList<String> {
        return if (data.isEmpty()) mutableListOf() else data.split(",").toMutableList()
    }
}

package com.mr.suraksha

import android.content.Context
import android.content.SharedPreferences

class User(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("userinfo", Context.MODE_PRIVATE)

    var mobile: String
        get() = sharedPreferences.getString("mobile", "")?: ""
        set(value) = sharedPreferences.edit().putString("mobile", value).apply()

    var contact1: String
        get() = sharedPreferences.getString("contact1", "") ?: ""
        set(value) = sharedPreferences.edit().putString("contact1", value).apply()

    var contact2: String
        get() = sharedPreferences.getString("contact2", "") ?: ""
        set(value) = sharedPreferences.edit().putString("contact2", value).apply()

    var name1: String
        get() = sharedPreferences.getString("name1", "") ?: ""
        set(value) = sharedPreferences.edit().putString("name1", value).apply()

    var name2: String
        get() = sharedPreferences.getString("name2", "") ?: ""
        set(value) = sharedPreferences.edit().putString("name2", value).apply()

    fun removeUser() {
        sharedPreferences.edit().clear().apply()
    }
}


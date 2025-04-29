package com.el_aouthmanie.flowinventory.module

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object SessionManager {

    private const val PREF_NAME = "loginSession"
    private const val PREF_LOGIN = "login"
    private const val PREF_PASS = "pass"
    private const val PREF_LOGGED = "loggedIn"
    private const val PREF_ID = "id"

    fun saveLogin(context: Context, login: String, pass: String) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.edit(commit = true) {
            apply {
                putString(PREF_LOGIN, login)
                putString(PREF_PASS, pass)
                putBoolean(PREF_LOGGED, true)
            }
        }
    }

    fun saveId(context: Context, id: String) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.edit(commit = true) {
            apply {
                putString(PREF_ID, id)

            }
        }
    }
    fun getId(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(PREF_ID, null)
    }

    fun isLogIn(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(PREF_LOGGED, false)
    }

    fun removeLogSession(context: Context) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.edit(commit = true) { clear() }
    }

    // Optional: get login/pass if needed
    fun getLogin(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(PREF_LOGIN, null)
    }

    fun getPassword(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(PREF_PASS, null)
    }
}

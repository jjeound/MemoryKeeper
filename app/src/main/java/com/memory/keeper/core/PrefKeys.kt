package com.memory.keeper.core

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PrefKeys {
    val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    val USER_NAME = stringPreferencesKey("user_name")
    val USER_ID = longPreferencesKey("user_id")
    val ROLE = stringPreferencesKey("role")
    val HAS_SIGNED_UP = booleanPreferencesKey("has_signed_up")
}
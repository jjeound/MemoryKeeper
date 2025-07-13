package com.memory.keeper.core

import androidx.datastore.preferences.core.stringPreferencesKey

object PrefKeys {
    val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    val USER_NAME = stringPreferencesKey("user_name")
    val ROLE = stringPreferencesKey("role")
}
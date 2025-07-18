package com.memory.keeper

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application(){
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this,  BuildConfig.KAKAO_APP_KEY)
    }
}
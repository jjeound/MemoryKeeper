package com.memory.keeper.feature.home

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class FullScreenVideoActivity : ComponentActivity() {
    private lateinit var exoPlayer: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = intent.getStringExtra("VIDEO_URI")?.toUri()
        exoPlayer = ExoPlayer.Builder(this).build().apply {
            uri?.let {
                setMediaItem(MediaItem.fromUri(it))
            }
            prepare()
            playWhenReady = true
        }

        val playerView = PlayerView(this).apply {
            player = exoPlayer
            useController = true
            systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }

        setContentView(playerView)
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }
}
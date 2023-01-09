package com.portfolioprojects.exomediaexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView

class MainActivity : AppCompatActivity(), Player.Listener {

    private lateinit var player: ExoPlayer
    private lateinit var playerView: StyledPlayerView
    private lateinit var progressBar: ProgressBar
    private lateinit var titleTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar = findViewById(R.id.progressBar)
        titleTv = findViewById(R.id.title)

        setupPlayer()
        addMp3Files()
        addMp4Files()
        //Youtube Tutorial code
        if(savedInstanceState != null){
            savedInstanceState.getInt("mediaItem").let {restoredMedia ->
                val seekTime = savedInstanceState.getLong("SeekTime")
                player.seekTo(restoredMedia, seekTime)
                player.play()
            }
        }


    }




    //Youtube Tutorial code
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putLong("SeekTime", player.currentPosition)
        outState.putInt("mediaItem", player.currentMediaItemIndex)
    }

    private fun setupPlayer(){
        player = ExoPlayer.Builder(this).build()
        playerView = findViewById(R.id.video_view)
        playerView.player = player
        player.addListener(this)
    }

    private fun addMp4Files(){
        val mediaItem = MediaItem.fromUri(getString(R.string.media_url_mp4))
        player.addMediaItem(mediaItem)
        player.prepare()
    }

    private fun addMp3Files(){
        val mediaItem = MediaItem.fromUri(getString(R.string.test_mp3))
        player.addMediaItem(mediaItem)
        player.prepare()
    }

    override fun onPlaybackStateChanged(playbackState: Int){
        super.onPlaybackStateChanged(playbackState)

        when(playbackState){
            Player.STATE_BUFFERING -> {
                progressBar.visibility = View.VISIBLE
            }
            Player.STATE_READY -> {
                progressBar.visibility = View.INVISIBLE
            }
        }
    }

    override fun onStop(){
        super.onStop()
        player.release()
    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        super.onMediaMetadataChanged(mediaMetadata)

        titleTv.text = mediaMetadata.title ?: mediaMetadata.displayTitle ?: "title not found"
    }
}
package com.portfolioprojects.exomediaexample

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.KeyEvent.*
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView

class MainActivity : AppCompatActivity(), Player.Listener {

    private lateinit var player: ExoPlayer
    private lateinit var playerView: StyledPlayerView
    private lateinit var songListMenu: ConstraintLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var titleTv: TextView
    private lateinit var stringsProj: Array<String>
    private var audioCursor: Cursor? = null
    private lateinit var songImgBtn: ImageButton
    private lateinit var playPauseBtn: ImageButton
    private lateinit var prevBtn: ImageButton
    private lateinit var nextBtn: ImageButton

    //Variables for the Song List RecyclerView
    private lateinit var songRV: RecyclerView
    private lateinit var songAL: ArrayList<String>
    private lateinit var songRVAdapter: SongAdapter
    private val songsMap = HashMap<String,Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Request Permissions to Read Music from External Storage
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            }
        }

        setupPlayer()


        songListMenu = findViewById(R.id.songListMenu)
        songRV = findViewById(R.id.songListView)
        songRV.setHasFixedSize(true)
        songRV.layoutManager = LinearLayoutManager(this)

        songAL = ArrayList()

        //Reads Song names into Song list
        stringsProj = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA
        )
        audioCursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            stringsProj,
            null,
            null,
            null
        )
        if (audioCursor != null) {
            if (audioCursor!!.moveToFirst()) {
                do {
                    val audioTitleIndex: Int =
                        audioCursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val audioUriIndex: Int =
                        audioCursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                    songAL.add(audioCursor!!.getString(audioTitleIndex))                    //Add Song to Array List
                    addMediaFiles(audioCursor!!.getString(audioUriIndex))                   //Add URI to Media player playlist
                    songsMap.put(audioCursor!!.getString(audioTitleIndex),audioCursor!!.position)
                } while (audioCursor!!.moveToNext())
            }
        }
        audioCursor!!.close()

        songAL.sort()

        songRVAdapter = SongAdapter(songAL)
        songRV.adapter = songRVAdapter


        //Click event for song items
        songRVAdapter.setOnItemClickListener(object: SongAdapter.OnItemClickListener{
            override fun onItemClick(position: Int, songName:String) {
                val temp: Int? = songsMap.get(songName)
                Toast.makeText(this@MainActivity,"You Clicked on $temp", Toast.LENGTH_SHORT).show()
                //Toast.makeText(this@MainActivity,"You Clicked on $songName no. $temp", Toast.LENGTH_SHORT).show()

                if (temp != null) {
                    playSong(temp)
                }
            }
        })

        songImgBtn = findViewById(R.id.songImgBtn)
        songImgBtn.setOnClickListener{
            val isVisible: Int = playerView.getVisibility()
            if(isVisible == View.GONE){
                songListMenu.setVisibility(View.GONE)
                playerView.setVisibility(View.VISIBLE)
            }
        }


        //Youtube Tutorial code Begin Main function
        progressBar = findViewById(R.id.progressBar)
        titleTv = findViewById(R.id.title)

        //setupPlayer()
        //addMp3Files()
        //addMp4Files()

        savedInstanceState?.getInt("mediaItem")?.let { restoredMedia ->
            val seekTime = savedInstanceState.getLong("SeekTime")
            player.seekTo(restoredMedia, seekTime)
            player.play()
        }
    }
        //End of Main function

    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build()
        playerView = findViewById(R.id.video_view)
        playerView.player = player
        player.addListener(this)
        playPauseBtn = findViewById(R.id.playPauseBtn)
        playPauseBtn.setOnClickListener {
            if(player.isPlaying){
                player.pause()
                playPauseBtn.setImageResource(R.drawable.ic_play)
            }else{
                player.play()
                playPauseBtn.setImageResource(R.drawable.ic_pause)
            }
        }

        prevBtn = findViewById(R.id.prevBtn)
        nextBtn = findViewById(R.id.nextBtn)


    }

    private fun addMediaFiles(songUriString: String) {
        val mediaItem = MediaItem.fromUri(songUriString)
        player.addMediaItem(mediaItem)
        //player.prepare()
    }

    fun playSong(songIndex:Int){
        //val path: String = songAL.get(position).getPath()
        player.seekTo(songIndex,0)
        player.prepare()
        player.play()
    }

//Overridden Functions
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
            return if(keyCode == KeyEvent.KEYCODE_BACK){
                val isVisible: Int = playerView.getVisibility()
                if(isVisible == View.VISIBLE){
                    //FIX:Minimize to phone home page
                    songListMenu.setVisibility(View.VISIBLE)
                    playerView.setVisibility(View.GONE)
                }
                true
            }else super.onKeyUp(keyCode, event)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putLong("SeekTime", player.currentPosition)
        outState.putInt("mediaItem", player.currentMediaItemIndex)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)

        when (playbackState) {
            Player.STATE_BUFFERING -> {
                progressBar.visibility = View.VISIBLE
            }
            Player.STATE_READY -> {
                progressBar.visibility = View.INVISIBLE
            }
        }
    }

    override fun onStop() {
        super.onStop()
        player.release()
    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        super.onMediaMetadataChanged(mediaMetadata)

        titleTv.text = mediaMetadata.title ?: mediaMetadata.displayTitle ?: "title not found"
    }

}




    //Youtube Tutorial code Begin


    /*private fun addMp4Files(){
        val mediaItem = MediaItem.fromUri(getString(R.string.media_url_mp4))
        player.addMediaItem(mediaItem)
        player.prepare()
    }

    private fun addMp3Files(){
        val mediaItem = MediaItem.fromUri(getString(R.string.test_mp3))
        player.addMediaItem(mediaItem)
        player.prepare()
    }*/


    //Youtube Tutorial code End


/*var isAvailable: Boolean = false;
    var isWritable: Boolean = false;
    var isReadable: Boolean = false;
    var state: String = Environment.getExternalStorageState();

    if(Environment.MEDIA_MOUNTED.equals(state)){
        isAvailable = true
        isWritable = true
        isReadable = true
        println(Environment.MEDIA_MOUNTED.equals(state))
        println("first")
    }else if(Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
        isAvailable = true
        isWritable = false
        isReadable = true
        println(Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
        println("second")
    }else{
        isAvailable = false
        isWritable = false
        isReadable = false
    }
    println(Environment.getExternalStorageDirectory().getPath())
    println(Environment.getStorageDirectory().getPath())*/
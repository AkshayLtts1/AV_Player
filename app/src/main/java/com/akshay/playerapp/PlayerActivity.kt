package com.akshay.playerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Toast
import com.akshay.playerapp.databinding.ActivityPlayerBinding
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout


class PlayerActivity : AppCompatActivity() {
    private val audioList: ArrayList<Audio> = ArrayList()
    private val videoList: ArrayList<Video> = ArrayList()
    lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeLayout()
    }

    private fun initializeLayout(){

        when(intent.getStringExtra("class")){
            "AllVideos" -> {
                videoList.addAll(MainActivity.videoList)
                createVideoPlayerLib()
            }

            "AllAudios" -> {
                audioList.addAll(MainActivity.audioList)
                createAudioPlayerLib()
            }

            "FolderActivity" -> {
                AllPlayerList = ArrayList()
                AllPlayerList.addAll(MainActivity.folderList)
            }
        }
        //Add Custom Control(play, pause, forward, backward)
        setUpCustomControls()
    }

    private fun createAudioPlayerLib(){
        binding.videoTitle.text = audioList[position].title
        binding.videoTitle.isSelected = true
        libVLC = LibVLC(this)
        mediaPlayer = MediaPlayer(libVLC)
        vlcVideoLayout = findViewById(R.id.libPlayerView)
        val mediaPath = audioList[position].path
        val media = Media(libVLC, mediaPath)
        mediaPlayer.media = media
        mediaPlayer.play()
        mediaPlayer.attachViews(vlcVideoLayout, null, false, false)
//        seekBarProgress()
    }
    private fun createVideoPlayerLib(){
        binding.videoTitle.text = videoList[position].title
        binding.videoTitle.isSelected = true
        libVLC = LibVLC(this)
        mediaPlayer = MediaPlayer(libVLC)
        vlcVideoLayout = findViewById(R.id.libPlayerView)
        val mediaPath = videoList[position].path
        val media = Media(libVLC, mediaPath)
        mediaPlayer.media = media
        mediaPlayer.attachViews(vlcVideoLayout, null, false, false)
        mediaPlayer.play()
    }

    private fun setUpCustomControls(){
        val playButton: ImageButton = findViewById(R.id.playPauseButton)
//        val pauseButton: ImageButton = findViewById(R.id.pauseButton)
        val nextButton: ImageButton = findViewById(R.id.nextButton)
        val previousButton: ImageButton = findViewById(R.id.previousButton)
        val fastForwardButton: ImageButton = findViewById(R.id.fastForwardButton)
        val fastRewindButton: ImageButton = findViewById(R.id.fastRewindButton)

        var buttonVisible = true
        var touchStartTime = 0L

        //set up touch listener
        vlcVideoLayout.setOnTouchListener { v, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN ->{
                    touchStartTime = System.currentTimeMillis()
                }
                MotionEvent.ACTION_UP ->{
                    val touchDuration = System.currentTimeMillis() - touchStartTime

                    if (touchDuration<3000){
                        buttonVisible = !buttonVisible

                            //Update Visibility of Buttons
                        playButton.visibility = if (buttonVisible) View.VISIBLE else View.INVISIBLE
                        nextButton.visibility = if (buttonVisible) View.VISIBLE else View.INVISIBLE
                        previousButton.visibility = if (buttonVisible) View.VISIBLE else View.INVISIBLE
                        fastForwardButton.visibility = if (buttonVisible) View.VISIBLE else View.INVISIBLE
                        fastRewindButton.visibility = if (buttonVisible) View.VISIBLE else View.INVISIBLE
                    }
                }
            }
            true
        }

        playButton.setOnClickListener{
            if (!mediaPlayer.isPlaying){
                mediaPlayer.play()
                playButton.setImageResource(R.drawable.pause)
                Handler().postDelayed({
                    playButton.visibility = View.INVISIBLE
                    nextButton.visibility = View.INVISIBLE
                    previousButton.visibility = View.INVISIBLE
                    fastForwardButton.visibility = View.INVISIBLE
                    fastRewindButton.visibility = View.INVISIBLE
                }, 3000)
            }
            else{
                mediaPlayer.pause()
                playButton.setImageResource(R.drawable.play_icon)
            }
        }
        val currentMedia = getCurrentMedia()
        nextButton.setOnClickListener {
            if (position >= audioList.size + videoList.size){
                position = 0
            }
            currentMedia.let {
                if (it is Audio){
                    mediaPlayer.stop()
                    playNextAudio()
                }
                else if (it is Video){
                    mediaPlayer.stop()
                    playNextVideo()
                }
            }
        }
        previousButton.setOnClickListener {
            if (position > 0){
                position -= 1
            }
            currentMedia.let {
                if (it is Audio){
                    mediaPlayer.stop()
                    playPreviousAudio()
                }
                else if (it is Video){
                    mediaPlayer.stop()
                    playPreviousVideo()
                }
            }
        }

        // move forwardButton
        fastForwardButton.setOnClickListener {
            mediaPlayer.time += 10000
        }

        // move backwardButton
        fastRewindButton.setOnClickListener {
            mediaPlayer.time -= 10000
        }
    }
    private fun getCurrentMedia(): Any? {
        return if (position < audioList.size) {
            audioList[position]
        } else if (position < audioList.size + videoList.size) {
            videoList[position - audioList.size]
        } else {
            null
        }
    }
    private fun playNextAudio() {
        if (position < audioList.size - 1) {
            position++
            createAudioPlayerLib()
        } else {
            // All audio have been played
            Toast.makeText(this, "Msg for next audio button for change the song",Toast.LENGTH_SHORT).show()
        }
    }

    private fun playPreviousAudio() {
        if (position >= 0) {
            createAudioPlayerLib()
        }
        else {
            // All audio have been played
            Toast.makeText(this, "MSg for previous audio button for change the song",Toast.LENGTH_SHORT).show()
        }
    }

    private fun playNextVideo(){
        if (position < videoList.size - 1) {
            position++
            createVideoPlayerLib()
        } else {
            // All audio have been played
            Toast.makeText(this, "MSg for forward video button for change the song",Toast.LENGTH_SHORT).show()
        }
    }

    private fun playPreviousVideo() {
        if (position >= 0) {
            createVideoPlayerLib()
        }
        else {
            // All audio have been played
            Toast.makeText(this, "MSg for previous audio button for change the song",Toast.LENGTH_SHORT).show()
        }
    }

    private fun seekBarProgress(){
        seekBar = findViewById(R.id.seekBar)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    mediaPlayer.position = progress.toFloat()
                    mediaPlayer.play()
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                seekBar?.max!!
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.progress!!
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        libVLC.release()
        mediaPlayer.stop()
    }

    companion object{
        lateinit var seekBar: SeekBar
        lateinit var playPauseButton: ImageButton
        lateinit var AllPlayerList: ArrayList<Folder>
        lateinit var libVLC: LibVLC
        lateinit var vlcVideoLayout: VLCVideoLayout
        lateinit var mediaPlayer: MediaPlayer
        var position: Int = 0
    }
}
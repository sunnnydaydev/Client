package com.sunnyday.client


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sunnyday.binderpool.IAudioManager
import com.sunnyday.binderpool.IMusicManager

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Thread { test() }.start()

    }

    private fun test() {
        Log.i("MainActivity","client:test")
        // test get MusicBinder
        val musicBinder = BinderPool.getInstance(this).getBinder(BinderPool.BINDER_MUSIC_MANAGER)
        val mIMusicManager = IMusicManager.Stub.asInterface(musicBinder)
        mIMusicManager.playMusic()

        // test get AudioBinder
        val audioBinder = BinderPool.getInstance(this).getBinder(BinderPool.BINDER_AUDIO_MANAGER)
        val mAudioManager = IAudioManager.Stub.asInterface(audioBinder)
        mAudioManager.playAudio()
    }
}
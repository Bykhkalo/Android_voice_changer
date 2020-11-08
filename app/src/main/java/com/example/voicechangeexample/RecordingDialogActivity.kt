package com.example.voicechangeexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView

class RecordingDialogActivity : AppCompatActivity(), Animation.AnimationListener {

    private lateinit var recordImage: ImageView
    private lateinit var stopRecordButton: Button

    private lateinit var animRotate: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording_dialog)

        init()
    }

    private fun init() {
        bindViewsWithId()
        initAnimation()

        stopRecordButton.setOnClickListener{
            MainActivity.isRecording = false
            this.finish()
        }
    }

    private fun initAnimation() {
        animRotate = AnimationUtils.loadAnimation(applicationContext, R.anim.zoom_out)
        animRotate.setAnimationListener(this)
        recordImage.visibility = View.VISIBLE
        recordImage.startAnimation(this.animRotate)
    }

    private fun bindViewsWithId() {
        recordImage = findViewById(R.id.record_image_view)
        stopRecordButton = findViewById(R.id.btn_stop_record)

    }

    override fun onAnimationStart(p0: Animation?) {

    }

    override fun onAnimationEnd(p0: Animation?) {
        recordImage.startAnimation(this.animRotate)
    }

    override fun onAnimationRepeat(p0: Animation?) {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        MainActivity.isRecording = false
        finish()
    }
}
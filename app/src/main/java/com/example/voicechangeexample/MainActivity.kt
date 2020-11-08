package com.example.voicechangeexample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioFormat.ENCODING_PCM_16BIT
import android.media.AudioRecord
import android.media.AudioTrack
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.*

class MainActivity : AppCompatActivity() {

    private lateinit var file: File


    companion object{
       var isRecording: Boolean = false
    }

    private lateinit var spFrequency: Spinner

    private lateinit var microphoneImage: ImageView
    private lateinit var recordImage: ImageView
    private lateinit var playImage: ImageView

    private lateinit var adapter: ArrayAdapter<String>

    private val effects: List<String> = listOf(
        "Ghost on cocaine",
        "Slow Motion",
        "Robot on cocaine",
        "Normal (without cocaine)",
        "Chipmunk",
        "Funny",
        "Speedy bee",
        "Fast"
    )

    private var audioTrack: AudioTrack? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initPermissions()
        init()
    }

    private fun initPermissions() {
        if (ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.RECORD_AUDIO) !=
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                    Manifest.permission.RECORD_AUDIO)) {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.RECORD_AUDIO), 1)
            } else {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.RECORD_AUDIO), 1)
            }
        }
    }

    private fun init() {
        file = File(applicationContext.getExternalFilesDir(null), "test.pom")
        bindViewsWithId()
        setOnClickListeners()
        initFrequencySpinner()
    }


    private fun bindViewsWithId() {
        microphoneImage = findViewById(R.id.microphoneImage)
        recordImage = findViewById(R.id.record_image_view)
        playImage = findViewById(R.id.play_image_view)

        spFrequency = findViewById(R.id.frequency_spinner)

    }

    private fun setOnClickListeners() {
        recordImage.setOnClickListener{
            val intent = Intent(this, RecordingDialogActivity::class.java)
            startActivity(intent)

            Thread {
                startRecording()
            }.start()
        }

        playImage.setOnClickListener{
            if (file.exists()) playRecord()
        }
    }

    private fun initFrequencySpinner() {
        adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, effects)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spFrequency.adapter = adapter
        microphoneImage.visibility = View.VISIBLE
    }

    private fun playRecord() {

        var i = 0

        val effect = (spFrequency.selectedItem as String)

        val shortSizeInBytes = Short.SIZE_BYTES
        val bufferSizeInBytes = (file.length() / shortSizeInBytes).toInt()

        val audioData = ShortArray(bufferSizeInBytes)

        val inputStream = FileInputStream(file)
        val bufferedInputStream = BufferedInputStream(inputStream)
        val dataInputStream = DataInputStream(bufferedInputStream)

        var j = 0
        while (dataInputStream.available() > 0){
            audioData[j] = dataInputStream.readShort()
            j++
        }

        dataInputStream.close()

        when (effect){
            effects[0] -> i = 5000
            effects[1] -> i = 6050
            effects[2] -> i = 8500
            effects[3] -> i = 11025
            effects[4] -> i = 16000
            effects[5] -> i = 22050
            effects[6] -> i = 41000
            effects[7] -> i = 30000
        }

        val audioAttrs = AudioAttributes.Builder()
            .setLegacyStreamType(3)
            .build()

        val audioFormat = AudioFormat.Builder()
            .setSampleRate(i)
            .setEncoding(ENCODING_PCM_16BIT)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO) // channel config
            .build()

        // audioTrack = AudioTrack(3, i, 2, 2, bufferSizeInBytes, 1)

        audioTrack = AudioTrack(audioAttrs, audioFormat, bufferSizeInBytes, 1, 1)
        audioTrack!!.play()
        audioTrack!!.write(audioData, 0 , bufferSizeInBytes)

    }

    private fun startRecording() {
        isRecording = true

        val myFile = File(applicationContext.getExternalFilesDir(null)!!.absolutePath, "test.pom")

        myFile.createNewFile()

        val outputStream = FileOutputStream(myFile)
        val bufferedOutputStream = BufferedOutputStream(outputStream)
        val dataOutputStream = DataOutputStream(bufferedOutputStream)
        val minBufferSize = AudioRecord.getMinBufferSize(11025, 2, 2)

        val audioData = ShortArray(minBufferSize)

        val audioRecord = AudioRecord(1, 11025, 2, 2, minBufferSize)
        audioRecord.startRecording()

        while (isRecording){
            val numberOfShort = audioRecord.read(audioData, 0, minBufferSize)

            for (i in 0 until numberOfShort) {
                dataOutputStream.writeShort(audioData[i].toInt())
            }
        }

        if (!isRecording){
            audioRecord.stop()
            dataOutputStream.close()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRecording = false
        audioTrack?.release()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.RECORD_AUDIO) ==
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
}
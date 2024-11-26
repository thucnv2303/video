package com.example.qrcoderecord

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import com.example.qrcoderecorde.R

class MainActivity : AppCompatActivity() {

    private var videoCapture: VideoCapture<Recorder>? = null
    private lateinit var outputDirectory: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Yêu cầu quyền truy cập
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 101
        )

        // Tạo thư mục lưu trữ video
        outputDirectory = getOutputDirectory()

        // Khởi động camera
        startCamera()

        // Nút quét QR Code
        findViewById<Button>(R.id.scanButton).setOnClickListener {
            startQRCodeScan()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()

            videoCapture = VideoCapture.withOutput(recorder)

            val preview = androidx.camera.core.Preview.Builder().build().apply {
                setSurfaceProvider(findViewById<PreviewView>(R.id.previewView).surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Gắn camera với vòng đời activity
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, videoCapture
                )
            } catch (exc: Exception) {
                Log.e("CameraX", "Lỗi khi gắn camera", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun startQRCodeScan() {
        val integrator = IntentIntegrator(this)
        integrator.setOrientationLocked(false)
        integrator.setCameraId(1) // Camera trước
        integrator.setBeepEnabled(true)
        integrator.initiateScan()
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
            val qrData = result.contents
            val fileName = "$qrData.mp4"
            stopRecordingAndSave(fileName)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun stopRecordingAndSave(fileName: String) {
        videoCapture?.let { capture ->
            val videoFile = File(outputDirectory, fileName)
            val outputOptions = FileOutputOptions.Builder(videoFile).build()
            val recording = capture.output
                .prepareRecording(this, outputOptions)
                .apply {
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.RECORD_AUDIO
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    ) {
                        withAudioEnabled()
                    }
                }
                .start(ContextCompat.getMainExecutor(this)) { event ->
                    if (event is VideoRecordEvent.Finalize) {
                        if (!event.hasError()) {
                            Log.d("CameraX", "Video lưu tại: ${videoFile.absolutePath}")
                        } else {
                            Log.e("CameraX", "Lỗi khi lưu video: ${event.error}")
                        }
                        // Tiếp tục quay video mới sau khi dừng
                        startCamera()
                    }
                }
        }
    }
}

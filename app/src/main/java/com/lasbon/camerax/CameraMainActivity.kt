package com.lasbon.camerax

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class CameraMainActivity : AppCompatActivity() {
    // Camera activity request codes
    private val CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100
    private val CAMERA_CAPTURE_IMAGE_REQUEST_CODE_DATABASE = 131
    val MEDIA_TYPE_IMAGE = 1
    private var fileUri: Uri? = null
    private var fileUriaci: Uri? = null
    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_main)
        val fab: FloatingActionButton = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener(View.OnClickListener { view ->
            Snackbar.make(view, "Opening Camera . . .", Snackbar.LENGTH_LONG)
                .setAction("Open", View.OnClickListener { captureImage() }).show()
        })
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                m.invoke(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val button = findViewById<Button>(R.id.upload)
        button.setOnClickListener { addCriminalImage() }
        requestPermission()
    }


    private fun requestPermission() {
        val galleryPermissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (!EasyPermissions.hasPermissions(this, *galleryPermissions)) {
            EasyPermissions.requestPermissions(
                this, "Access for storage",
                101, *galleryPermissions
            )
        }
    }





    //adding criminals  image to database
    private fun addCriminalImage() {
        val intentaci = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        fileUriaci = getOutputMediaFileUri2(MEDIA_TYPE_IMAGE)
        intentaci.putExtra(MediaStore.EXTRA_OUTPUT, fileUriaci)
        startActivityForResult(intentaci, CAMERA_CAPTURE_IMAGE_REQUEST_CODE_DATABASE)
    }

    fun getOutputMediaFileUri2(type: Int): Uri? {
        return Uri.fromFile(getOutputMediaFile2(type))
    }

    private fun getOutputMediaFile2(type: Int): File? {
        val mediaStorageDir = File(
            Environment.getExternalStorageDirectory()
                .toString() + "/" + Constants.databaseFolder + "/"
        )
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(
                    TAG, "Oops! Failed to create "
                            + " directory"
                )
                return null
            }
        }

        // Create a media file name
        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())
        val mediaFile: File
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = File(
                mediaStorageDir.path + File.separator
                        + "IMG" + timeStamp + ".jpg"
            )
        } else {
            return null
        }
        return mediaFile
    }

    //uploading criminals or object image to server
    private fun captureImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
    }

    fun getOutputMediaFileUri(type: Int): Uri? {
        return Uri.fromFile(getOutputMediaFile(type))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                launchUploadActivity()
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(
                    applicationContext,
                    "User cancelled image capture", Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Sorry! Failed to capture image", Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
        //if result is capturing i.e adding criminals image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE_DATABASE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(
                    applicationContext,
                    "Images Sucessfully Added into Database !!", Toast.LENGTH_SHORT
                )
                    .show()
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(
                    applicationContext,
                    "User cancelled image capture", Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Sorry! Failed to capture image", Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun launchUploadActivity() {
        val i = Intent(this, UploadActivity::class.java)
        i.putExtra("filePath", fileUri!!.path)
        startActivity(i)
    }


    private fun getOutputMediaFile(type: Int): File? {
        val mediaStorageDir = File(
            Environment.getExternalStorageDirectory()
                .toString() + "/" + Constants.comparedFolder + "/"
        )

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(
                    TAG, ("Oops! Failed to create "
                            + " directory")
                )
                return null
            }
        }

        // Create a media file name
        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())
        val mediaFile: File
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = File(
                (mediaStorageDir.path + File.separator
                        + "IMG" + timeStamp + ".jpg")
            )
        } else {
            return null
        }
        return mediaFile
    }
}
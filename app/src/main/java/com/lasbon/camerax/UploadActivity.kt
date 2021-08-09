package com.lasbon.camerax

import android.net.wifi.WifiConfiguration.AuthAlgorithm.strings
import android.os.AsyncTask
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


import com.amazonaws.services.rekognition.model.CompareFacesMatch
import com.amazonaws.services.rekognition.model.Label

import com.google.gson.Gson
import java.lang.StringBuilder
import java.util.*

class UploadActivity : AppCompatActivity() {

    public val TAG = UploadActivity::class.java.simpleName
    public var imgPreview: ImageView? = null
    public var resultTextView: TextView? = null
    public var resultTextView2: TextView? = null
    public var imgResult: ImageView? = null
    public val refImagePath: String? = null
    public var loading: LinearLayout? = null
    var textToSpeech: TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)
        imgPreview = findViewById(R.id.imgPreview) as ImageView
        imgResult = findViewById(R.id.imgResult) as ImageView
        resultTextView = findViewById(R.id.chargesText) as TextView
        resultTextView2 = findViewById(R.id.chargesText2) as TextView
        loading = findViewById(R.id.loading)
        // init aws
        AwsUtil().init(applicationContext)

        // Receiving the data from previous activity
        val i = intent

        // image or video path that is captured in previous activity
        val filePath = i.getStringExtra("filePath")
        if (filePath != null) {
            // Displaying the image or video on the screen
            imgPreview!!.visibility = View.VISIBLE
            loading?.setVisibility(View.VISIBLE)
            Util().setImage(filePath, imgPreview!!)
            CompareFaces().execute(filePath)
            detectLabels().execute(filePath)
        } else {
            Toast.makeText(
                applicationContext,
                "Sorry, file path is missing!", Toast.LENGTH_LONG
            ).show()
        }
    }

    class detectLabels :
        AsyncTask<String?, Void?, List<Label>>() {

        override fun onPostExecute(result: List<Label>) {
//            setTextViewLabels(result, resultTextView);
        }

        override fun doInBackground(vararg params: String?): List<Label> {
            return AwsUtil().getLabels(strings[0])
        }
    }


    class CompareFaces :
        AsyncTask<String?, Void?, List<CompareFacesMatch>?>() {
        var textToSpeech: TextToSpeech? = null

        public var imgPreview: ImageView? = null
        public var resultTextView: TextView? = null
        public var resultTextView2: TextView? = null
        public var imgResult: ImageView? = null
        public var refImagePath: String? = null
        public var loading: LinearLayout? = null

        override fun onPostExecute(result2: List<CompareFacesMatch>?) {

            setTextViewFacesCompare(result2, resultTextView2)
            if (result2 != null && !result2.isEmpty()) {
                imgResult?.let { Util().setImage(refImagePath, it) }
//                loading.setVisibility(View.VISIBLE)
            }
        }

        fun setTextViewFacesCompare(
            faceDetails: List<CompareFacesMatch>?,
            resultTextView: TextView?
        ): String? {
            val sb = StringBuilder()
            if (faceDetails == null) {
//                loading.setVisibility(View.GONE)
                sb.append("Faces Not Found")
            } else {
                Log.w("face_compare", Gson().toJson(faceDetails))
                if (!faceDetails.isEmpty()) {
                    sb.append("Face Detected")
//                    getTextToSpeech()
                } else {
                    sb.append("Face NOT detected ")
                }
            }
            if (resultTextView != null) {
                resultTextView.text = sb.toString()
            }
            return null
        }

        override fun doInBackground(vararg params: String?): List<CompareFacesMatch>? {
            val files: List<String> = Util().getFiles(Constants.databaseFolder)
            for (file in files) {
                val result: List<CompareFacesMatch> = AwsUtil().compareFace(
                    file,
                    strings[0]
                )!!
                if (result != null && !result.isEmpty()) {
                    refImagePath = file
                    return result
                }
            }
            return null
        }

//        fun getTextToSpeech(): TextToSpeech {
//            // create an object textToSpeech and adding features into it
//            textToSpeech = TextToSpeech(
//                getApplicationContext()
//            ) { i -> // if No error is found then only it will run
//                if (i != TextToSpeech.ERROR) {
//                    // To Choose language of speech
//                    textToSpeech!!.language = Locale.UK
//                }
//                textToSpeech!!.speak("Image detected ", TextToSpeech.QUEUE_FLUSH, null)
//
//            }
//            return textToSpeech!!
//        }
    }


}
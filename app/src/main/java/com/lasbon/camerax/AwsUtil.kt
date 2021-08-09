package com.lasbon.camerax

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.rekognition.AmazonRekognition
import com.amazonaws.services.rekognition.AmazonRekognitionClient
import com.amazonaws.services.rekognition.model.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.nio.ByteBuffer

 class AwsUtil {
    var aWSRekognition: AmazonRekognition? = null
    private val region = Regions.US_EAST_1
    private  val poolId =
        "us-east-1:e80e9bcf-1f61-4f60-9e13-43e80b07d072" //ur respective pool id /  token will go here

    fun init(context: Context?) {
        if (aWSRekognition == null) {
            val credentialsProvider = CognitoCachingCredentialsProvider(
                context,
                poolId,
                region
            )
            aWSRekognition = AmazonRekognitionClient(credentialsProvider)
        }
    }

    fun getImageFromPath(path: String?): Image {
        val options = BitmapFactory.Options()
        options.inSampleSize = 8
        var bitmap = BitmapFactory.decodeFile(path, options)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val imageBytes = ByteBuffer.wrap(stream.toByteArray())
        return Image().withBytes(imageBytes)
    }

    fun getDetectLabelRequest(path: String?): DetectLabelsRequest {
        val image = getImageFromPath(path)
        val request = DetectLabelsRequest()
        request.image = image
        return request
    }

    fun getLabels(path: String?): List<Label> {
        val request = getDetectLabelRequest(path)
        val response = aWSRekognition!!.detectLabels(request)
        return response.labels
    }

    fun getDetectFacesRequest(path: String?): DetectFacesRequest {
        return DetectFacesRequest().withImage(getImageFromPath(path))
    }

    fun compareFace(face1: String?, face2: String?): List<CompareFacesMatch>? {
        return try {
            val image_ref =
                getImageFromPath(face1)
            val image_to_check =
                getImageFromPath(face2)
            aWSRekognition!!.compareFaces(
                CompareFacesRequest(
                    image_ref,
                    image_to_check
                )
            ).faceMatches
        } catch (e: Exception) {
            null
        }
    }


}
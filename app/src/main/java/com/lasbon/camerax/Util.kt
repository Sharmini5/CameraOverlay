package com.lasbon.camerax

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.widget.ImageView
import java.io.File
import java.util.ArrayList


class Util {
    fun setImage(path: String?, imageView: ImageView) {
        val options = BitmapFactory.Options()
        options.inSampleSize = 8
        val bitmap = BitmapFactory.decodeFile(path, options)
        imageView.setImageBitmap(bitmap)
    }

    //image list from criminal gallery
    fun getFiles(dirName: String?): List<String> {
        val f: MutableList<String> = ArrayList() // list of file paths
        val file = File(Environment.getExternalStorageDirectory(), dirName)
        if (file.isDirectory) {
            val listFile = file.listFiles()
            for (i in listFile.indices) {
                f.add(listFile[i].absolutePath)
            }
        }
        return f
    }
}

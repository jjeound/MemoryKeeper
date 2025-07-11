package com.memory.keeper.feature.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream

object ImageUtil {

    fun Context.getRealPathFromURI(uri: Uri): String? {
        var filePath: String? = null
        contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                filePath = cursor.getString(columnIndex)
            }
        }
        return filePath
    }

    fun Context.copyUriToFile(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val file = File(cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            outputStream.close()
            inputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun compressImage(
        context: Context,
        originalFile: File,
        quality: Int = 70
    ): File {
        val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath)
        val compressedFile = File.createTempFile("compressed_", ".jpg", context.cacheDir)
        FileOutputStream(compressedFile).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)
        }
        return compressedFile
    }
}
package com.example.storagepractice

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.storagepractice.databinding.ActivityMediaStoreBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MediaStoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediaStoreBinding

    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.cameraButton.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(intent)
        }

        binding.uploadButton.setOnClickListener {
            //storeBitmapToExternalFilesDir(bitmap)
            //storeBitmapToExternalPublicDir(bitmap)
            storeUsingMediaStore(bitmap)
        }

        val uris = queryMediaStore()

        Log.d(TAG, "onCreate: No of images: ${uris.size}")

        for(uri in uris)
            Log.d(TAG, "onCreate: file path: ${uri.path}")

        binding.profileImageView.setImageURI(uris[0])

        //deleteFileFromSamePackage(uris[1])
        //deleteFileFromDifferentPackage(uris[0])
    }

    private fun deleteFileFromSamePackage(uri: Uri) {
        contentResolver.delete(uri, null, null)
    }

    // unable to delete other file, if need to delete then handle recoverable security exception
    private fun deleteFileFromDifferentPackage(uri: Uri) {
        contentResolver.delete(uri, null, null)
        //android.app.RecoverableSecurityException: com.example.storagepractice has no access to content://media/external/images/media/33
    }

    private fun queryMediaStore(): MutableList<Uri> {
        // Need the READ_EXTERNAL_STORAGE permission if accessing video files that your
// app didn't create.


        val uriList = mutableListOf<Uri>()

        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }


        val projection = arrayOf(
            MediaStore.Images.Media._ID
        )

        val query = contentResolver.query(
            collection,
            projection,
            null,
            null,
            null
        )
        query?.use { cursor ->
            // Cache column indices.
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)


            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                val id = cursor.getLong(idColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                uriList.add(contentUri)
                // Stores column values and the contentUri in a local object
                // that represents the media file.

            }
        }

        return uriList
    }


    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == RESULT_OK){

            val imageBitmap = result.data?.extras?.get("data") as Bitmap?

            if(imageBitmap != null) {
                binding.cameraImageView.setImageBitmap(imageBitmap)
                bitmap = imageBitmap
                binding.uploadButton.isEnabled = true
            }
            else
                Toast.makeText(this, "Bit map not found", Toast.LENGTH_SHORT).show()


        }

    }

//On Android 10 (API level 29) and higher, the proper directory for sharing photos is the MediaStore.Images table.
    private fun storeUsingMediaStore(bitmap: Bitmap): Uri {

    val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL_PRIMARY
        )
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    val contentValue = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME , "camimgmediastore2.png")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        // by default it is stored in external/pictures
        //put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        // image stored in dcim is shown in gallery app
        // whereas image stored in pictures is not shown in gallery
    }

    var uri: Uri? = null

     try{
        uri = contentResolver.insert(
            collection,
            contentValue
        ) ?: throw IOException("Failed to create media store record")

        contentResolver.openOutputStream(uri)?.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, it)
        } ?: throw IOException("Failed to open output stream")

    }
    catch (e: Exception){
        Log.d(TAG, "storeUsingMediaStore: ${e.message}")
    }

    return uri!!
    }


    private fun storeBitmapToExternalFilesDir(bitmap: Bitmap) {
        val fileName = "campriimg1.png"
        val picturesDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        // Make sure the directory "Android/data/com.example.storagePractice/files/Pictures" exists
        if (!picturesDirectory.exists()) {
            picturesDirectory.mkdirs()
        }

        try {
            val ops = FileOutputStream(File(picturesDirectory, fileName))
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, ops)

            ops.flush()
            ops.close()
            Toast.makeText(this, "Image uploaded successfully in app specific storage", Toast.LENGTH_SHORT).show()
        } catch(e: Exception) {
            // handle the error
        }
    }

    private fun storeBitmapToExternalPublicDir(bitmap: Bitmap) {
        val fileName = "campubimg1.png"
        val picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)!!

        try {
            val ops = FileOutputStream(File(picturesDirectory, fileName))
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, ops)

            ops.flush()
            ops.close()
            Toast.makeText(this, "Image uploaded successfully in shared storage", Toast.LENGTH_SHORT).show()
        } catch(e: Exception) {
            // handle the error
        }
    }


}
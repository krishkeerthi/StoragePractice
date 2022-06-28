package com.example.storagepractice

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageManager.ACTION_MANAGE_STORAGE
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.example.storagepractice.databinding.ActivityIestorageBinding
import java.io.File
import java.util.*

// File(filepath, filename)

//permissions are only defined for accessing external storage.
// That means that every app, by default, has permissions to access its internal storage.

//    1.When you are accessing app-specific files on external storage
//    you don’t need to request any permission (on Android 4.4 and higher)
//    2.With scoped storage introduced in Android 10, you no longer need to
//    request permission when working with media files that are created by
//     your app
//     3.don’t need any permissions if you’re trying to obtain any documents
//     or other types of content when using Storage Access Framework.
//     because a user is involved in the process of selecting the actual content to work with.
class ActivityIEStorage : AppCompatActivity() {
    val NUM_BYTES_NEEDED_FOR_MY_APP = 1024 * 1024 * 5000L

    private lateinit var binding: ActivityIestorageBinding

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityIestorageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "onCreate: ${filesDir}")
        Log.d(TAG, "onCreate: ${cacheDir}")

        binding.createFileButton.setOnClickListener {
            createFile("SampleFileIndex0") // creates file
            // only when content is provided for file
        }

        binding.createCacheFileButton.setOnClickListener {
            createTempFileInCache("SampleCacheFile5")
            // Creates file without content
            //File.createTempFile("SampleCacheFile2", null, cacheDir)
        }

        binding.nextButton.setOnClickListener {
            startActivity(Intent(this, MediaStoreActivity::class.java))
        }

        binding.sharedFileButton.setOnClickListener {
            val file = getPublicDocumentStorageDir("SharedFile.txt")
            Log.d(TAG, "onCreate: file Path: $file")
            if (file.exists()) {
                Log.d(TAG, "onCreate: file exists")
            } else {
                Log.d(TAG, "onCreate: file does not exist")

                writeFile(file, "First shared file to other apps")
            }

        }

        // check available space
        checkAvailableSpace()

        // list of file in internal app specific storage
        Log.d(TAG, "list of files: ${fileList()}")

        // create new sub directory
        getDir("sub directory", Context.MODE_PRIVATE)

        // delete file
        File(filesDir, "SampleImage").delete()

        deleteFile("SampleFile1")  // file under internal files dir -- success
        deleteFile("SampleCacheFile26024872273533928123.tmp") // file under internal cache dir
        // file under cache not deleted
        deleteFile("SampleCacheFile754745500754961458suffix") // not deleted

        Log.d(
            TAG,
            "onCreate: external file directory: ${getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}"
        )
        Log.d(TAG, "onCreate: external cache directory: $externalCacheDir")
        Log.d(TAG, "onCreate: external cache directories index  0: ${externalCacheDirs[0]}")
        // index 0 is primary external storage storage/emulated/0/Android/
        Log.d(TAG, "onCreate: external cache directories index 1: ${externalCacheDirs[1]}")
        // index 1 is secondary external storage storage/151D-2711/Android/

        val granted = checkExternalStoragePermission()
        Log.d(TAG, "onCreate: External storage permission: $granted")

    }

    private fun writeFile(file: File, message: String) {
//        val opStream = FileOutputStream(file)
//        opStream.write(message.toByteArray())
//        opStream.close()

        //                       or

        file.writeText(message)

        Toast.makeText(this, "Shared file created", Toast.LENGTH_SHORT).show()
    }

    private fun checkExternalStoragePermission(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    // Checks if a volume containing external storage is available to at least read.
    private fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }


    private fun createFile(fileName: String) {
        //File(filesDir, fileName)
        //var file = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        var file = getExternalFilesDirs(Environment.DIRECTORY_DOCUMENTS)[1]
        // deleted when uninstalled
        // though app specific data, other apps with proper permission can access   
        ///storage/emulated/0/Android/data/com.example.storagepractice/files/Documents

        // Important: data saved here also saved in
        // storage/self/primary/Documents

        //Returns the absolute path to the directory on the primary shared/external
        // storage device where the application can place persistent files it owns.
        // These files are internal to the applications, and not typically visible
        // to the user as media.

        // Note: There is no security enforced with these files. For example, any
        // application holding android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        // can write to these files.

        file = File(file, fileName)
        writeFile(
            file, "External storage but within the" +
                    "app package , app specific data"
        )
        //saveData("Sample text", fileName)
    }

    private fun createTempFileInCache(fileName: String) {
//        File.createTempFile(fileName, "suffix", cacheDir)
//        File.createTempFile("prefix","suffix")

        // when directory not mentioned default directory is cache dir
        // creates temporary file name
        // prefix + random number + suffix
        // mostly used in cache directory

        var file = externalCacheDir // by clearing cache, files stored in this directory is deleted
        file = File(file, "SampleCacheFile.txt")
        writeFile(file, "Cache text file created inside external cache directory")
    }

    private fun getPublicDocumentStorageDir(albumName: String): File {
        // Get the directory for the user's public  directory.
        return File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS
            ), albumName

            ///storage/emulated/0/Documents

            // Important: data saved here also saved in
            // storage/self/primary/Documents
        )
    }

    private fun getPublicAlbumStorageDir(albumName: String?): File {
        // Get the directory for the user's public pictures directory.
        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), albumName
        )

//        if (!file.mkdirs()) {  // creates a directory under media/0/pictures/sampleImage(given album name)
//            Log.d(TAG, "Directory not created")
//        }

        return file
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkAvailableSpace() {
        val storageManager = applicationContext.getSystemService<StorageManager>()!!
        val appSpecificInternalDirUuid: UUID = storageManager.getUuidForPath(filesDir)
        val availableBytes: Long =
            storageManager.getAllocatableBytes(appSpecificInternalDirUuid)
        Log.d(
            TAG,
            "checkAvailableSpace: requested bytes: ${NUM_BYTES_NEEDED_FOR_MY_APP / (1024 * 1024)} MB"
        )
        Log.d(TAG, "checkAvailableSpace: Available bytes: ${availableBytes / (1024 * 1024)} MB")



        if (availableBytes >= NUM_BYTES_NEEDED_FOR_MY_APP) {
//            storageManager.allocateBytes(
//                appSpecificInternalDirUuid, NUM_BYTES_NEEDED_FOR_MY_APP)
            Log.d(TAG, "checkAvailableSpace: space available")
        } else {
            Log.d(TAG, "checkAvailableSpace: space unavailable")
            val storageIntent = Intent().apply {
                // To request that the user remove all app cache files instead, set
                // "action" to ACTION_CLEAR_APP_CACHE.
                action = ACTION_MANAGE_STORAGE // it works
                //action = ACTION_CLEAR_APP_CACHE // not going to cache page
            }
            startActivity(storageIntent)
        }

    }

    // path, absolute path, canonical path

    //C:\temp\file.txt - This is a path, an absolute path, and a canonical path.
    //
    //.\file.txt - This is a path. It's neither an absolute path nor a canonical path.
    //
    //C:\temp\myapp\bin\..\\..\file.txt - This is a path and an absolute path. It's not a canonical path.
    //
    //A canonical path is always an absolute path.
    //
    //Converting from a path to a canonical path makes it absolute (usually tack on the current working directory so
    // e.g. ./file.txt becomes c:/temp/file.txt). The canonical path of a file just "purifies" the path, removing and resolving
    // stuff like ..\ and resolving symlinks (on unixes).

    private fun fileMethods() {
        // file methods/properties
//        file.isFile //this abstract pathname is a normal file. A file is normal if it is not a directory
//        file.absoluteFile //Returns the absolute form of this abstract pathname. Equivalent to new File(this.getAbsolutePath).
//        file.absolutePath //Returns the absolute path of this file. An absolute path is a path that starts at a root of the file system.
//        // On Android, there is only one root: /.
//        file.canonicalFile //Returns the canonical form of this abstract pathname. Equivalent to new File(this.getCanonicalPath).
//        file.freeSpace //Returns the number of unallocated bytes in the partition named by this abstract path name.
//        file.isAbsolute // true if this abstract pathname is absolute, false otherwise
//        file.isDirectory //true if and only if the file denoted by this abstract pathname exists and is a directory; false otherwise
//        file.isHidden//true if and only if the file denoted by this abstract pathname is hidden according to the conventions of the underlying platform
//        file.name
//        file.parent
//        file.parentFile //The abstract pathname of the parent directory named by this abstract pathname
//        file.totalSpace //Returns the size of the partition named by this abstract pathname.
//        file.usableSpace // this method checks for write permissions and other operating system restrictions and will therefore usually provide a
//        // more accurate estimate of how much new data can actually be written than getFreeSpace.
//        file.path
        // file.createTempFile()
        // file.canRead()
        //mkdir
        //length
        //delete
    }

    private fun streamRelated() {
        //                val opStream = FileOutputStream(file)
//                val printWriter = PrintWriter(opStream)
//                printWriter.write("First shared file to other apps")
//                printWriter.flush()
//                printWriter.close()
//                opStream.close()
//file.writeBytes("First shared file to other apps".toByteArray())
//saveData("First shared file to other apps", file.absolutePath)

// opstream, printwriter
// ipstream, bufferreader
    }

    private fun environmentFunctions(){
//        Environment.getExternalStorageDirectory()
//        Environment.getExternalStorageState()
//        Environment.getExternalStoragePublicDirectory()
//        Environment.getDataDirectory()  // /data directory
//        Environment.getRootDirectory()  // /root directory
//        Environment.getStorageDirectory()  // Return root directory where all external storage devices will be mounted
//        Environment.getDownloadCacheDirectory()  // download/cache content directory.
    }
}



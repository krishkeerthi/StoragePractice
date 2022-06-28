package com.example.storagepractice

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import com.example.storagepractice.databinding.ActivitySafactivityBinding
import java.io.*

// Storage Access Framework means Intent Action
// No permission needed, because user selects file using system picker

class SAFActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySafactivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySafactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createDocButton.setOnClickListener {
            createFile()
        }

        binding.openDocButton.setOnClickListener {
            openDocumentLauncher.launch(arrayOf("text/plain", "application/pdf")) // document types to enabled for selection
        }

        binding.grantButton.setOnClickListener {

        }
    }

    private fun createFile() {
//        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            type = "text/plain"
//            putExtra(Intent.EXTRA_TITLE, "invoice.txt")
//            putExtra(Intent.EXTRA_SUBJECT, "Sample document created using storage access framework")
//            putExtra(Intent.EXTRA_UID, "001")
//
//            // Optionally, specify a URI for the directory that should be opened in
//            // the system file picker before your app creates the document.
//            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
//        }
//
//        createFileLauncher.launch(intent)
        //startActivityForResult(intent, CREATE_FILE)

        createDocumentLauncher.launch("testfile2.txt")
    }

    private val createDocumentLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument()){
        Log.d(TAG, "created document : ${it.path}")
        writeDocumentFile(it)
    }

//    private val createFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
//    Log.d(TAG, "created document : ${it.data}")
//    }

    private val openDocumentLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()){
        readDocumentFile(it)
    }

    private fun writeDocumentFile(uri: Uri){
        // File descriptor
        //file descriptor class representing an open file, an open socket, or another source or sink of bytes.
        // The main practical use for a file descriptor is to create a FileInputStream or FileOutputStream to contain it.
        //Applications should not create their own file descriptors.
        val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "w")
        //The FileDescriptor returned by Parcel.readFileDescriptor, allowing you to close it when done with it.
        val fileOutputStream = FileOutputStream(parcelFileDescriptor?.fileDescriptor)
        //Creates a file output stream to write to the specified file descriptor, which represents an
        // existing connection to an actual file in the file system.
        fileOutputStream.write("this is a sample document created".toByteArray())
        fileOutputStream.close()
        parcelFileDescriptor?.close()
    }

    private fun readDocumentFile(uri: Uri){
        val fileInputStream = contentResolver.openInputStream(uri)
        val bufferedReader = BufferedReader(InputStreamReader(fileInputStream))
        //Creates a buffering character-input stream that uses a default-sized input buffer.

        val content = bufferedReader.useLines {
            it.fold(""){ some, text ->
                "$some \n$text"
            }
        }

        Log.d(TAG, "readDocumentFile: $content")

        fileInputStream?.close()
        bufferedReader.close()
    }

    private fun activityResultContract(){
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

        }

        registerForActivityResult(ActivityResultContracts.TakePicture()){

        }

        registerForActivityResult(ActivityResultContracts.TakePicturePreview()){

        }
        registerForActivityResult(ActivityResultContracts.TakeVideo()){

        }

        registerForActivityResult(ActivityResultContracts.CreateDocument()){

        }

        registerForActivityResult(ActivityResultContracts.OpenDocument()){

        }

        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()){

        }

        registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()){

        }
        registerForActivityResult(ActivityResultContracts.RequestPermission()){

        }
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){

        }

        registerForActivityResult(ActivityResultContracts.GetContent()){

        }
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()){

        }
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()){

        }
        registerForActivityResult(ActivityResultContracts.PickContact()){
        }
    }
}


//val fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
//fileDir.toUri()
//val uri = Uri.fromFile(File(fileDir ))
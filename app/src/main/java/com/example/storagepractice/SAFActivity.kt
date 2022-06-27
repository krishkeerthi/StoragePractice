package com.example.storagepractice

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.example.storagepractice.databinding.ActivitySafactivityBinding

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
            openDocumentLauncher.launch(arrayOf("invoice1.pdf", "dsjl", "lsdf"))
        }

        binding.grantButton.setOnClickListener {

        }
    }

    private val CREATE_FILE = 1

    private fun createFile(pickerInitialUri: Uri? = null) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "invoice.pdf")
            putExtra(Intent.EXTRA_SUBJECT, "Sample document created using storage access framework")

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
            //putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        createDocumentLauncher.launch("invoice1.pdf")
        //startActivityForResult(intent, CREATE_FILE)
    }

    private val createDocumentLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument()){
        Log.d(TAG, "created document : ${it.path}")
    }
//    private val createFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
//    Log.d(TAG, "created document : ${it.data}")
//    }

    private val openDocumentLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()){
        Log.d(TAG, "opened document : ${it}")
    }
}
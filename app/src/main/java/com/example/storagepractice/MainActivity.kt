package com.example.storagepractice

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.storagepractice.databinding.ActivityMainBinding
import com.example.storagepractice.databinding.ActivityMediaStoreBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.saveButton.setOnClickListener {
            saveData(binding.dataEdittext.text.toString(), "SampleFile")
        }

        binding.loadButton.setOnClickListener {
            binding.dataTextView.text = loadData("SampleFile")
        }

        binding.nextButton.setOnClickListener {
            startActivity(Intent(this, ActivityIEStorage::class.java))
        }

        //startActivity(Intent(this, DatabaseActivity::class.java))
    }

    private fun saveData(data: String, fileName: String){
        try{
            val opStream = openFileOutput(fileName, MODE_APPEND) // FileOutputStream
            // for writing. Creates the file if it doesn't already exist.
            //MODE_PRIVATE, MODE_WORLD_READABLE, MODE_WORLD_WRITEABLE, and MODE_APPEND

            opStream.write(data.toByteArray())
            //Encodes the contents of this string using the specified character set and returns the resulting byte array.
            opStream.close()
            //releases any system resources associated with this stream.

            Toast.makeText(this, "Saved to file successfully", Toast.LENGTH_SHORT).show()

        }
        catch (e: Exception){
            Toast.makeText(this, "Exception: ${e.message}", Toast.LENGTH_SHORT).show()
        }

    }

    private fun loadData(fileName: String): String{
        return try{  val ipStream = openFileInput(fileName)
//            var c: Int
//            var temp = ""
//            while (ipStream.read().also { c = it } != -1) {  // read returns int values, then converted to string
//                temp += c.toChar()//.toString()
//            }
//            temp
//

            //        or

            // FileInputStream  for reading purpose
//            openFileInput(fileName).bufferedReader().useLines {
//                it.fold("") { some, text ->  // accumulator
//                    "$some \n$text"
//                }
//            }
            //           or

            File(filesDir,fileName).readText()

        } catch (e: Exception){
            e.message.toString()
        }
    }
}

// op stream methods
//            opStream.channel
//            opStream.fd

// ip stream properties/methods
//ipStream.channel
//Returns the unique FileChannel object associated with this file input stream.
//The initial position of the returned channel will be equal to the number of bytes read from the file so far.
// Reading bytes from this stream will increment the channel's position.
//ipStream.fd
//Returns the FileDescriptor object that represents the connection to the actual file in the file system being used by this FileInputStream.
//ipStream.available()
//Returns an estimate of the number of remaining bytes that can be read (or skipped over) from this input stream without
// blocking by the next invocation of a method for this input stream. Returns 0 when the file position is beyond EOF.

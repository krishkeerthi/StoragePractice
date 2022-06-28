package com.example.storagepractice

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.storagepractice.databinding.ActivitySharedPreferenceBinding
// shared preference is a file which contains key value pair of data
class SharedPreferenceActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySharedPreferenceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySharedPreferenceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // getSharedPreferences(), callable from any context
        val sharedPref1 = getSharedPreferences(KEY1, Context.MODE_PRIVATE)
        val sharedPref2 = getSharedPreferences(KEY2, Context.MODE_PRIVATE)
        val sharedPref3 = getSharedPreferences(KEY3, Context.MODE_PRIVATE)

        // Write
        binding.writeButton.setOnClickListener {
            writeSharedPref(sharedPref1, sharedPref2, sharedPref3)
            Toast.makeText(this, "Write successful", Toast.LENGTH_SHORT).show()
        }

        // Read
        binding.readButton.setOnClickListener {
            readSharedPref(sharedPref1, sharedPref2, sharedPref3)
        }


        //getPreferences() this is available only to this activity
        val preference1 = getPreferences(Context.MODE_PRIVATE)  // No name for this preference
        val preference2 = getPreferences(Context.MODE_PRIVATE) // It points to the same activity preference

        with(preference1.edit()){
            putString("PREFERENCEKEY", "From shared Preference activity")
            apply()
        }

        Toast.makeText(this, "${preference2.getString("PREFERENCEKEY", "Default string")}", Toast.LENGTH_SHORT).show()
        //If you're using the SharedPreferences API to save app settings, you should instead use getDefaultSharedPreferences()
        // to get the default shared preference file for your entire app

    }

    private fun readSharedPref(sharedPref1: SharedPreferences, sharedPref2: SharedPreferences, sharedPref3: SharedPreferences) {
        val name = sharedPref1.getString("SHAREDPREF1NAME", "Not Mentioned")
        val score = sharedPref1.getInt("SHAREDPREF2SCORE", 100)
        val age = sharedPref1.getInt("SHAREDPREF3AGE", 17)

        Toast.makeText(this, "$name $score $age", Toast.LENGTH_SHORT).show()
    }

    private fun writeSharedPref(sharedPref1: SharedPreferences, sharedPref2: SharedPreferences, sharedPref3: SharedPreferences) {

        val sharedPref1Editor = sharedPref1.edit()
        sharedPref1Editor.apply {
            putString("SHAREDPREF1NAME", "Keerthi")
            apply()
        }

//        editor.remove("key")
//        editor.clear() //clears all keys

        with (sharedPref2.edit()) {
            putInt("SHAREDPREF2SCORE", 100)
            apply()
        }

        val sharedPref3Editor = sharedPref3.edit()
        sharedPref3Editor.apply {
            putInt("SHAREDPREF3AGE", 25)
            apply() //commit() also works
            // Consider using apply() instead; commit writes its data to persistent storage immediately,
        // whereas apply will handle it in the background
        }

    }

    companion object{
        const val KEY1 = "com.example.storagepractice.sharedprefkey1"
        const val KEY2 = "com.example.storagepractice.sharedprefkey2"
        const val KEY3 = "com.example.storagepractice.sharedprefkey3"
    }
}
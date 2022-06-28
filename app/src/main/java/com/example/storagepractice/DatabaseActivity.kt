package com.example.storagepractice

import android.content.ContentValues
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.widget.Toast
import com.example.storagepractice.databinding.ActivityDatabaseBinding

class DatabaseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDatabaseBinding
    private lateinit var dbHelper: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatabaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DbHelper(this)

        binding.insertButton.setOnClickListener {
            val rowId= insert(binding.usernameEdittext.text.toString(), binding.passwordTextview.text.toString()).toInt()

            if(rowId != -1){
                Toast.makeText(this, "Data inserted in table", Toast.LENGTH_SHORT).show()
                binding.passwordTextview.text.clear()
                binding.usernameEdittext.text.clear()
            }
            else
                Toast.makeText(this, "Error inserting", Toast.LENGTH_SHORT).show()

        }

        binding.deleteButton.setOnClickListener {
            val rowsAffected = deleteRecord("dell")

            if(rowsAffected > 0){
                Toast.makeText(this, "Data deleted from table", Toast.LENGTH_SHORT).show()
            }
            else
                Toast.makeText(this, "Error deleting", Toast.LENGTH_SHORT).show()
        }

        binding.updateButton.setOnClickListener {
            val rowsAffected = updateTable("keerthi", "ytrewq")

            if(rowsAffected > 0){
                Toast.makeText(this, "Data updated from table", Toast.LENGTH_SHORT).show()
            }
            else
                Toast.makeText(this, "Error updating", Toast.LENGTH_SHORT).show()

        }

        binding.readButton.setOnClickListener {
            readTable()
        }

    }

    private fun readTable(){
        val db = dbHelper.readableDatabase

        val projection = arrayOf(BaseColumns._ID, FeedEntry.COLUMN_NAME_USERNAME, FeedEntry.COLUMN_NAME_PASSWORD)

//        val selection = ""

//        val selectionArgs = arrayOf("")

        val sortOrder = "${FeedEntry.COLUMN_NAME_USERNAME} DESC"

        val query = db.query(
            FeedEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            sortOrder
        )

        val records = mutableListOf<String>()

        query?.use { cursor ->

            val idColumn = cursor.getColumnIndex(BaseColumns._ID)
            val usernameColumn = cursor.getColumnIndex(FeedEntry.COLUMN_NAME_USERNAME)
            val passwordColumn = cursor.getColumnIndex(FeedEntry.COLUMN_NAME_PASSWORD)

            while(cursor.moveToNext()){
                val id = cursor.getString(idColumn)
                val username = cursor.getString(usernameColumn)
                val password = cursor.getString(passwordColumn)

                records.add("$id $username $password")
            }
        }

        Toast.makeText(this, "${records.size}", Toast.LENGTH_SHORT).show()
        for(record in records)
            Log.d(TAG, "readTable: $record")

    }

    private fun insert(username: String, password: String): Long{
        val db = dbHelper.writableDatabase

        val contentValues = ContentValues().apply {
            put(FeedEntry.COLUMN_NAME_USERNAME, username)
            put(FeedEntry.COLUMN_NAME_PASSWORD, password)
        }

        return db.insert(FeedEntry.TABLE_NAME, null, contentValues)
    }

    private fun deleteRecord(username: String): Int{
        val db = dbHelper.writableDatabase

        val selection = "${FeedEntry.COLUMN_NAME_USERNAME} = ?"
        val selectionArgs = arrayOf(username)

        return db.delete(FeedEntry.TABLE_NAME, selection, selectionArgs)
    }

    private fun updateTable(username: String, password: String): Int{
        val db = dbHelper.writableDatabase

        val contentValues = ContentValues().apply {
            put(FeedEntry.COLUMN_NAME_PASSWORD, password)
        }

        val selection = "${FeedEntry.COLUMN_NAME_USERNAME} = ?"
        val selectionArgs = arrayOf(username)

        return db.update(FeedEntry.TABLE_NAME, contentValues, selection, selectionArgs )
    }

}
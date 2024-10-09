package a.co.varsitycollege.st10091229.myapplication

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.database.sqlite.SQLiteOpenHelper



class MainActivity : AppCompatActivity() {
    private var etName: EditText? = null
    private var etAge: EditText? = null
    private var btnAddData: Button? = null
    private var btnRetrieveData: Button? = null
    private var listView: ListView? = null
    private var dbHelper: DBHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etName = findViewById(R.id.etName)
        etAge = findViewById(R.id.etAge)
        btnAddData = findViewById(R.id.btnAddData)
        btnRetrieveData = findViewById(R.id.btnRetrieveData)
        listView = findViewById(R.id.listView)
        dbHelper = DBHelper(this)
        btnAddData?.setOnClickListener { insertData() }
        btnRetrieveData?.setOnClickListener { retrieveData() }
    }

    private fun insertData() {
        val name = etName!!.text.toString().trim { it <= ' ' }
        val ageStr = etAge!!.text.toString().trim { it <= ' ' }
        if (name.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(this, "Please enter name and age.", Toast.LENGTH_SHORT).show()
            return
        }
        val age = ageStr.toInt()
        val db: SQLiteDatabase = dbHelper!!.getWritableDatabase()
        val values = ContentValues()
        values.put(DBHelper.COLUMN_NAME, name)
        values.put(DBHelper.COLUMN_AGE, age)
        val newRowId = db.insert(DBHelper.TABLE_NAME, null, values)
        if (newRowId != -1L) {
            Toast.makeText(this, "Data inserted successfully.", Toast.LENGTH_SHORT).show()
            etName!!.setText("")
            etAge!!.setText("")
        } else {
            Toast.makeText(this, "Error inserting data.", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }

    private fun retrieveData() {
        val db: SQLiteDatabase = dbHelper!!.getReadableDatabase()
        val projection = arrayOf<String>(
            DBHelper.COLUMN_NAME,
            DBHelper.COLUMN_AGE
        )
        val cursor = db.query(
            DBHelper.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        val dataList = ArrayList<String>()
        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_NAME))
            val age = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_AGE))
            dataList.add("Name: $name, Age: $age")
        }
        cursor.close()
        db.close()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dataList)
        listView!!.adapter = adapter
    }
}

class DBHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TABLE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "MyData.db"
        private const val DATABASE_VERSION = 1

        // Define the table and columns
        const val TABLE_NAME = "person"
        private const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_AGE = "age"

        // Create the table query
        private const val TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_AGE + " INTEGER);"
    }
}



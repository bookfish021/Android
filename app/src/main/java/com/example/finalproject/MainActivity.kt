package com.example.finalproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    lateinit var mainlistview : ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        val plus: Button = findViewById(R.id.New)
        val getbool = intent.getBooleanExtra("bool", false)
        var getalldiary = intent.getStringArrayListExtra("alldiary")
        var getallroute = intent.getStringArrayListExtra("allroute")
        var getalldiarycontent = intent.getStringArrayListExtra("alldiarycontent")
        val myAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        mainlistview = findViewById(R.id.mainlistview)
        if (getbool) {
            if (getalldiary != null && getalldiarycontent != null && getallroute != null) {
                for (i in getalldiary) {
                    myAdapter.add(i)
                }
                plus.setOnClickListener { newDiary(getalldiary, getalldiarycontent, getallroute) }
            }
        } else {
            var alldiary: ArrayList<String> = ArrayList()
            var alldiarycontent: ArrayList<String> = ArrayList()
            var allroute: ArrayList<String> = ArrayList()
            plus.setOnClickListener { newDiary(alldiary, alldiarycontent, allroute) }
        }
        mainlistview.setOnItemClickListener { adapterView, view, i, id ->
            val thediary = myAdapter.getItem(i)
            val intent = Intent(this, DiaryContent::class.java)
            intent.putStringArrayListExtra("alldiary", getalldiary)
            intent.putStringArrayListExtra("alldiarycontent", getalldiarycontent)
            intent.putStringArrayListExtra("allroute", getallroute)
            intent.putExtra("thediary", thediary)
            intent.putExtra("id", id)
            startActivity(intent)
        }
        mainlistview.adapter = myAdapter
    }
    private fun newDiary(alldiary : ArrayList<String>, alldiarycontent: ArrayList<String>, allroute: ArrayList<String>)
    {
        val Newdiary : Intent = Intent()
        Newdiary.setClass(this, NewDiary::class.java)
        Newdiary.putStringArrayListExtra("alldiary", alldiary)
        Newdiary.putStringArrayListExtra("alldiarycontent", alldiarycontent)
        Newdiary.putStringArrayListExtra("allroute", allroute)
        startActivity(Newdiary)
    }
}
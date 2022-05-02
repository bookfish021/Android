package com.example.finalproject

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class DiaryContent : AppCompatActivity(), OnMapReadyCallback {
    lateinit var title : TextView
    lateinit var content: TextView
    lateinit var image: ImageView
    private lateinit var mMap: GoogleMap
    private lateinit var pol: Polyline
    private var start = LatLng(25.150783, 121.775669)
    private var end = LatLng(25.047897, 121.517348)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_content)
        title = findViewById(R.id.title)
        content = findViewById(R.id.content)
        image = findViewById(R.id.image)
        var thediary = intent.getStringExtra("thediary")
        var i = intent.getLongExtra("id", 0)
        var alldiarycontent = intent.getStringArrayListExtra("alldiarycontent")
        var allroute = intent.getStringArrayListExtra("allroute")

        title.text = thediary
        val fullContent = alldiarycontent?.get(i.toInt())
        val (newContent, scanUri) = fullContent.toString().split("img:")
        val fullroute = allroute?.get(i.toInt())
        val (s1, s2, e1, e2) = fullroute.toString().split(",")
        start = LatLng(s1.toDouble(), s2.toDouble())
        end = LatLng(e1.toDouble(), e2.toDouble())
        content.setText(newContent)
        if(scanUri == null.toString())
            image.setImageResource(R.drawable.galleryicon)
        else
            image.setImageURI(Uri.parse(scanUri))
        val back : Button = findViewById(R.id.back)
        back.setOnClickListener{ back() }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    private fun back()
    {
        var alldiary = intent.getStringArrayListExtra("alldiary")
        var alldiarycontent = intent.getStringArrayListExtra("alldiarycontent")
        var allroute = intent.getStringArrayListExtra("allroute")
        val intent : Intent = Intent()
        intent.setClass(this, MainActivity::class.java)
        intent.putExtra("bool", true)
        intent.putStringArrayListExtra("alldiary", alldiary)
        intent.putStringArrayListExtra("alldiarycontent", alldiarycontent)
        intent.putStringArrayListExtra("allroute", allroute)
        startActivity(intent)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera

        mMap.addMarker(
            MarkerOptions()
                .position(end)
                .title("destination")
        )

        mMap.addMarker(
            MarkerOptions()
                .position(start)
                .title("starting point")
                .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation))

        )

        pol = mMap.addPolyline(
            PolylineOptions()
                .add(
                    start,
                    end))
        pol.color = -0xc771c4
        mMap.moveCamera(CameraUpdateFactory.newLatLng(start))
    }
}
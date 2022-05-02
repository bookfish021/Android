package com.example.finalproject

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
//map
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
//location
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager

class NewDiary : AppCompatActivity(), GoogleMap.OnMarkerDragListener, LocationListener,OnMapReadyCallback {

    lateinit var topic : EditText
    lateinit var time: EditText
    lateinit var article: EditText
    lateinit var show_iv: ImageView
    lateinit var imageUri: String
    private val ACTION_CAMERA_REQUEST_CODE = 100
    private lateinit var mMap: GoogleMap
    private lateinit var startMarker: Marker
    private lateinit var finishMarker: Marker
    private lateinit var pol:Polyline
    private var start = LatLng(25.150783, 121.775669)
    private var end = LatLng(25.047897, 121.517348)
    lateinit var locmgr: LocationManager
    var mapReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_diary)
        topic = findViewById(R.id.topic)
        time = findViewById(R.id.time)
        article = findViewById(R.id.article)
        val add : Button = findViewById(R.id.add)
        add.setOnClickListener{ addNew() }

        //        新增圖片
        val btn_addphoto: Button = findViewById(R.id.btn_addphoto)
        btn_addphoto.setOnClickListener { add_photo() }
        imageUri = null.toString()
        show_iv = findViewById(R.id.show_iv)
        show_iv.setImageResource(R.drawable.galleryicon)
        //  Map get location
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION),
                1)
        } else {
            initLoc()
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    private fun addNew()
    {

        var alldiary = intent.getStringArrayListExtra("alldiary")
        alldiary?.add(
            "標題 : " + topic.getText().toString() + "\n時間 : " + time.getText().toString()
        )
        var alldiarycontent = intent.getStringArrayListExtra("alldiarycontent")
        alldiarycontent?.add(article.getText().toString() + "\n" + "img:" + imageUri)
        var allroute = intent.getStringArrayListExtra("allroute")
        allroute?.add(
            start.latitude.toString() + "," + start.longitude.toString() + "," + end.latitude.toString() + "," + end.longitude.toString()
        )
        val intent: Intent = Intent()
        intent.setClass(this, MainActivity::class.java)
        intent.putExtra("bool", true)
        intent.putStringArrayListExtra("alldiary", alldiary)
        intent.putStringArrayListExtra("alldiarycontent", alldiarycontent)
        intent.putStringArrayListExtra("allroute", allroute)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == ACTION_CAMERA_REQUEST_CODE){
            val uri: Uri? = data?.data
            imageUri = uri.toString()
            val realPath = getRealPathFromURI(Uri.parse(imageUri))
            imageUri = realPath.toString()
            val file = File(realPath)
            imageUri = file.toURI().toString()
            show_iv.setImageURI(Uri.parse(imageUri)) // handle chosen image
//            et_body.setText(imageUri + "\n" + file.toURI().toString())
        }
    }
    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor =
            contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }


    // 通過 intent 使用 album
    private fun add_photo(){
        println("take image from album")

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, ACTION_CAMERA_REQUEST_CODE)
    }

    //Map function start
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode,
            permissions, grantResults)

        if ((grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED)) {
            initLoc()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera

        finishMarker = mMap.addMarker(
            MarkerOptions()
                .position(end)
                .title("destination")
                .draggable(true)
        )

        startMarker = mMap.addMarker(
            MarkerOptions()
                .position(start)
                .title("starting point")
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation))

        )

        pol = mMap.addPolyline(
            PolylineOptions()
                .add(
                    start,
                    end))
        pol.color = -0xc771c4
        mMap.moveCamera(CameraUpdateFactory.newLatLng(start))
        mMap.setOnMarkerDragListener(this)
        mapReady = true
    }

    override fun onMarkerDragEnd(p: Marker?) {
        if (p == startMarker){
            start = LatLng(p.position.latitude, p.position.longitude)
            startMarker.remove()
            startMarker = mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(p.position.latitude, p.position.longitude))
                    .title("starting point")
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation))
            )
            pol.remove()
            pol = mMap.addPolyline(
                PolylineOptions()
                    .add(
                        start,
                        end))
            pol.color = -0xc771c4
        } else if (p == finishMarker){
            end = LatLng(p.position.latitude, p.position.longitude)
            finishMarker.remove()
            finishMarker = mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(p.position.latitude, p.position.longitude))
                    .title("destination")
                    .draggable(true)
            )
            pol.remove()
            pol = mMap.addPolyline(
                PolylineOptions()
                    .add(
                        start,
                        end))
            pol.color = -0xc771c4
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locmgr.removeUpdates(this)
    }

    override fun onLocationChanged(loc: Location) {
    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onProviderDisabled(provider: String) {
    }
    override fun onMarkerDragStart(p0: Marker?) {}
    override fun onMarkerDrag(p0: Marker?) {}

    private fun initLoc() {
        locmgr = getSystemService(LOCATION_SERVICE) as
                LocationManager

        var loc: Location? = null
        try {
            loc = locmgr.getLastKnownLocation(
                LocationManager.GPS_PROVIDER)
            if (loc == null) {
                loc = locmgr.getLastKnownLocation(
                    LocationManager.NETWORK_PROVIDER)
            }
        } catch (e: SecurityException) {
        }

        if (loc != null) {
            start = LatLng(loc.latitude, loc.longitude)
            if(mapReady){
                startMarker.remove()
                startMarker = mMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(loc.latitude, loc.longitude))
                        .title("starting point")
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation))
                )
                pol.remove()
                pol = mMap.addPolyline(
                    PolylineOptions()
                        .add(
                            start,
                            end))
                pol.color = -0xc771c4
            }
        }

        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        val provider: String? = locmgr.getBestProvider(
            criteria, true)

        try {
            if (provider != null) {
                locmgr.requestLocationUpdates(provider,
                    1000, 1f, this)
            } else {
                locmgr.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000, 1f, this)
            }
        } catch (e: SecurityException) {
        }
    }
    //Map function end
}
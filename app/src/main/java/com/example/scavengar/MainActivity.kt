package com.example.scavengar

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.github.sceneview.SceneView
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.launch
import java.util.*
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context


class MainActivity : AppCompatActivity() {

    private lateinit var locationManager : LocationManager
    private lateinit var fetchLocation : Button
//    private val locationPermissionCode = 2
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2

    private lateinit var lat : String
    private lateinit var long : String

    private lateinit var arscan : Button

//    private lateinit var scanner : Button

    private var oat : Location = Location("")
    private var dbh : Location = Location("")
    private var opengym : Location = Location("")
    private var tennis : Location = Location("")
    private var juicebar : Location = Location("")
    private var myLocation : Location = Location("")

    private val modelNode = ModelNode()

//    private lateinit var sceneView: ArSceneView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fetchLocation = findViewById(R.id.fetchlocation)
        arscan = findViewById(R.id.arscan)

        oat.latitude = 31.7060450
        oat.longitude = 76.5254121

        dbh.latitude = 31.7118001
        dbh.longitude = 76.5249494

        opengym.latitude = 31.7091290
        opengym.longitude = 76.5227127

        tennis.latitude = 31.7058605
        tennis.longitude = 76.5239382

        juicebar.latitude = 31.7054263
        juicebar.longitude = 76.5276316

//        scanner = findViewById(R.id.scanner)

//        sceneView = findViewById(R.id.sceneView)
//
//        sceneView.addChild(modelNode)


        fetchLocation.setOnClickListener {
//            Toast.makeText(this,"No clue in the location",Toast.LENGTH_LONG).show()
            getLocation()

        }

        arscan.setOnClickListener {

            if(myLocation.checkIsInBound(20.0,oat)){
                Toast.makeText(this,"OAT",Toast.LENGTH_LONG).show()
//                scanner.visibility = View.VISIBLE
                val intent = Intent(this,ScanActivity::class.java)
                intent.putExtra("glbname","racket")
                startActivity(intent)
            }else if(myLocation.checkIsInBound(20.0,tennis)){
                Toast.makeText(this,"Tennis Court",Toast.LENGTH_LONG).show()
                val intent = Intent(this,ScanActivity::class.java)
                intent.putExtra("glbname","phone")
                startActivity(intent)
//                scanner.visibility = View.VISIBLE
            }else if(myLocation.checkIsInBound(20.0,juicebar)){
                Toast.makeText(this,"JUICE BAR",Toast.LENGTH_LONG).show()
                val intent = Intent(this,ScanActivity::class.java)
//                scanner.visibility = View.VISIBLE
                intent.putExtra("glbname","Spoons")
                startActivity(intent)
            }else if(myLocation.checkIsInBound(20.0,opengym)){
                Toast.makeText(this,"OPEN GYM",Toast.LENGTH_LONG).show()
                val intent = Intent(this,ScanActivity::class.java)
//                scanner.visibility = View.VISIBLE
                intent.putExtra("glbname","racket")
                startActivity(intent)
            }else if(myLocation.checkIsInBound(20.0,opengym)){
                Toast.makeText(this," Co",Toast.LENGTH_LONG).show()
                val intent = Intent(this,ScanActivity::class.java)
                intent.putExtra("glbname","phone")
                startActivity(intent)
//                scanner.visibility = View.VISIBLE
            }
            else{
                Toast.makeText(this,"Please scan first",Toast.LENGTH_LONG).show()
            }

//            lifecycleScope.launch {
//                ARView()
//            }
        }


    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>
                            Toast.makeText(this,"${list[0].latitude},${list[0].longitude}",Toast.LENGTH_LONG).show()
                        lat = list[0].latitude.toString()
                        long = list[0].longitude.toString()

                        myLocation = location

                        if(location.checkIsInBound(20.0,oat) || location.checkIsInBound(20.0,dbh) || location.checkIsInBound(20.0,juicebar) || location.checkIsInBound(20.0,tennis) || location.checkIsInBound(20.0,opengym)){
                            vibratePhone()
                        }else{
                            Toast.makeText(this,"No clue in the location",Toast.LENGTH_LONG).show()
                        }

                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    fun vibratePhone() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) { // Vibrator availability checking
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)) // New vibrate method for API Level 26 or higher
            } else {
                vibrator.vibrate(500) // Vibrate method for below API Level 26
            }
        }
    }

    fun Location.checkIsInBound(radius: Double,center:Location):Boolean
            =  this.distanceTo(center)<radius

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

//
//    suspend fun ARView(){
//
//        Toast.makeText(this,"Started",Toast.LENGTH_LONG).show()
//        modelNode.loadModelGlb(
//            context = this,
//            glbFileLocation = "models/Spoons.glb",
//            lifecycle = lifecycle,
//            autoAnimate = true,
//            centerOrigin = Position(x = -1.0f, y = 1.0f,z = 0.0f),
//            onError = { exception -> }
//        )
//
//    }



}
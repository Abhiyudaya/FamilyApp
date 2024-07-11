package com.example.familyapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.core.content.PackageManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.READ_CONTACTS,
    )
    val permissionCode = 78
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        askForPermission()

        val bottomBar = findViewById<BottomNavigationView>(R.id.bottom_bar)
        bottomBar.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.nav_guard -> {
                    inflateFragment(GuardFragment.newInstance())
                }
                R.id.nav_home -> {
                    inflateFragment(HomeFragment.newInstance())
                }
                R.id.nav_dashboard -> {
                    inflateFragment(MapsFragment())
                }
                R.id.nav_profile -> {
                    inflateFragment(ProfileFragment.newInstance())
                }
            }
            true
        }
        bottomBar.selectedItemId=R.id.nav_home
    }

    private fun askForPermission() {
        ActivityCompat.requestPermissions(this,permissions,permissionCode)
    }

    private fun inflateFragment(newInstance: Fragment) {

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,newInstance)
        transaction.commit()

    }

    private fun allPermissionGranted(): Boolean {
        for(item in permissions){
            if(ContextCompat.checkSelfPermission(this,item) != PackageManager.PERMISSION_GRANTED){
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

//        if(requestCode == permissionCode){
//            if(allPermissionGranted()){
//                openCamera()
//            }
//            else{
//
//            }
//        }
    }

    private fun openCamera() {
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        startActivity(intent)
    }
}
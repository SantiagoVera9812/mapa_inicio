package com.example.practica.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.icu.text.Transliterator.Position
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.practica.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions

class MapsFragment : Fragment() {

    private var polygon: Polygon? = null
    private var userMarker: Marker? = null
    private lateinit var gmap: GoogleMap
    private var circle: Circle? = null
    private var circle2: Circle? = null
    private var circle3: Circle? = null
    private val polygons: MutableList<Polygon> = mutableListOf()

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        gmap = googleMap

        val sydney = LatLng(-34.0, 151.0)

        val markerOptions = MarkerOptions()
            .position(sydney)
            .icon(context?.let { bitmapDescriptorFromVector(it, R.drawable.baseline_location_pin_24) })

        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10f))

        userMarker = gmap.addMarker(markerOptions)
        gmap.uiSettings.isZoomControlsEnabled = false
        gmap.uiSettings.isCompassEnabled = true


        gmap.setOnMapClickListener { latLng ->

            if(polygons.isNotEmpty()){
                polygon?.remove()
                polygons.remove(polygon)
            }

            userMarker?.remove()

            val polygonOptions = PolygonOptions()
                .add(
                    LatLng(  latLng.latitude + 0.02, latLng.longitude + 0.02),
                    LatLng(latLng.latitude - 0.02, latLng.longitude + 0.02),
                    LatLng(latLng.latitude + 0.02, latLng.longitude - 0.02),
                    LatLng(latLng.latitude - 0.02, latLng.longitude - 0.02)
                )
                .strokeColor(Color.RED) // Color del borde
                .fillColor(Color.BLUE)  // Color del relleno
                .strokeWidth(5f)

            polygon = gmap.addPolygon(polygonOptions)
            polygons.add(polygon!!)

            val circleOptionsInner = CircleOptions()
                .center(latLng)
                .radius(100.0)
                .strokeColor(Color.RED)
                .fillColor(Color.GREEN)
                .strokeWidth(5f)

            val circleOptionsMiddle = CircleOptions()
                .center(latLng)
                .radius(300.0)
                .strokeColor(Color.RED)
                .fillColor(Color.YELLOW)
                .strokeWidth(5f)

            val circleOptionsRed = CircleOptions()
                .center(latLng)
                .radius(500.0)
                .strokeColor(Color.RED)
                .fillColor(Color.RED)
                .strokeWidth(5f)

            circle3 = gmap.addCircle(circleOptionsRed)
            circle2 = gmap.addCircle(circleOptionsMiddle)
            circle = gmap.addCircle(circleOptionsInner)

        }

    }


    private fun deletePolygon(){
        polygon?.remove()
    }

    fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}
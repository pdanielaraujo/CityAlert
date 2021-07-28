package pt.atp.cityalert.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pt.atp.cityalert.AddOccurrenceActivity
import pt.atp.cityalert.R
import pt.atp.cityalert.ViewSpecificOccurrenceActivity
import pt.atp.cityalert.api.EndPoints
import pt.atp.cityalert.api.Ocorrencia
import pt.atp.cityalert.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private lateinit var marker_list: ArrayList<Marker>
    private lateinit var googleMap: GoogleMap
    private lateinit var marker: Marker

    // last know location
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // location requests
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private lateinit var fabAdd: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(R.layout.fragment_home, container, false)

        marker_list = arrayListOf()
        val mapFragment = root.findViewById(R.id.map_view) as MapView
        mapFragment.onCreate(savedInstanceState)
        mapFragment.onResume()
        mapFragment.getMapAsync(this)

        getOccurrences()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationCallback = object :  LocationCallback(){
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
                var loc = LatLng(lastLocation.latitude, lastLocation.longitude)
                //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.0f))
                Log.d("aa", "localização nova: " + loc.latitude + " - " + loc.longitude)
            }
        }

        createLocationRequest()

        fabAdd = root.findViewById(R.id.add_occurrence_btn)

        fabAdd.setOnClickListener {
            var posicao = LatLng(lastLocation.latitude, lastLocation.longitude)
            val lat = lastLocation.latitude
            val lng = lastLocation.longitude
            val intent = Intent(context, AddOccurrenceActivity::class.java).apply {
                putExtra("lat", lat.toString())
                putExtra("lng", lng.toString())
            }
            startActivityForResult(intent, 123)
        }

        // Inflate the layout for this fragment
        return root
    }

    override fun onMapReady(map: GoogleMap) {
        val starting_point = LatLng(39.804215, -8.069341)
        googleMap = map
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(starting_point, 7f))
        googleMap.setOnInfoWindowClickListener(this)

        getLocationAccess()

        Log.d("aa", "cona")
        /*map.let{
            googleMap = it

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(starting_point, 7f))
            map.setOnInfoWindowClickListener(this)

        }*/
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
        Log.d("aa", "onResume - startLocationUpdates")
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d("aa", "onPause - removeLocationUpdates")
    }

    private fun startLocationUpdates(){
        if(ActivityCompat.checkSelfPermission(requireContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper() /* Looper */)
    }

    private fun createLocationRequest(){
        locationRequest = LocationRequest()
        //intervalos de 10mil milisegundos entre atualizações de coordenadas
        locationRequest.interval = 10000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    fun getOccurrences(){
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val callOcorrencias = request.getOcorrencias()

        callOcorrencias.enqueue(object : Callback<List<Ocorrencia>> {
            override fun onResponse(call: Call<List<Ocorrencia>>, response: Response<List<Ocorrencia>>) {
                if(response.isSuccessful){
                    response.body()!!.forEach() {
                        //var marker: Marker?

                        val latLng = LatLng(it.latitude.toDouble(), it.longitude.toDouble())
                        val sharedPref: SharedPreferences = activity!!.getSharedPreferences(
                                getString(R.string.preference_file_key),
                                Context.MODE_PRIVATE
                        )

                        val id_ocorrencia = it.id
                        val foto = it.foto
                        var fotoBitmap: Bitmap
                        val occurrence_pessoa_id = it.pessoa_id

                        Log.d("aa", "id pessoa dentro FOR: " + occurrence_pessoa_id.toString())
                        val pessoa_id_sharedPref = sharedPref.getInt(getString(R.string.person_id), 0)
                        Log.d("aa", pessoa_id_sharedPref.toString())
                        val tipo_ocorrencia = it.tipo_id
                        val titulo_ocorrencia = it.descricao

                        val markerOpt = MarkerOptions()
                        val markerOptPersonal = MarkerOptions()
                        val markerOptTypeOne = MarkerOptions()
                        val markerOptTypeTwo = MarkerOptions()
                        val markerOptTypeThree = MarkerOptions()

                        val tipoBuracoEstrada = 207F
                        val tipoQuedaArvore = 54F
                        val tipoQuedaPoste = 289F

                        //(BitmapDescriptorFactory.HUE_AZURE)

                        markerOpt.position(latLng)
                                .title(titulo_ocorrencia + " / Tipo: " + tipo_ocorrencia.toString())
                                .icon(BitmapDescriptorFactory.defaultMarker())
                                .snippet(id_ocorrencia.toString())

                        markerOptPersonal.position(latLng)
                                .title(titulo_ocorrencia + " / Tipo: " + tipo_ocorrencia.toString())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                .snippet(id_ocorrencia.toString())

                        markerOptTypeOne.position(latLng)
                                .title(titulo_ocorrencia + " / Tipo: " + tipo_ocorrencia.toString())
                                .icon(BitmapDescriptorFactory.defaultMarker(tipoBuracoEstrada))
                                .snippet(id_ocorrencia.toString())

                        markerOptTypeTwo.position(latLng)
                                .title(titulo_ocorrencia + " / Tipo: " + tipo_ocorrencia.toString())
                                .icon(BitmapDescriptorFactory.defaultMarker(tipoQuedaArvore))
                                .snippet(id_ocorrencia.toString())

                        markerOptTypeThree.position(latLng)
                                .title(titulo_ocorrencia + " / Tipo: " + tipo_ocorrencia.toString())
                                .icon(BitmapDescriptorFactory.defaultMarker(tipoQuedaPoste))
                                .snippet(id_ocorrencia.toString())

                        //marker = map.addMarker(markerOpt)

                        //marker.showInfoWindow()

                        /*if (occurrence_pessoa_id == pessoa_id_sharedPref) {
                            marker = map.addMarker(markerOptPersonal)
                            marker_list.add(marker)
                        } else{
                            marker_list.add(marker)
                        }*/

                        if(occurrence_pessoa_id == pessoa_id_sharedPref){
                            marker = googleMap.addMarker(markerOptPersonal)
                            marker_list.add(marker)
                        } else if(tipo_ocorrencia == 1) {
                            marker = googleMap.addMarker(markerOptTypeOne)
                            marker_list.add(marker)
                        } else if(tipo_ocorrencia == 2){
                            marker = googleMap.addMarker(markerOptTypeTwo)
                            marker_list.add(marker)
                        } else if(tipo_ocorrencia == 3){
                            marker = googleMap.addMarker(markerOptTypeThree)
                            marker_list.add(marker)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Ocorrencia>>, t: Throwable) {
                Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onInfoWindowClick(marker: Marker) {
        Toast.makeText(context, marker.snippet, Toast.LENGTH_SHORT).show()
        val intent = Intent(context, ViewSpecificOccurrenceActivity::class.java).apply {
            putExtra("occurrenceId", marker.snippet)
        }
        startActivityForResult(intent, 123)
    }

    private val LOCATION_PERMISSION_REQUEST = 1


    private fun getLocationAccess() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
        }
        else
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                googleMap.isMyLocationEnabled = true
            }
            else {
                Toast.makeText(context, "User has not granted location access permission", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
package pt.atp.cityalert.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import pt.atp.cityalert.R
import pt.atp.cityalert.ViewSpecificNoteActivity
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
        // Inflate the layout for this fragment
        return root

    }

    override fun onMapReady(map: GoogleMap) {
        val starting_point = LatLng(39.804215, -8.069341)
        map.let{
            googleMap = it

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(starting_point, 7f))
            map.setOnInfoWindowClickListener(this)
            val request = ServiceBuilder.buildService(EndPoints::class.java)
            val callOcorrencias = request.getOcorrencias()

            callOcorrencias.enqueue(object : Callback<List<Ocorrencia>> {
                override fun onResponse(call: Call<List<Ocorrencia>>, response: Response<List<Ocorrencia>>) {
                    if(response.isSuccessful){
                        response.body()!!.forEach() {
                            //var marker: Marker?

                            val latLng = LatLng(it.latitude.toDouble(), it.longitude.toDouble())
                            val sharedPref: SharedPreferences = requireActivity().getSharedPreferences(
                                    getString(R.string.preference_file_key),
                                    Context.MODE_PRIVATE
                            )

                            val id_ocorrencia = it.id_ocorrencia
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
                                marker = map.addMarker(markerOptPersonal)
                                marker_list.add(marker)
                            } else if(tipo_ocorrencia == 1) {
                                marker = map.addMarker(markerOptTypeOne)
                                marker_list.add(marker)
                            } else if(tipo_ocorrencia == 2){
                                marker = map.addMarker(markerOptTypeTwo)
                                marker_list.add(marker)
                            } else if(tipo_ocorrencia == 3){
                                marker = map.addMarker(markerOptTypeThree)
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
}
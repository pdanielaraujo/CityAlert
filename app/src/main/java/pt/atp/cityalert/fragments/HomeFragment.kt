package pt.atp.cityalert.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import pt.atp.cityalert.LoginActivity
import pt.atp.cityalert.R

class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(R.layout.fragment_home, container, false)

        val mapFragment = root.findViewById(R.id.map_view) as MapView
        mapFragment?.onCreate(savedInstanceState)
        mapFragment.onResume()
        mapFragment.getMapAsync(this)
        // Inflate the layout for this fragment
        return root

    }

    override fun onMapReady(map: GoogleMap) {
        val sydney = LatLng(-33.852, 151.211)
        map.let{
            googleMap = it
            map.addMarker(
                    MarkerOptions()
                            .position(sydney)
                            .title("Marker in Sydney")
            )

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initialize views (buttons, texts, etc...)
        val button: Button = view.findViewById(R.id.go_login_btn)

        button.setOnClickListener{
            val intent = Intent(this.context, LoginActivity::class.java)
            startActivity(intent)
        }
    }

}
package pt.atp.cityalert.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.finishAffinity
import pt.atp.cityalert.LoginActivity
import pt.atp.cityalert.MainActivity
import pt.atp.cityalert.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PerfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PerfilFragment : Fragment() {

    private lateinit var perfil_fragment: View
    private lateinit var btn_logout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        perfil_fragment = inflater.inflate(R.layout.fragment_perfil, container, false)

        // inicializar variaveis
        btn_logout = perfil_fragment.findViewById(R.id.btn_logout)

        // click listener
        btn_logout.setOnClickListener {
            val sharedPref: SharedPreferences = requireActivity().getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
            with(sharedPref.edit()){
                putBoolean(getString(R.string.session_status), false)
                commit()
            }
            val i = Intent(activity, LoginActivity::class.java)
            activity?.finishAffinity()
            startActivity(i)
        }
        // Inflate the layout for this fragment
        return perfil_fragment
    }
}
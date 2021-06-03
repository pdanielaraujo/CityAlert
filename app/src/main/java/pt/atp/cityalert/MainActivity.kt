package pt.atp.cityalert

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import pt.atp.cityalert.fragments.HomeFragment
import pt.atp.cityalert.fragments.NotesFragment
import pt.atp.cityalert.fragments.PerfilFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val home_fragment = HomeFragment()
        val notes_fragment = NotesFragment()
        val perfil_fragment = PerfilFragment()

        setMainFragment(home_fragment)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.view_notes_toolbar)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.home -> setMainFragment(home_fragment)
                R.id.notes -> setMainFragment(notes_fragment)
                R.id.person -> setMainFragment(perfil_fragment)
            }
            true
        }

        when(intent.getStringExtra("goToNotesFromLogin")){
            "openFragment" -> {
                supportFragmentManager.beginTransaction().replace(R.id.layout_fragment, notes_fragment).commit()
            }
        }

        val sharedPref: SharedPreferences = this.getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        val isLogged = sharedPref.getBoolean(getString(R.string.session_status), false)
        if(isLogged){
            toolbar?.setNavigationOnClickListener{
                val i = Intent(this, LoginActivity::class.java)
                finishAffinity()
                startActivity(i)
            }
            bottomNavigationView?.visibility = View.GONE
        }

    }

    private fun setMainFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.layout_fragment, fragment)
            commit()
        }

    //fun btn(view: View){
    //    val intent = Intent(this, LoginActivity::class.java).apply {

    //   }
    //    startActivity(intent)
    //}
}

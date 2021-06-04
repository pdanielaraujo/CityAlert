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

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.home -> setMainFragment(home_fragment)
                R.id.notes -> setMainFragment(notes_fragment)
                R.id.person -> setMainFragment(perfil_fragment)
            }
            true
        }

        /* Recebe o intent enviado através da página Login esconde
        o BottomNavigationView para o user não conseguir navegar para outra página*/
        when(intent.getStringExtra("goToNotesFromLogin")){
            "openFragment" -> {
                supportFragmentManager.beginTransaction().replace(R.id.layout_fragment, notes_fragment).commit()
                bottomNavigationView.visibility = View.GONE
            }
        }
    }

    private fun setMainFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.layout_fragment, fragment)
            commit()
        }
}

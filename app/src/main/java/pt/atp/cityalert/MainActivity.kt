package pt.atp.cityalert

import android.content.Intent
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

    }

    private fun setMainFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.layout_fragment, fragment)
            commit()
        }

    fun btn(view: View){
        val intent = Intent(this, LoginActivity::class.java).apply {

        }
        startActivity(intent)
    }
}
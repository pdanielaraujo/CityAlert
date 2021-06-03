package pt.atp.cityalert

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import pt.atp.cityalert.api.EndPoints
import pt.atp.cityalert.api.OutputPost
import pt.atp.cityalert.api.ServiceBuilder
import pt.atp.cityalert.fragments.NotesFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    private lateinit var inserted_email: EditText
    private lateinit var inserted_passwd: EditText
    private lateinit var btn_login: Button
    private lateinit var btn_goToNotes: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //inicialiazr variaveis
        inserted_email = findViewById(R.id.edit_email)
        inserted_passwd = findViewById(R.id.edit_passwd)
        btn_login = findViewById(R.id.btn_login)
        btn_goToNotes = findViewById(R.id.btn_viewnotes_prelogin)

        val sharedPref: SharedPreferences = getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        val isLogged = sharedPref.getBoolean(getString(R.string.session_status), false)

        // Se estiver com sessão iniciada a entrar na app redireciona automaticamente para a páginal inicial
        if(isLogged){
            val i = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(i)
            finish()
        }

        btn_login.setOnClickListener {
            login()
        }

        btn_goToNotes.setOnClickListener {
            val i = Intent(this@LoginActivity, MainActivity::class.java)
            i.putExtra("goToNotesFromLogin","openFragment")
            startActivity(i)
        }

    }

    fun login(){
        if(TextUtils.isEmpty(inserted_email.text) || TextUtils.isEmpty(inserted_passwd.text)){
            Toast.makeText(this, "Insert some text", Toast.LENGTH_SHORT).show()
        }else{
            getSinglePerson()
        }
    }

    fun getSinglePerson(){
        val email = inserted_email.text.toString()
        val psw = inserted_passwd.text.toString()
        Log.d("aaa", "bbb")
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val callPersonPost = request.postEmail(email, psw)
        callPersonPost.enqueue(object : Callback<OutputPost>{
            override fun onResponse(call: Call<OutputPost>, response: Response<OutputPost>) {
                Log.d("aaa", response.toString())
                Log.d("aaa", response.errorBody().toString())
                if(response.isSuccessful){
                    val c: OutputPost = response.body()!!

                    val sharedPref: SharedPreferences = getSharedPreferences(
                        getString(R.string.preference_file_key),
                        Context.MODE_PRIVATE
                    )
                    with(sharedPref.edit()){
                        putBoolean(getString(R.string.session_status), true)
                        putString(getString(R.string.person_id), c.id_pessoa.toString())
                        commit()
                    }


                    val i = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(i)
                    finish()

                    Toast.makeText(applicationContext, c.email, Toast.LENGTH_SHORT).show()
                    Log.d("aaa", c.email)
                    Log.d("aaa", "ccc")
                }
            }

            override fun onFailure(call: Call<OutputPost>, t: Throwable) {
                Log.d("aaa", "ddd")
                Toast.makeText(this@LoginActivity, "Wrong credentials", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
package pt.atp.cityalert

import android.app.Service
import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.android.material.imageview.ShapeableImageView
import org.w3c.dom.Text
import pt.atp.cityalert.api.EndPoints
import pt.atp.cityalert.api.Ocorrencia
import pt.atp.cityalert.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewSpecificOccurrenceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_specific_occurrence)

        val occurrence_description = findViewById<TextView>(R.id.description_txt)
        val occurrence_img_view = findViewById<ShapeableImageView>(R.id.occurrence_img)
        val lat_value = findViewById<TextView>(R.id.lat_value)
        val lng_value = findViewById<TextView>(R.id.lng_value)

        // Toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.view_specific_occurrence_toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_go_back_foreground)
        toolbar.title = "Ocorrência"
        toolbar.setTitleTextColor(resources.getColor(R.color.black, null))
        toolbar.setNavigationOnClickListener{
            finish()
        }



        val occurrenceId = intent.getStringExtra("occurrenceId")


        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getIdOcorrencia(occurrenceId.toInt())

        call.enqueue(object : Callback<Ocorrencia>{
            override fun onResponse(call: Call<Ocorrencia>, response: Response<Ocorrencia>) {
                if(response.isSuccessful){
                    val c: Ocorrencia = response.body()!!

                    val pessoaId = c.pessoa_id
                    val ocorrenciaImgBase64String = c.foto
                    val latitude = c.latitude
                    val longitude = c.longitude
                    val imageBytes = Base64.decode(ocorrenciaImgBase64String, Base64.DEFAULT)
                    val decodedImg = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    occurrence_img_view.setImageBitmap(decodedImg)
                    occurrence_description.append(c.descricao)
                    lat_value.text = latitude
                    lng_value.text = longitude

                    val sharedPref: SharedPreferences = getSharedPreferences(
                            getString(R.string.preference_file_key),
                            Context.MODE_PRIVATE
                    )

                    val pessoaLogada = sharedPref.getInt(getString(R.string.person_id), 0)

                    if(pessoaId == pessoaLogada){
                        toolbar.inflateMenu(R.menu.toolbar_view_specific_occurrence_menu)
                        toolbar.setOnMenuItemClickListener {
                            onOptionsItemSelected(it)
                        }
                        Log.d("aa", "Esta ocorrencia é tua.")
                    } else{
                        Log.d("aa", "Esta ocorrencia não é tua.")
                    }
                }
            }

            override fun onFailure(call: Call<Ocorrencia>, t: Throwable) {
                Log.d("aa", t.message)
                Toast.makeText(this@ViewSpecificOccurrenceActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val occurrenceId = intent.getStringExtra("occurrenceId")
        val occurrence_title = findViewById<EditText>(R.id.description_txt)
        val occurrence_save_edit = findViewById<Button>(R.id.save_edit_occurrence_btn)
        return when (item.itemId){
            R.id.start_edit_occurrence_btn -> {
                if(!occurrence_title.isEnabled){
                    occurrence_title.isEnabled = true
                    occurrence_save_edit.visibility = View.VISIBLE
                }else{
                    occurrence_title.isEnabled = false
                    occurrence_save_edit.visibility = View.INVISIBLE
                }

                true
            }
            R.id.delete_note_btn -> {
                //noteViewModel.deleteByid(id)
                finish()

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
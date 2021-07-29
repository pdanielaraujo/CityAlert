package pt.atp.cityalert

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.gms.maps.model.Marker
import com.google.android.material.imageview.ShapeableImageView
import pt.atp.cityalert.api.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File

private const val FILE_NAME = "foto.jpg"
private const val REQUEST_IMAGE_CAPTURE = 1
private lateinit var photoFile: File

class ViewSpecificOccurrenceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_specific_occurrence)

        val lat_value = findViewById<TextView>(R.id.lat_value)
        val lng_value = findViewById<TextView>(R.id.lng_value)
        val occurrence_img_view = findViewById<ShapeableImageView>(R.id.occurrence_img)
        val occurrence_description = findViewById<TextView>(R.id.description_txt)
        val occurrence_save_edit = findViewById<Button>(R.id.save_edit_occurrence_btn)
        val occurrence_new_photo = findViewById<ImageButton>(R.id.add_photo)

        // Toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.view_specific_occurrence_toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_go_back_foreground)
        toolbar.title = "Ocorrência"
        toolbar.setTitleTextColor(resources.getColor(R.color.black, null))
        toolbar.setNavigationOnClickListener{
            finish()
        }

        occurrence_new_photo.setOnClickListener{
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)

            //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile)
            val fileProvider = FileProvider.getUriForFile(this@ViewSpecificOccurrenceActivity, "pt.atp.cityalert.fileprovider", photoFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if(takePictureIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } else{
                Toast.makeText(this@ViewSpecificOccurrenceActivity, "Impossível abrir a câmara.", Toast.LENGTH_SHORT).show()
            }
        }

        val occurrenceId = intent.getStringExtra("occurrenceId")


        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getIdOcorrencia(occurrenceId.toInt())

        call.enqueue(object : Callback<Ocorrencia> {
            override fun onResponse(call: Call<Ocorrencia>, response: Response<Ocorrencia>) {
                if (response.isSuccessful) {
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

                    if (pessoaId == pessoaLogada) {
                        toolbar.inflateMenu(R.menu.toolbar_view_specific_occurrence_menu)
                        toolbar.setOnMenuItemClickListener {
                            onOptionsItemSelected(it)
                        }
                        Log.d("aa", "Esta ocorrencia é tua.")
                    } else {
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

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val occurrence_img_view = findViewById<ShapeableImageView>(R.id.occurrence_img)
        val occurrence_description = findViewById<TextView>(R.id.description_txt)
        val occurrence_save_edit = findViewById<Button>(R.id.save_edit_occurrence_btn)
        val occurrenceId = intent.getStringExtra("occurrenceId")
        val newDescription = occurrence_description.text
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            val imageBitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
            occurrence_img_view.setImageBitmap(imageBitmap)

            val outputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val byteArray = outputStream.toByteArray()
            val fotoString64 = Base64.encodeToString(byteArray, Base64.DEFAULT)

            occurrence_save_edit.setOnClickListener{

                Log.d("aa", newDescription.toString())
                Log.d("aa", occurrenceId)
                val request = ServiceBuilder.buildService(EndPoints::class.java)
                val call = request.editOcorrencia(occurrenceId.toInt(), newDescription.toString(), fotoString64)

                call.enqueue(object : Callback<OcorrenciaUpdate> {
                    override fun onResponse(call: Call<OcorrenciaUpdate>, response: Response<OcorrenciaUpdate>) {
                        val c: OcorrenciaUpdate = response.body()!!

                        if(!c.status) {
                            Toast.makeText(this@ViewSpecificOccurrenceActivity, c.MSG, Toast.LENGTH_SHORT).show()
                            finish()
                        } else{
                            Toast.makeText(this@ViewSpecificOccurrenceActivity, c.MSG, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }

                    override fun onFailure(call: Call<OcorrenciaUpdate>, t: Throwable) {
                        Log.d("aa", t.message)
                        Toast.makeText(this@ViewSpecificOccurrenceActivity, "aa" + "${t.message}", Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }
    }

    fun deleteOccurrence(id: String?){
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.deleteOcorrencia(id!!.toInt())

        call.enqueue(object : Callback<OcorrenciaDelete> {
            override fun onResponse(call: Call<OcorrenciaDelete>, response: Response<OcorrenciaDelete>) {
                    val c: OcorrenciaDelete = response.body()!!

                    if (!c.status) {
                        Toast.makeText(this@ViewSpecificOccurrenceActivity, c.MSG, Toast.LENGTH_SHORT).show()
                        finish()
                    }else {
                        Toast.makeText(this@ViewSpecificOccurrenceActivity, c.MSG, Toast.LENGTH_SHORT).show()
                        finish()
                    }
            }

            override fun onFailure(call: Call<OcorrenciaDelete>, t: Throwable) {
                Log.d("aa", t.message)
                Toast.makeText(this@ViewSpecificOccurrenceActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val occurrenceId = intent.getStringExtra("occurrenceId")
        val occurrence_title = findViewById<EditText>(R.id.description_txt)
        val occurrence_save_edit = findViewById<Button>(R.id.save_edit_occurrence_btn)
        val occurrence_new_photo = findViewById<ImageButton>(R.id.add_photo)
        return when (item.itemId){
            R.id.start_edit_occurrence_btn -> {
                if (!occurrence_title.isEnabled) {
                    occurrence_title.isEnabled = true
                    occurrence_save_edit.visibility = View.VISIBLE
                    occurrence_new_photo.visibility = View.VISIBLE
                } else {
                    occurrence_title.isEnabled = false
                    occurrence_save_edit.visibility = View.INVISIBLE
                    occurrence_new_photo.visibility = View.INVISIBLE
                }

                true
            }
            R.id.delete_note_btn -> {
                deleteOccurrence(occurrenceId)
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
package pt.atp.cityalert

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.FileProvider
import com.google.android.material.imageview.ShapeableImageView
import pt.atp.cityalert.api.EndPoints
import pt.atp.cityalert.api.OcorrenciaInsert
import pt.atp.cityalert.api.OcorrenciaUpdate
import pt.atp.cityalert.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File

private const val FILE_NAME = "foto.jpg"
private const val REQUEST_IMAGE_CAPTURE = 1

class AddOccurrenceActivity : AppCompatActivity() {

    private lateinit var title: EditText
    private lateinit var lat_value: EditText
    private lateinit var lng_value: EditText
    private lateinit var add_photo_btn: ImageButton
    private lateinit var insert_btn: Button
    private lateinit var image_occurrence: ImageView
    private lateinit var photoFile: File
    private var num_categoria: Int = 0
    private lateinit var fotoString64: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_occurrence)

        val lat_val = intent.getStringExtra("lat")
        val lng_val = intent.getStringExtra("lng")

        lat_value = findViewById(R.id.new_lat_value)
        lng_value = findViewById(R.id.new_lng_value)
        add_photo_btn = findViewById(R.id.add_photo_btn)
        image_occurrence = findViewById(R.id.add_occurrence_img)
        insert_btn = findViewById(R.id.insert_occurrence_btn)
        title = findViewById(R.id.new_description_txt)

        lat_value.append(lat_val)
        lng_value.append(lng_val)
        fotoString64 = ""
        val descricao_val = title.text

        // Toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.add_occurrence_toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_go_back_foreground)
        toolbar.title = "Adicionar Ocorrência"
        toolbar.setTitleTextColor(resources.getColor(R.color.black, null))
        toolbar.setNavigationOnClickListener{
            finish()
        }

        add_photo_btn.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)

            //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile)
            val fileProvider = FileProvider.getUriForFile(this@AddOccurrenceActivity, "pt.atp.cityalert.fileprovider", photoFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if(takePictureIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } else{
                Toast.makeText(this@AddOccurrenceActivity, "Impossível abrir a câmara.", Toast.LENGTH_SHORT).show()
            }
        }

        val sharedPref: SharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
        )

        val pessoaLogada = sharedPref.getInt(getString(R.string.person_id), 0)

        insert_btn.setOnClickListener {
            if(fotoString64.isEmpty()){
                Toast.makeText(this@AddOccurrenceActivity, R.string.erro_insert_photo, Toast.LENGTH_SHORT).show()
            } else{
                val request = ServiceBuilder.buildService(EndPoints::class.java)
                val call = request.newReport(title.text.toString(), fotoString64, lat_val.toDouble(), lng_val.toDouble(), pessoaLogada, num_categoria)
                call.enqueue(object : Callback<OcorrenciaInsert> {
                    override fun onResponse(call: Call<OcorrenciaInsert>, response: Response<OcorrenciaInsert>) {
                        val c: OcorrenciaInsert = response.body()!!

                        if(!c.status) {
                            Toast.makeText(this@AddOccurrenceActivity, c.MSG, Toast.LENGTH_SHORT).show()
                            finish()
                        } else{
                            Toast.makeText(this@AddOccurrenceActivity, c.MSG, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }

                    override fun onFailure(call: Call<OcorrenciaInsert>, t: Throwable) {
                        Toast.makeText(this@AddOccurrenceActivity, "aa" + "${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            val imageBitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
            image_occurrence.setImageBitmap(imageBitmap)

            val outputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val byteArray = outputStream.toByteArray()
            fotoString64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radio_buraco ->
                    if (checked) {
                        // Valor da categoria = 1
                        num_categoria = 1
                    }
                R.id.radio_queda_arvore ->
                    if (checked) {
                        // Valor da categoria = 2
                        num_categoria = 2
                    }
                R.id.radio_queda_poste ->
                    if (checked) {
                        // Valor da categoria = 3
                        num_categoria = 3
                    }
            }
        }
    }


    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }
}
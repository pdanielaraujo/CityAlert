package pt.atp.cityalert.api

import retrofit2.Call
import retrofit2.http.*

interface EndPoints {

    // Buscar todas as ocorrencias
    @GET("ocorrencias")
    fun getOcorrencias(): Call<List<Ocorrencia>>

    // Buscar uma pessoa pelo email
    @GET("/pessoas/{email}")
    fun getPersonByEmail(@Path("email") email: String): Call<Pessoa>

    @FormUrlEncoded
    @POST("login")
    fun postEmail(@Field("email") email: String, @Field("password") password: String): Call<OutputPost>

    @FormUrlEncoded
    @POST("ocorrencia")
    fun getIdOcorrencia(@Field("id") id: Int): Call<Ocorrencia>

    @FormUrlEncoded
    @POST("ocorrencia/update")
    fun editOcorrencia(
            @Field("id") id: Int,
            @Field("descricao") descricao: String,
            @Field("foto") foto: String
    ): Call<OcorrenciaUpdate>

    @FormUrlEncoded
    @POST("ocorrencia/delete")
    fun deleteOcorrencia(@Field("id") id: Int): Call<OcorrenciaDelete>
}
package pt.atp.cityalert.api

import retrofit2.Call
import retrofit2.http.*

interface EndPoints {

    // Buscar todos as pessoas
    @GET("/pessoas/")
    fun getPersons(): Call<List<Pessoa>>

    // Buscar uma pessoa pelo email
    @GET("/pessoas/{email}")
    fun getPersonByEmail(@Path("email") email: String): Call<Pessoa>

    @FormUrlEncoded
    @POST("login")
    fun postEmail(@Field("email") email: String, @Field("password") password: String): Call<OutputPost>
}
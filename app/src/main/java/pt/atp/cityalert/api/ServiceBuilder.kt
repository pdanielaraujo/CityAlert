package pt.atp.cityalert.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object ServiceBuilder {

    private val client = OkHttpClient.Builder().build()

    private val gson = GsonBuilder().setLenient().create()

    private val retrofit = Retrofit.Builder()
            .baseUrl("https://cityalertapp.000webhostapp.com/slim/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

    fun<T> buildService(service: Class<T>): T{
        return retrofit.create(service)
    }
}
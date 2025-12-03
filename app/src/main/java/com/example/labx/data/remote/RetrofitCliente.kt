package com.example.labx.data.remote
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {

    // URL  la Abase dePI
    private const val URL_BASE = "https://api-dfs2-dm-production.up.railway.app/"

    // Interceptor para ver peticiones en Logcat
    private val interceptorLog = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente HTTP con timeouts
    private val clienteHttp = OkHttpClient.Builder()
        .addInterceptor(interceptorLog)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Instancia de Retrofit
    val instancia: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // crear servicios
    fun <T> crearServicio(servicioClase: Class<T>): T {
        return instancia.create(servicioClase)
    }
}
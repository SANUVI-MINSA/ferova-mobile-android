package pe.edu.upc.ferovafamily.data.remote

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import pe.edu.upc.ferovafamily.data.local.TokenManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Test:       https://backend-ferova-test.up.railway.app/
// Producción: https://backend-ferova-production-187b.up.railway.app/
private const val BASE_URL = "https://backend-ferova-test.up.railway.app/"

/**
 * Interceptor que agrega el JWT Bearer Token a cada petición autenticada.
 */
class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = tokenManager.token

        val newRequest = if (token != null) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }
        return chain.proceed(newRequest)
    }
}

/**
 * Singleton Retrofit. Llama a FerovaApiClient.create(TuService::class.java, context).
 */
object FerovaApiClient {

    private var retrofit: Retrofit? = null

    private fun getRetrofit(context: Context): Retrofit {
        if (retrofit == null) {
            val tokenManager = TokenManager.getInstance(context)

            val okHttp = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(tokenManager))
                .connectTimeout(30L, TimeUnit.SECONDS)
                .readTimeout(30L, TimeUnit.SECONDS)
                .writeTimeout(30L, TimeUnit.SECONDS)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttp)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    fun <T> create(serviceClass: Class<T>, context: Context): T =
        getRetrofit(context).create(serviceClass)

    /** Forzar recreación del cliente (útil al cambiar de entorno) */
    fun reset() { retrofit = null }
}

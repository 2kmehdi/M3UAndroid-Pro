@file:Suppress("unused")

package com.m3u.data.api

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.m3u.data.Certs
import com.m3u.data.SSLs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class OkhttpClient(val chucker: Boolean)

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ProviderOkhttpClient

@Module
@InstallIn(SingletonComponent::class)
internal object ApiModule {
    @Provides
    @Singleton
    @OkhttpClient(true)
    fun provideChuckerOkhttpClient(
        @ApplicationContext context: Context,
        @OkhttpClient(false) okhttpClient: OkHttpClient
    ): OkHttpClient {
        return okhttpClient
            .newBuilder()
            .addInterceptor(
                ChuckerInterceptor.Builder(context)
                    .maxContentLength(10240)
                    .build()
            )
            .sslSocketFactory(SSLs.TLSTrustAll.socketFactory, Certs.TrustAll)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    @Provides
    @Singleton
    @OkhttpClient(false)
    fun provideOkhttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(45, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .authenticator(Authenticator.JAVA_NET_AUTHENTICATOR)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                if (original.header("User-Agent") == null) {
                    requestBuilder.header("User-Agent", "IPTVSmartersPro/3.1.5.1 (Linux; Android 14) ExoPlayerLib/2.19.1")
                }
                if (original.header("Accept") == null) {
                    requestBuilder.header("Accept", "*/*")
                }
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .sslSocketFactory(SSLs.TLSTrustAll.socketFactory, Certs.TrustAll)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    @Provides
    @Singleton
    @ProviderOkhttpClient
    fun provideProviderOkhttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .sslSocketFactory(SSLs.TLSTrustAll.socketFactory, Certs.TrustAll)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitBuilder(
        @OkhttpClient(true) okHttpClient: OkHttpClient
    ): Retrofit.Builder {
        val json = Json {
            ignoreUnknownKeys = true
            prettyPrint = false
        }
        val mediaType = "application/json".toMediaType()
        return Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory(mediaType))
            .client(okHttpClient)
    }
}

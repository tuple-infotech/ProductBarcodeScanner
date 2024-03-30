package com.tupleinfotech.productbarcodescanner.di

import android.annotation.SuppressLint
import com.tupleinfotech.productbarcodescanner.data.api.Api
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    //TODO: ASK AND CHANGE TIMEOUT
    val timeout : Long = 60
    @Singleton
    @Provides
    fun providesRetrofit()  :   Retrofit{
        val interceptor     =   HttpLoggingInterceptor()
        interceptor.level   =   HttpLoggingInterceptor.Level.BODY

        val httpbuilder     =   OkHttpClient.Builder()
//                                    .addInterceptor(OAuthInterceptor("---ACCESS---TOKEN---"))

        httpbuilder
            .addInterceptor(interceptor)
//            .readTimeout(timeout, TimeUnit.SECONDS)
//            .callTimeout(timeout, TimeUnit.SECONDS)
//            .connectTimeout(timeout, TimeUnit.SECONDS)
//            .writeTimeout(timeout, TimeUnit.SECONDS)
        val mClient         =   httpbuilder.build()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://150.129.105.34/api/v1/")
            .client(mClient)
            .build()
    }
    @SuppressLint("SuspiciousIndentation")
    @Singleton
    @Provides
    fun provideHTTPLoggingInterceptor()     :    HttpLoggingInterceptor {
        val interceptor     =   HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return interceptor
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(loggingInterceptor  : HttpLoggingInterceptor)   : OkHttpClient {
        return OkHttpClient
            .Builder()
//            .callTimeout(timeout, TimeUnit.SECONDS)
//            .connectTimeout(timeout,TimeUnit.SECONDS)
//            .writeTimeout(timeout,TimeUnit.SECONDS)
//            .readTimeout(timeout, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun providesCustomerAPI(retrofit    : Retrofit) : Api {
        return retrofit.create(Api::class.java)
    }

    class OAuthInterceptor(private var acceessToken : String)    : Interceptor {

        override fun intercept(chain    : Interceptor.Chain)    : okhttp3.Response {
            acceessToken    =   ""
            var request     =   chain.request()
            request         =   request.newBuilder()
                                    .header("Authorization", "Bearer $acceessToken")
                                    .build()

            return chain.proceed(request)
        }
    }
}
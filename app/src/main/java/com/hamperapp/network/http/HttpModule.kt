package com.hamperapp.network.http

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hamperapp.BuildConfig
import com.hamperapp.CommonApi
import com.hamperapp.HamperApplication
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


object Http {

	const val Header_ContentType_ApplicationJson = "Content-Type: application/json"
	const val Header_Accept_ApplicationJson = "Accept: application/json"

	val provideCommonApi by lazy<CommonApi> {

		val commonApiDomain = HamperApplication.instance.configManager.getCommonApiBaseUrl()

		Retrofit
			.Builder()
			.baseUrl(commonApiDomain)
			.client(okHttpClient)
			.addConverterFactory(provideGsonConverterFactory)
			.build()
			.create(CommonApi::class.java)

	}

	val provideGson by lazy<Gson> {

		GsonBuilder()
			.setPrettyPrinting()
			.create()

	}

	val okHttpClient by lazy<OkHttpClient> {

		val okHttpClientBuilder: OkHttpClient.Builder = if (BuildConfig.DEBUG) {

			provideUnsafeOkHttpBuilder()
				.addInterceptor(provideHttpLoggingInterceptor())

		} else {

			OkHttpClient.Builder()

		}

		okHttpClientBuilder
			.connectTimeout(10, TimeUnit.SECONDS)
			.readTimeout(30, TimeUnit.SECONDS)
			.writeTimeout(30, TimeUnit.SECONDS)
			.callTimeout(70, TimeUnit.SECONDS)

		okHttpClientBuilder.build()

	}

	private val provideGsonConverterFactory by lazy<GsonConverterFactory> { GsonConverterFactory.create(provideGson) }

	private fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {

		return HttpLoggingInterceptor().apply {
			level = HttpLoggingInterceptor.Level.BODY
		}

	}

	/**
	 * Provides unsafe ok http builder, used in debug
	 */
	private fun provideUnsafeOkHttpBuilder(): OkHttpClient.Builder {
		try {

			val trustAllManager = object : X509TrustManager {
				@SuppressLint("TrustAllX509TrustManager")
				@Throws(CertificateException::class)

				override fun checkClientTrusted(
					chain: Array<X509Certificate>,
					authType: String
				) {
				}

				@SuppressLint("TrustAllX509TrustManager")
				@Throws(CertificateException::class)
				override fun checkServerTrusted(
					chain: Array<X509Certificate>,
					authType: String
				) {
				}

				override fun getAcceptedIssuers(): Array<X509Certificate> {
					return emptyArray()
				}
			}

			// Create a trust manager that does not validate certificate chains
			val trustAllCerts = arrayOf<TrustManager>(trustAllManager)

			// Install the all-trusting trust manager
			val sslContext = SSLContext.getInstance("TLSv1.2")

			sslContext.init(null, trustAllCerts, SecureRandom())

			// Create an ssl socket factory with our all-trusting manager
			val sslSocketFactory = sslContext.socketFactory

			val okHttpBuilder = OkHttpClient.Builder()

			okHttpBuilder.sslSocketFactory(sslSocketFactory, trustAllManager)

			return okHttpBuilder

		} catch (e: Exception) {
			throw RuntimeException(e)
		}

	}


}
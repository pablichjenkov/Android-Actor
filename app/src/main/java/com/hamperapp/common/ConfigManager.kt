package com.hamperapp.common

import android.app.Application
import android.content.res.AssetManager
import com.hamperapp.network.http.Http
import com.hamperapp.network.http.genericType
import java.io.InputStreamReader

class ConfigManager(application: Application) {

	private val assetManager: AssetManager = application.assets

	private var config: Config? = null

	fun getAppCenterClientId(): String {

		return config?.appCenterClientId ?: run {

			val inputStream = assetManager.open("config.json")

			val configCopy =  Http.provideGson.fromJson<Config>(
				InputStreamReader(inputStream, "UTF-8"),
				genericType<Config>()
			)

			config = configCopy

			configCopy.appCenterClientId

		}

	}

}
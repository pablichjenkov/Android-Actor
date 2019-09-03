package com.hamperapp

import android.content.Context
import com.esotericsoftware.minlog.Log
import io.paperdb.Paper

class StorageManager(val appContext: Context) {

	private val AppStorageDir = "Hamperapp-Storage"

	init {

		Paper.init(appContext)

		val logLevel = if (BuildConfig.DEBUG) Log.LEVEL_NONE else Log.LEVEL_DEBUG

		Paper.setLogLevel(logLevel)

	}

	fun <T> write(docName: String, obj: T) {

		Paper.book(AppStorageDir).write(docName, obj)

	}

	fun <T> read(docName: String): T {

		return Paper.book(AppStorageDir).read(docName, null) as T

	}

	fun delete(docName: String) {

		Paper.book(AppStorageDir).delete(docName)

	}

	fun destroy() {

		Paper.book(AppStorageDir).destroy()

	}

}
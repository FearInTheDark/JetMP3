package com.vincent.jetmp3.utils

import android.util.Log

fun doLog(context: Any, message: String) = Log.d(
	context.javaClass.name,
	message
)
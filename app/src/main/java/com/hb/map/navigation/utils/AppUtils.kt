package com.hb.map.navigation.utils

import android.content.Context
import android.text.TextUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.Charset

object AppUtils {

    @JvmStatic
    @Throws(IOException::class)
    fun loadStringFromAssets(context: Context, fileName: String): String {
        if (TextUtils.isEmpty(fileName)) {
            throw NullPointerException("No GeoJSON File Name passed in.")
        }
        val `is` = context.assets.open(fileName)
        val rd = BufferedReader(InputStreamReader(`is`, Charset.forName("UTF-8")) as Reader?)
        return readAll(rd)
    }

    @Throws(IOException::class)
    private fun readAll(rd: Reader): String {
        val sb = StringBuilder()
        rd.forEachLine {
            sb.append(it)
        }
        return sb.toString()
    }



}

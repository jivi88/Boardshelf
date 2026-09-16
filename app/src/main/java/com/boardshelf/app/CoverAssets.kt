package com.boardshelf.app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayInputStream

object CoverAssets {
    private val encoded = arrayOf(
        "PLACEHOLDER"
    )
    private val cache = arrayOfNulls<Bitmap>(30)
    fun get(index: Int): Bitmap {
        cache[index]?.let { return it }
        val bytes = Base64.decode(encoded[index], Base64.DEFAULT)
        return BitmapFactory.decodeStream(ByteArrayInputStream(bytes)).also { cache[index] = it }
    }
}

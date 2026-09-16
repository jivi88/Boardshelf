package com.boardshelf.app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Base64
import java.io.ByteArrayInputStream

object CoverSprite {
    private val source: Bitmap? by lazy {
        runCatching {
            val data = CoverChunk1.DATA + CoverChunk2.DATA + CoverChunk3.DATA + CoverChunk4.DATA + CoverChunk5.DATA
            BitmapFactory.decodeStream(ByteArrayInputStream(Base64.decode(data, Base64.DEFAULT)))
        }.getOrNull()
    }

    fun drawCover(canvas: Canvas, index: Int, dst: Rect) {
        val bitmap = source
        if (bitmap != null && bitmap.width >= 400 && bitmap.height >= 480) {
            val col = index % 10
            val row = index / 10
            val src = Rect(col * 40, row * 160, (col + 1) * 40, (row + 1) * 160)
            canvas.drawBitmap(bitmap, src, dst, Paint(Paint.ANTI_ALIAS_FLAG).apply { isFilterBitmap = true })
        } else {
            val fallback = CoverAssets1.get(index % 3)
            canvas.drawBitmap(fallback, null, dst, Paint(Paint.ANTI_ALIAS_FLAG).apply { isFilterBitmap = true })
        }
    }
}

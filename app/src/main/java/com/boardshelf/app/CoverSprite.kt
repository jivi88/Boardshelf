package com.boardshelf.app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.util.Base64
import java.io.ByteArrayInputStream

object CoverSprite {
    private val source: Bitmap by lazy {
        val data = CoverChunk1.DATA + CoverChunk2.DATA + CoverChunk3.DATA + CoverChunk4.DATA + CoverChunk5.DATA
        BitmapFactory.decodeStream(ByteArrayInputStream(Base64.decode(data, Base64.DEFAULT)))
    }

    fun drawCover(canvas: android.graphics.Canvas, index: Int, dst: Rect) {
        val src = Rect((index % 10) * 40, (index / 10) * 160, (index % 10 + 1) * 40, (index / 10 + 1) * 160)
        canvas.drawBitmap(source, src, dst, android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { isFilterBitmap = true })
    }
}

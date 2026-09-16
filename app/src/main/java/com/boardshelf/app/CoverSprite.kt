package com.boardshelf.app

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

object CoverSprite {
    fun drawCover(canvas: Canvas, index: Int, dst: Rect) {
        // Reference-photo crops are bundled locally, so the APK works offline.
        // Use the available reference crops as the visual cover layer.
        val bitmap = CoverAssets1.get(index % 3)
        canvas.drawBitmap(bitmap, null, dst, Paint(Paint.ANTI_ALIAS_FLAG).apply { isFilterBitmap = true })
    }
}

package com.boardshelf.app

import android.graphics.Bitmap

object CoverAssets {
    fun get(index: Int): Bitmap = when (index) {
        in 0..9 -> CoverAssets1.get(index)
        in 10..19 -> CoverAssets2.get(index - 10)
        else -> CoverAssets3.get(index - 20)
    }
}

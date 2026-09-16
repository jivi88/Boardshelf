package com.boardshelf.app

import android.app.Activity
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(23, 15, 12)
        window.navigationBarColor = Color.rgb(18, 13, 11)
        setContentView(BoardShelfView())
    }

    private inner class BoardShelfView : View(this@MainActivity) {
        private data class Game(
            val name: String,
            val year: Int,
            val players: String,
            val accent: Int,
            val motif: Int
        )

        private val games = listOf(
            Game("Terraforming Mars", 2016, "1–5", Color.rgb(188, 77, 37), 0),
            Game("Gloomhaven", 2017, "1–4", Color.rgb(45, 39, 36), 1),
            Game("Scythe", 2016, "1–5", Color.rgb(113, 111, 108), 2),
            Game("Wingspan", 2019, "1–5", Color.rgb(34, 103, 116), 3),
            Game("Azul", 2017, "2–4", Color.rgb(65, 118, 133), 4),
            Game("Root", 2018, "2–4", Color.rgb(76, 79, 48), 5),
            Game("7 Wonders", 2010, "3–7", Color.rgb(25, 50, 91), 6),
            Game("Catan", 1995, "3–4", Color.rgb(178, 58, 40), 7),
            Game("Ticket to Ride", 2004, "2–5", Color.rgb(93, 91, 82), 8),
            Game("Carcassonne", 2000, "2–5", Color.rgb(29, 84, 116), 9),
            Game("The Lord of the Rings", 2010, "2–4", Color.rgb(55, 39, 27), 10),
            Game("Pandemic", 2008, "2–4", Color.rgb(26, 65, 104), 11),
            Game("Brass Birmingham", 2018, "2–4", Color.rgb(84, 83, 77), 12),
            Game("El Grande", 1995, "2–5", Color.rgb(30, 62, 92), 13),
            Game("Splendor", 2014, "2–4", Color.rgb(39, 45, 53), 14),
            Game("7 Wonders Duel", 2015, "2", Color.rgb(69, 69, 82), 15),
            Game("Tzolk'in", 2012, "2–4", Color.rgb(25, 90, 105), 16),
            Game("Dominion", 2008, "2–4", Color.rgb(28, 63, 95), 17),
            Game("Viticulture", 2013, "1–6", Color.rgb(116, 141, 143), 18),
            Game("Ark Nova", 2021, "1–4", Color.rgb(30, 126, 99), 19),
            Game("Spirit Island", 2017, "1–4", Color.rgb(29, 105, 103), 20),
            Game("Heat", 2022, "1–6", Color.rgb(179, 93, 38), 21),
            Game("The Crew", 2019, "2–5", Color.rgb(20, 55, 91), 22),
            Game("Patchwork", 2014, "2", Color.rgb(143, 76, 42), 23),
            Game("Lost Ruins of Arnak", 2020, "1–4", Color.rgb(46, 91, 54), 24),
            Game("Everdell", 2018, "1–4", Color.rgb(112, 91, 46), 25),
            Game("Evergreen", 2021, "1–4", Color.rgb(26, 91, 60), 26),
            Game("Innovation", 2010, "2–4", Color.rgb(115, 68, 40), 27),
            Game("Galaxy Trucker", 2007, "2–6", Color.rgb(28, 47, 78), 28),
            Game("Twilight Imperium", 1997, "3–6", Color.rgb(37, 47, 72), 29)
        )

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val wood = Paint(Paint.ANTI_ALIAS_FLAG)
        private var selectedTab = 0
        private var searchOpen = false
        private var scroll = 0f
        private var downY = 0f
        private var lastY = 0f
        private var moved = false

        init {
            setBackgroundColor(Color.rgb(28, 18, 14))
            textPaint.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
            paint.setShadowLayer(12f, 0f, 7f, 0xAA000000.toInt())
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val w = width.toFloat()
            val h = height.toFloat()
            drawBackground(canvas, w, h)
            drawTopBar(canvas, w)

            canvas.save()
            canvas.clipRect(0f, 142f, w, h - 70f)
            canvas.translate(0f, -scroll)
            val startY = 158f
            repeat(3) { row ->
                drawShelf(canvas, startY + row * 304f, row)
            }
            canvas.restore()

            drawBottomBar(canvas, w, h)
        }

        private fun drawBackground(c: Canvas, w: Float, h: Float) {
            val gradient = LinearGradient(0f, 0f, w, h, Color.rgb(27, 17, 13), Color.rgb(56, 31, 19), Shader.TileMode.CLAMP)
            paint.shader = gradient
            paint.style = Paint.Style.FILL
            c.drawRect(0f, 0f, w, h, paint)
            paint.shader = null

            // Subtle vertical wood grain.
            paint.strokeWidth = 1f
            for (x in 0..w.toInt() step 28) {
                paint.color = if (x % 56 == 0) 0x18000000 else 0x10000000
                c.drawLine(x.toFloat(), 0f, x.toFloat() + 7f, h, paint)
            }
            for (y in 18..h.toInt() step 54) {
                paint.color = 0x10000000
                c.drawLine(0f, y.toFloat(), w, y.toFloat() + 4f, paint)
            }
        }

        private fun drawTopBar(c: Canvas, w: Float) {
            paint.color = 0xE817110E.toInt()
            c.drawRect(0f, 0f, w, 142f, paint)
            paint.color = 0x553D2419
            c.drawRect(0f, 112f, w, 142f, paint)

            drawMenuIcon(c, 28f, 52f)
            textPaint.color = Color.rgb(244, 235, 224)
            textPaint.textSize = 24f
            textPaint.typeface = Typeface.create("sans-serif", Typeface.BOLD)
            c.drawText(if (searchOpen) "Поиск" else "Моя коллекция", 88f, 59f, textPaint)

            if (searchOpen) {
                textPaint.color = 0xFFBBAA9B.toInt()
                textPaint.textSize = 14f
                c.drawText("Нажмите на игру, чтобы открыть карточку", 88f, 83f, textPaint)
            }
            drawSearchIcon(c, w - 82f, 53f)
            drawDots(c, w - 28f, 52f)

            val labels = arrayOf("Все игры", "Сыгранное", "Стеллаж", "Избранное", "Профиль")
            val centers = floatArrayOf(w * .10f, w * .30f, w * .50f, w * .70f, w * .90f)
            for (i in labels.indices) {
                val active = i == selectedTab
                val color = if (active) Color.rgb(255, 194, 45) else Color.rgb(236, 228, 218)
                drawTabIcon(c, centers[i], 103f, i, color)
                textPaint.color = color
                textPaint.textSize = 13f
                textPaint.typeface = Typeface.create("sans-serif", if (active) Typeface.BOLD else Typeface.NORMAL)
                textPaint.textAlign = Paint.Align.CENTER
                c.drawText(labels[i], centers[i], 132f, textPaint)
            }
            textPaint.textAlign = Paint.Align.LEFT
            paint.color = Color.rgb(255, 194, 45)
            c.drawRect(centers[selectedTab] - 62f, 139f, centers[selectedTab] + 62f, 143f, paint)
        }

        private fun drawBottomBar(c: Canvas, w: Float, h: Float) {
            paint.color = 0xF317120F.toInt()
            c.drawRect(0f, h - 70f, w, h, paint)
            paint.color = 0x553F2A20
            c.drawRect(0f, h - 71f, w, h - 69f, paint)
            val center = w * .17f
            drawCollectionIcon(c, center, h - 42f, Color.rgb(255, 194, 45))
            textPaint.color = Color.rgb(255, 194, 45)
            textPaint.textSize = 13f
            textPaint.typeface = Typeface.DEFAULT_BOLD
            textPaint.textAlign = Paint.Align.CENTER
            c.drawText("Коллекция", center, h - 14f, textPaint)
            drawSliders(c, w * .84f, h - 36f)
            textPaint.textAlign = Paint.Align.LEFT
        }

        private fun drawShelf(c: Canvas, y: Float, row: Int) {
            val w = width.toFloat()
            val shelfLeft = 18f
            val shelfRight = w - 18f
            val shelfHeight = 274f
            val boardY = y + 247f

            // Back panel.
            val back = LinearGradient(0f, y, 0f, boardY, Color.rgb(86, 48, 29), Color.rgb(45, 25, 17), Shader.TileMode.CLAMP)
            wood.shader = back
            c.drawRect(shelfLeft, y, shelfRight, boardY, wood)
            wood.shader = null

            // Shelf top and front lip.
            paint.color = Color.rgb(112, 65, 37)
            c.drawRect(shelfLeft, boardY, shelfRight, boardY + 26f, paint)
            paint.color = Color.rgb(53, 30, 20)
            c.drawRect(shelfLeft, boardY + 22f, shelfRight, boardY + 28f, paint)
            paint.color = 0x2A000000
            c.drawRect(shelfLeft, boardY - 7f, shelfRight, boardY + 2f, paint)

            // Brass label plate.
            val plateW = 190f
            val plateL = w / 2f - plateW / 2f
            val plate = Path().apply {
                moveTo(plateL + 13f, boardY + 5f)
                lineTo(plateL + plateW - 13f, boardY + 5f)
                lineTo(plateL + plateW, boardY + 13f)
                lineTo(plateL + plateW - 13f, boardY + 22f)
                lineTo(plateL + 13f, boardY + 22f)
                lineTo(plateL, boardY + 13f)
                close()
            }
            paint.color = Color.rgb(119, 75, 43)
            c.drawPath(plate, paint)
            paint.color = Color.rgb(221, 166, 84)
            c.drawCircle(plateL + 13f, boardY + 13f, 3.2f, paint)
            c.drawCircle(plateL + plateW - 13f, boardY + 13f, 3.2f, paint)
            textPaint.color = Color.rgb(240, 224, 203)
            textPaint.textSize = 15f
            textPaint.typeface = Typeface.create("sans-serif", Typeface.BOLD)
            textPaint.textAlign = Paint.Align.CENTER
            c.drawText("Полка ${row + 1} · 10 игр", w / 2f, boardY + 18f, textPaint)
            textPaint.textAlign = Paint.Align.LEFT

            val count = 10
            val gap = 9f
            val left = 27f
            val right = w - 27f
            val bookW = (right - left - gap * (count - 1)) / count
            for (i in 0 until count) {
                val index = row * 10 + i
                val game = games[index]
                val x = left + i * (bookW + gap)
                val top = y + 18f + (i % 3) * 2f
                val bottom = boardY - 6f - (i % 4) * 2f
                drawBook(c, game, index, x, top, bookW, bottom)
            }
        }

        private fun drawBook(c: Canvas, game: Game, index: Int, x: Float, top: Float, bw: Float, bottom: Float) {
            val radius = 4f
            paint.setShadowLayer(8f, 1f, 3f, 0x99000000.toInt())
            val cover = LinearGradient(x, top, x + bw, bottom, lighten(game.accent, .18f), darken(game.accent, .30f), Shader.TileMode.CLAMP)
            paint.shader = cover
            c.drawRoundRect(x, top, x + bw, bottom, radius, radius, paint)
            paint.shader = null
            paint.clearShadowLayer()

            // spine highlight / edge.
            paint.color = 0x3FFFFFFF
            c.drawRoundRect(x + 2f, top + 2f, x + 5f, bottom - 2f, 2f, 2f, paint)
            paint.color = 0x28000000
            c.drawRect(x + bw - 4f, top + 2f, x + bw - 2f, bottom - 2f, paint)

            drawMotif(c, game.motif, x + bw / 2f, top + (bottom - top) * .43f, min(bw, 62f), game.accent)

            // Vertical title.
            c.save()
            c.rotate(90f, x + bw / 2f, top + 92f)
            textPaint.color = Color.WHITE
            textPaint.textSize = min(15f, bw * .47f)
            textPaint.typeface = Typeface.create("sans-serif", Typeface.BOLD)
            textPaint.textAlign = Paint.Align.CENTER
            c.drawText(game.name, x + bw / 2f, top + 97f, textPaint)
            c.restore()
            textPaint.textAlign = Paint.Align.LEFT

            // Small metadata and badge.
            textPaint.color = 0xFFF7EFE5.toInt()
            textPaint.textSize = 10f
            textPaint.typeface = Typeface.create("sans-serif", Typeface.BOLD)
            c.drawText(game.year.toString(), x + 7f, bottom - 26f, textPaint)
            textPaint.textSize = 9f
            textPaint.typeface = Typeface.DEFAULT
            c.drawText(game.players, x + 7f, bottom - 11f, textPaint)
            drawMiniBadge(c, x + bw / 2f, bottom - 15f, index)
        }

        private fun drawMotif(c: Canvas, motif: Int, cx: Float, cy: Float, size: Float, accent: Int) {
            val r = size * .22f
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2f
            paint.color = 0x70FFFFFF
            when (motif % 10) {
                0 -> {
                    c.drawCircle(cx, cy, r, paint)
                    c.drawLine(cx - r * 1.4f, cy + r * 1.2f, cx + r * 1.4f, cy - r * 1.2f, paint)
                    c.drawLine(cx - r * 1.4f, cy - r * 1.2f, cx + r * 1.4f, cy + r * 1.2f, paint)
                }
                1 -> {
                    for (i in 0..3) c.drawCircle(cx + (i - 1.5f) * r * .8f, cy + ((i % 2) - .5f) * r * 1.1f, r * .45f, paint)
                    c.drawLine(cx - r * 1.5f, cy + r * 1.4f, cx + r * 1.5f, cy + r * 1.4f, paint)
                }
                2 -> {
                    c.drawRect(cx - r, cy - r * 1.4f, cx + r, cy + r * 1.4f, paint)
                    c.drawLine(cx, cy - r * 1.4f, cx, cy + r * 1.4f, paint)
                }
                3 -> {
                    val bird = Path().apply { moveTo(cx - r * 1.5f, cy); quadTo(cx - r * .4f, cy - r * 1.3f, cx, cy); quadTo(cx + r * .7f, cy - r * 1.4f, cx + r * 1.6f, cy - r * .2f); quadTo(cx + r * .6f, cy - r * .1f, cx, cy + r * .7f); quadTo(cx - r * .7f, cy + r * .1f, cx - r * 1.5f, cy); close() }
                    paint.style = Paint.Style.FILL
                    paint.color = 0x55FFFFFF
                    c.drawPath(bird, paint)
                }
                4 -> {
                    paint.style = Paint.Style.FILL
                    for (dx in -1..1) for (dy in -1..1) {
                        paint.color = if ((dx + dy + motif) % 2 == 0) 0x65FFFFFF else 0x25303030
                        c.drawRect(cx + dx * r * .9f - r * .38f, cy + dy * r * .9f - r * .38f, cx + dx * r * .9f + r * .38f, cy + dy * r * .9f + r * .38f, paint)
                    }
                }
                else -> {
                    paint.style = Paint.Style.FILL
                    paint.color = 0x45FFFFFF
                    c.drawCircle(cx, cy, r * .95f, paint)
                    paint.color = 0x55FFFFFF
                    c.drawCircle(cx - r * .7f, cy + r * .2f, r * .45f, paint)
                }
            }
            paint.style = Paint.Style.FILL
        }

        private fun drawMiniBadge(c: Canvas, cx: Float, cy: Float, index: Int) {
            paint.color = 0xAAFFFFFF.toInt()
            val p = Path().apply {
                moveTo(cx, cy - 7f)
                lineTo(cx + 6f, cy - 3f)
                lineTo(cx + 4f, cy + 5f)
                lineTo(cx, cy + 8f)
                lineTo(cx - 4f, cy + 5f)
                lineTo(cx - 6f, cy - 3f)
                close()
            }
            c.drawPath(p, paint)
            paint.color = 0x99000000.toInt()
            c.drawCircle(cx, cy + 1f, 2f + (index % 2), paint)
        }

        private fun drawMenuIcon(c: Canvas, x: Float, y: Float) {
            paint.color = Color.WHITE
            paint.strokeWidth = 3f
            paint.strokeCap = Paint.Cap.ROUND
            for (i in -1..1) c.drawLine(x, y + i * 9f, x + 31f, y + i * 9f, paint)
            paint.strokeCap = Paint.Cap.BUTT
        }

        private fun drawSearchIcon(c: Canvas, x: Float, y: Float) {
            paint.color = Color.WHITE
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 3f
            c.drawCircle(x, y - 2f, 12f, paint)
            c.drawLine(x + 9f, y + 7f, x + 19f, y + 17f, paint)
            paint.style = Paint.Style.FILL
        }

        private fun drawDots(c: Canvas, x: Float, y: Float) {
            paint.color = Color.WHITE
            c.drawCircle(x, y - 10f, 2.4f, paint)
            c.drawCircle(x, y, 2.4f, paint)
            c.drawCircle(x, y + 10f, 2.4f, paint)
        }

        private fun drawTabIcon(c: Canvas, x: Float, y: Float, tab: Int, color: Int) {
            paint.color = color
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2.2f
            when (tab) {
                0 -> for (dx in -1..1) for (dy in -1..1) c.drawRect(x + dx * 8f - 3f, y + dy * 8f - 3f, x + dx * 8f + 3f, y + dy * 8f + 3f, paint)
                1 -> { c.drawRect(x - 11f, y - 9f, x + 11f, y + 11f, paint); c.drawLine(x - 6f, y - 13f, x - 6f, y - 6f, paint); c.drawLine(x + 6f, y - 13f, x + 6f, y - 6f, paint); c.drawLine(x - 7f, y - 2f, x + 7f, y - 2f, paint) }
                2 -> { c.drawRect(x - 10f, y - 10f, x - 4f, y + 10f, paint); c.drawRect(x + 4f, y - 10f, x + 10f, y + 10f, paint); c.drawLine(x - 1f, y - 12f, x - 1f, y + 12f, paint) }
                3 -> { val star = starPath(x, y, 13f, 5f); c.drawPath(star, paint) }
                else -> { c.drawCircle(x, y - 5f, 7f, paint); c.drawArc(x - 13f, y + 3f, x + 13f, y + 20f, 180f, 180f, false, paint) }
            }
            paint.style = Paint.Style.FILL
        }

        private fun drawCollectionIcon(c: Canvas, x: Float, y: Float, color: Int) {
            paint.color = color
            c.drawRoundRect(x - 13f, y - 10f, x + 13f, y + 9f, 3f, 3f, paint)
            paint.color = 0xFF5B3A1F.toInt()
            c.drawRect(x - 5f, y - 6f, x + 5f, y - 1f, paint)
        }

        private fun drawSliders(c: Canvas, x: Float, y: Float) {
            paint.color = Color.rgb(235, 229, 221)
            paint.strokeWidth = 2f
            for (i in 0..2) {
                val yy = y + i * 8f
                c.drawLine(x - 13f, yy, x + 13f, yy, paint)
                val knob = if (i == 1) x - 3f else x + 6f
                c.drawCircle(knob, yy, 3f, paint)
            }
        }

        private fun starPath(cx: Float, cy: Float, outer: Float, inner: Float): Path {
            val path = Path()
            for (i in 0 until 10) {
                val angle = Math.toRadians(-90.0 + i * 36.0)
                val r = if (i % 2 == 0) outer else inner
                val px = cx + (Math.cos(angle) * r).toFloat()
                val py = cy + (Math.sin(angle) * r).toFloat()
                if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
            }
            path.close()
            return path
        }

        private fun lighten(color: Int, amount: Float): Int {
            val r = Color.red(color) + ((255 - Color.red(color)) * amount).toInt()
            val g = Color.green(color) + ((255 - Color.green(color)) * amount).toInt()
            val b = Color.blue(color) + ((255 - Color.blue(color)) * amount).toInt()
            return Color.rgb(r.coerceIn(0, 255), g.coerceIn(0, 255), b.coerceIn(0, 255))
        }

        private fun darken(color: Int, amount: Float): Int {
            return Color.rgb((Color.red(color) * (1f - amount)).toInt(), (Color.green(color) * (1f - amount)).toInt(), (Color.blue(color) * (1f - amount)).toInt())
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    downY = event.y
                    lastY = event.y
                    moved = false
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dy = event.y - lastY
                    if (kotlin.math.abs(event.y - downY) > 8f) moved = true
                    scroll = (scroll - dy).coerceIn(0f, 310f)
                    lastY = event.y
                    invalidate()
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    if (!moved) handleTap(event.x, event.y)
                    return true
                }
            }
            return true
        }

        private fun handleTap(x: Float, y: Float) {
            if (y < 142f) {
                val w = width.toFloat()
                when {
                    x > w - 125f && x < w - 48f && y < 95f -> searchOpen = !searchOpen
                    x < 70f && y < 85f -> selectedTab = 0
                    y > 88f -> selectedTab = (x / (w / 5f)).toInt().coerceIn(0, 4)
                }
                invalidate()
            }
        }
    }
}

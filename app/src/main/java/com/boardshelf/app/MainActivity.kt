package com.boardshelf.app

import android.app.Activity
import android.app.AlertDialog
import android.graphics.*
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.math.min

private data class BoardGame(
    val name: String,
    val year: Int,
    val players: String,
    val accent: Int,
    val motif: Int
)

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(24, 15, 11)
        window.navigationBarColor = Color.rgb(16, 11, 9)
        setContentView(BoardShelfView())
    }

    private inner class BoardShelfView : View(this@MainActivity) {
        private val games = listOf(
            BoardGame("Terraforming Mars", 2016, "1–5", Color.rgb(180, 69, 34), 0),
            BoardGame("Gloomhaven", 2017, "1–4", Color.rgb(52, 43, 39), 1),
            BoardGame("Scythe", 2016, "1–5", Color.rgb(105, 105, 103), 2),
            BoardGame("Wingspan", 2019, "1–5", Color.rgb(38, 105, 116), 3),
            BoardGame("Azul", 2017, "2–4", Color.rgb(56, 112, 128), 4),
            BoardGame("Root", 2018, "2–4", Color.rgb(76, 82, 51), 5),
            BoardGame("7 Wonders", 2010, "3–7", Color.rgb(25, 50, 91), 6),
            BoardGame("Catan", 1995, "3–4", Color.rgb(177, 61, 39), 7),
            BoardGame("Ticket to Ride", 2004, "2–5", Color.rgb(91, 91, 84), 8),
            BoardGame("Carcassonne", 2000, "2–5", Color.rgb(29, 82, 113), 9),
            BoardGame("The Lord of the Rings", 2010, "2–4", Color.rgb(59, 43, 30), 10),
            BoardGame("Pandemic", 2008, "2–4", Color.rgb(25, 65, 104), 11),
            BoardGame("Brass Birmingham", 2018, "2–4", Color.rgb(83, 82, 77), 12),
            BoardGame("El Grande", 1995, "2–5", Color.rgb(30, 61, 90), 13),
            BoardGame("Splendor", 2014, "2–4", Color.rgb(42, 47, 53), 14),
            BoardGame("7 Wonders Duel", 2015, "2", Color.rgb(67, 68, 81), 15),
            BoardGame("Tzolk'in", 2012, "2–4", Color.rgb(24, 87, 103), 16),
            BoardGame("Dominion", 2008, "2–4", Color.rgb(28, 62, 94), 17),
            BoardGame("Viticulture", 2013, "1–6", Color.rgb(112, 139, 140), 18),
            BoardGame("Ark Nova", 2021, "1–4", Color.rgb(29, 123, 96), 19),
            BoardGame("Spirit Island", 2017, "1–4", Color.rgb(27, 101, 99), 20),
            BoardGame("Heat", 2022, "1–6", Color.rgb(180, 91, 36), 21),
            BoardGame("The Crew", 2019, "2–5", Color.rgb(19, 55, 91), 22),
            BoardGame("Patchwork", 2014, "2", Color.rgb(143, 77, 43), 23),
            BoardGame("Lost Ruins of Arnak", 2020, "1–4", Color.rgb(46, 90, 54), 24),
            BoardGame("Everdell", 2018, "1–4", Color.rgb(111, 90, 45), 25),
            BoardGame("Evergreen", 2021, "1–4", Color.rgb(25, 89, 59), 26),
            BoardGame("Innovation", 2010, "2–4", Color.rgb(113, 67, 40), 27),
            BoardGame("Galaxy Trucker", 2007, "2–6", Color.rgb(28, 46, 77), 28),
            BoardGame("Twilight Imperium", 1997, "3–6", Color.rgb(36, 47, 73), 29)
        )

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val text = Paint(Paint.ANTI_ALIAS_FLAG)
        private var selectedTab = 2
        private var searchOpen = false
        private var scroll = 0f
        private var downY = 0f
        private var lastY = 0f
        private var moved = false

        init {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            text.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
        }

        override fun onDraw(c: Canvas) {
            val w = width.toFloat()
            val h = height.toFloat()
            drawBackground(c, w, h)
            drawHeader(c, w)

            c.save()
            c.clipRect(0f, 143f, w, h - 72f)
            c.translate(0f, -scroll)
            repeat(3) { row -> drawShelf(c, 157f + row * 305f, row) }
            c.restore()

            drawBottomBar(c, w, h)
        }

        private fun drawBackground(c: Canvas, w: Float, h: Float) {
            paint.shader = LinearGradient(0f, 0f, w, h, Color.rgb(25, 16, 12), Color.rgb(67, 37, 22), Shader.TileMode.CLAMP)
            c.drawRect(0f, 0f, w, h, paint)
            paint.shader = null
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1f
            for (x in 0..w.toInt() step 26) {
                paint.color = if (x % 52 == 0) 0x18000000 else 0x0C000000
                c.drawLine(x.toFloat(), 0f, x + 10f, h, paint)
            }
            paint.style = Paint.Style.FILL
        }

        private fun drawHeader(c: Canvas, w: Float) {
            paint.color = 0xF018110D.toInt()
            c.drawRect(0f, 0f, w, 143f, paint)
            paint.color = 0x552F1C14
            c.drawRect(0f, 112f, w, 143f, paint)

            drawMenu(c, 52f, Color.WHITE)
            text.color = Color.rgb(245, 236, 225)
            text.textSize = 24f
            text.typeface = Typeface.DEFAULT_BOLD
            text.textAlign = Paint.Align.LEFT
            c.drawText(if (searchOpen) "Поиск" else "Моя коллекция", 88f, 60f, text)
            if (searchOpen) {
                text.color = 0xFFBBA99A.toInt()
                text.textSize = 12f
                text.typeface = Typeface.DEFAULT
                c.drawText("30 игр · выберите игру ниже", 88f, 81f, text)
            }
            drawSearch(c, w - 83f, 53f)
            drawDots(c, w - 28f, 52f)

            val labels = arrayOf("Все игры", "Сыгранное", "Стеллаж", "Избранное", "Профиль")
            val centers = floatArrayOf(w*.10f, w*.30f, w*.50f, w*.70f, w*.90f)
            for (i in labels.indices) {
                val active = i == selectedTab
                val col = if (active) Color.rgb(255, 194, 43) else Color.rgb(237, 229, 220)
                drawTabIcon(c, centers[i], 104f, i, col)
                text.color = col
                text.textSize = 13f
                text.typeface = if (active) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                text.textAlign = Paint.Align.CENTER
                c.drawText(labels[i], centers[i], 133f, text)
            }
            paint.color = Color.rgb(255, 194, 43)
            c.drawRect(centers[selectedTab]-62f, 140f, centers[selectedTab]+62f, 144f, paint)
        }

        private fun drawBottomBar(c: Canvas, w: Float, h: Float) {
            paint.color = 0xF317120F.toInt()
            c.drawRect(0f, h-72f, w, h, paint)
            paint.color = 0x553C281E
            c.drawRect(0f, h-73f, w, h-71f, paint)
            drawCollection(c, w*.17f, h-43f, Color.rgb(255,194,43))
            text.color = Color.rgb(255,194,43)
            text.textSize = 13f
            text.typeface = Typeface.DEFAULT_BOLD
            text.textAlign = Paint.Align.CENTER
            c.drawText("Коллекция", w*.17f, h-15f, text)
            drawSliders(c, w*.84f, h-39f)
            text.textAlign = Paint.Align.LEFT
        }

        private fun drawShelf(c: Canvas, y: Float, row: Int) {
            val w = width.toFloat()
            val left = 18f
            val right = w-18f
            val board = y+248f
            paint.shader = LinearGradient(0f,y,0f,board,Color.rgb(88,50,30),Color.rgb(43,24,17),Shader.TileMode.CLAMP)
            c.drawRect(left,y,right,board,paint)
            paint.shader = null

            // warm spotlights above each row
            paint.shader = RadialGradient(w*.17f,y-1f,42f,0x66FFF0C0,0x00000000,Shader.TileMode.CLAMP)
            c.drawCircle(w*.17f,y,42f,paint)
            paint.shader = RadialGradient(w*.83f,y-1f,42f,0x66FFF0C0,0x00000000,Shader.TileMode.CLAMP)
            c.drawCircle(w*.83f,y,42f,paint)
            paint.shader = null
            paint.color = 0xFFF7E8C2.toInt()
            c.drawOval(w*.145f,y-5f,w*.195f,y+1f,paint)
            c.drawOval(w*.805f,y-5f,w*.855f,y+1f,paint)

            paint.color = Color.rgb(116,67,38)
            c.drawRect(left,board,right,board+27f,paint)
            paint.color = Color.rgb(51,29,19)
            c.drawRect(left,board+22f,right,board+29f,paint)

            val plateW = 190f
            val px = w/2f-plateW/2f
            val p = Path().apply {
                moveTo(px+12f,board+5f); lineTo(px+plateW-12f,board+5f)
                lineTo(px+plateW,board+13f); lineTo(px+plateW-12f,board+22f)
                lineTo(px+12f,board+22f); lineTo(px,board+13f); close()
            }
            paint.color = Color.rgb(112,71,41); c.drawPath(p,paint)
            paint.color = Color.rgb(215,163,85)
            c.drawCircle(px+13f,board+13f,3f,paint); c.drawCircle(px+plateW-13f,board+13f,3f,paint)
            text.color = Color.rgb(242,225,202); text.textSize = 15f; text.typeface = Typeface.DEFAULT_BOLD; text.textAlign = Paint.Align.CENTER
            c.drawText("Полка ${row+1} · 10 игр",w/2f,board+18f,text)
            text.textAlign = Paint.Align.LEFT

            val gap = 9f
            val bookLeft = 27f
            val bookRight = w-27f
            val bw = (bookRight-bookLeft-gap*9f)/10f
            for (i in 0 until 10) {
                val g = games[row*10+i]
                val x = bookLeft+i*(bw+gap)
                drawBook(c,g,row*10+i,x,y+18f,bw,board-7f-(i%4)*2f)
            }
        }

        private fun drawBook(c: Canvas, g: BoardGame, index: Int, x: Float, top: Float, bw: Float, bottom: Float) {
            paint.setShadowLayer(9f,1f,4f,0xAA000000.toInt())
            paint.shader = LinearGradient(x,top,x+bw,bottom,lighten(g.accent,.20f),darken(g.accent,.30f),Shader.TileMode.CLAMP)
            c.drawRoundRect(x,top,x+bw,bottom,4f,4f,paint)
            paint.shader = null; paint.clearShadowLayer()

            paint.color = 0x35FFFFFF
            c.drawRoundRect(x+2f,top+2f,x+4.5f,bottom-2f,2f,2f,paint)
            paint.color = 0x24000000
            c.drawRect(x+bw-4f,top+2f,x+bw-2f,bottom-2f,paint)

            drawMotif(c,g.motif,x+bw/2f,top+(bottom-top)*.42f,min(bw,62f),g.accent)

            c.save()
            c.rotate(90f,x+bw/2f,top+96f)
            text.color = Color.WHITE; text.textSize = min(15f,bw*.47f); text.typeface = Typeface.DEFAULT_BOLD; text.textAlign = Paint.Align.CENTER
            c.drawText(g.name,x+bw/2f,top+101f,text)
            c.restore()
            text.textAlign = Paint.Align.LEFT

            text.color = 0xFFF8EFE4.toInt(); text.textSize = 10f; text.typeface = Typeface.DEFAULT_BOLD
            c.drawText(g.year.toString(),x+6f,bottom-25f,text)
            text.textSize = 9f; text.typeface = Typeface.DEFAULT
            c.drawText(g.players,x+6f,bottom-10f,text)
            drawBadge(c,x+bw/2f,bottom-15f,index)
        }

        private fun drawMotif(c: Canvas, motif: Int, cx: Float, cy: Float, size: Float, accent: Int) {
            paint.style = Paint.Style.STROKE; paint.strokeWidth = 2f; paint.color = 0x68FFFFFF
            when (motif % 10) {
                0 -> { c.drawCircle(cx,cy,size*.22f,paint); c.drawLine(cx-size*.34f,cy+size*.28f,cx+size*.34f,cy-size*.28f,paint) }
                1 -> { c.drawRect(cx-size*.25f,cy-size*.25f,cx+size*.25f,cy+size*.25f,paint); c.drawCircle(cx,cy,size*.35f,paint) }
                2 -> { c.drawLine(cx-size*.35f,cy+size*.28f,cx,cy-size*.38f,paint); c.drawLine(cx,cy-size*.38f,cx+size*.35f,cy+size*.28f,paint); c.drawLine(cx-size*.2f,cy,cx+size*.2f,cy,paint) }
                3 -> { c.drawOval(cx-size*.36f,cy-size*.12f,cx+size*.36f,cy+size*.12f,paint); c.drawLine(cx,cy-size*.22f,cx,cy+size*.24f,paint) }
                4 -> { for (k in -1..1) for (j in -1..1) c.drawRect(cx+k*size*.18f-size*.07f,cy+j*size*.18f-size*.07f,cx+k*size*.18f+size*.07f,cy+j*size*.18f+size*.07f,paint) }
                5 -> { c.drawCircle(cx,cy,size*.34f,paint); c.drawCircle(cx-size*.17f,cy-size*.18f,size*.08f,paint); c.drawCircle(cx+size*.17f,cy-size*.18f,size*.08f,paint) }
                6 -> { c.drawRect(cx-size*.32f,cy-size*.28f,cx+size*.32f,cy+size*.28f,paint); c.drawLine(cx-size*.32f,cy,cx+size*.32f,cy,paint) }
                7 -> { c.drawCircle(cx,cy,size*.34f,paint); c.drawLine(cx-size*.27f,cy+size*.2f,cx+size*.27f,cy-size*.2f,paint) }
                8 -> { c.drawLine(cx-size*.32f,cy+size*.3f,cx,cy-size*.3f,paint); c.drawLine(cx,cy-size*.3f,cx+size*.32f,cy+size*.3f,paint); c.drawCircle(cx,cy,size*.12f,paint) }
                else -> { c.drawPath(Path().apply { moveTo(cx,cy-size*.38f); lineTo(cx+size*.33f,cy); lineTo(cx,cy+size*.38f); lineTo(cx-size*.33f,cy); close() },paint) }
            }
            paint.style = Paint.Style.FILL
            paint.color = 0x24FFFFFF or (accent and 0x00FFFFFF)
            c.drawCircle(cx,cy,size*.08f,paint)
        }

        private fun drawBadge(c: Canvas,x:Float,y:Float,index:Int) {
            paint.style=Paint.Style.STROKE; paint.strokeWidth=1.5f; paint.color=0xDDFFFFFF.toInt()
            val p=Path().apply { moveTo(x,y-7f); lineTo(x+7f,y-3f); lineTo(x+5f,y+6f); lineTo(x,y+9f); lineTo(x-5f,y+6f); lineTo(x-7f,y-3f); close() }
            c.drawPath(p,paint); paint.style=Paint.Style.FILL
            text.color=Color.WHITE; text.textSize=6f; text.typeface=Typeface.DEFAULT_BOLD; text.textAlign=Paint.Align.CENTER
            c.drawText(((index%5)+1).toString(),x,y+2f,text); text.textAlign=Paint.Align.LEFT
        }

        private fun drawMenu(c:Canvas,x:Float,col:Int){ paint.color=col; paint.strokeWidth=3f; paint.strokeCap=Paint.Cap.ROUND; for(i in -1..1)c.drawLine(x-15f,52f+i*9f,x+15f,52f+i*9f,paint); paint.strokeCap=Paint.Cap.BUTT }
        private fun drawSearch(c:Canvas,x:Float,y:Float){ paint.style=Paint.Style.STROKE; paint.strokeWidth=3f; paint.color=Color.WHITE; c.drawCircle(x,y-3f,12f,paint); c.drawLine(x+9f,y+6f,x+18f,y+15f,paint); paint.style=Paint.Style.FILL }
        private fun drawDots(c:Canvas,x:Float,y:Float){ paint.color=Color.WHITE; c.drawCircle(x,y-10f,3f,paint); c.drawCircle(x,y,3f,paint); c.drawCircle(x,y+10f,3f,paint) }
        private fun drawTabIcon(c:Canvas,x:Float,y:Float,i:Int,col:Int){ paint.color=col; paint.style=Paint.Style.STROKE; paint.strokeWidth=2.2f; when(i){0->{for(a in -1..1)for(b in -1..1)c.drawRect(x+a*9f-3f,y+b*9f-3f,x+a*9f+3f,y+b*9f+3f,paint)};1->{c.drawRect(x-11f,y-11f,x+11f,y+10f,paint);c.drawLine(x-7f,y-15f,x-7f,y-8f,paint);c.drawLine(x+7f,y-15f,x+7f,y-8f,paint);c.drawLine(x-8f,y-2f,x+8f,y-2f,paint)};2->{c.drawRect(x-8f,y-11f,x-3f,y+11f,paint);c.drawRect(x+3f,y-11f,x+8f,y+11f,paint);c.drawLine(x-11f,y+7f,x+11f,y+7f,paint)};3->{c.drawPath(Path().apply{moveTo(x,y-12f);lineTo(x+4f,y-2f);lineTo(x+13f,y-2f);lineTo(x+6f,y+4f);lineTo(x+9f,y+13f);lineTo(x,y+8f);lineTo(x-9f,y+13f);lineTo(x-6f,y+4f);lineTo(x-13f,y-2f);lineTo(x-4f,y-2f);close()},paint)};4->{c.drawCircle(x,y-2f,7f,paint);c.drawArc(x-12f,y+3f,x+12f,y+17f,190f,160f,false,paint)}};paint.style=Paint.Style.FILL }
        private fun drawCollection(c:Canvas,x:Float,y:Float,col:Int){paint.color=col;c.drawRoundRect(x-11f,y-10f,x+11f,y+9f,3f,3f,paint);paint.color=0xFF2B1B12.toInt();c.drawRect(x-5f,y-14f,x+5f,y-9f,paint);c.drawCircle(x,y,3f,paint)}
        private fun drawSliders(c:Canvas,x:Float,y:Float){paint.color=Color.rgb(225,218,209);paint.strokeWidth=2f;for(i in -1..1){val yy=y+i*7f;c.drawLine(x-13f,yy,x+13f,yy,paint)};paint.color=Color.rgb(225,218,209);c.drawRect(x-5f,y-10f,x+1f,y-4f,paint);c.drawRect(x+5f,y-3f,x+11f,y+3f,paint);c.drawRect(x-9f,y+4f,x-3f,y+10f,paint)}

        private fun lighten(color:Int,amount:Float):Int{ return blend(color,Color.WHITE,amount) }
        private fun darken(color:Int,amount:Float):Int{ return blend(color,Color.BLACK,amount) }
        private fun blend(a:Int,b:Int,t:Float):Int{val u=1f-t;return Color.rgb((Color.red(a)*u+Color.red(b)*t).toInt(),(Color.green(a)*u+Color.green(b)*t).toInt(),(Color.blue(a)*u+Color.blue(b)*t).toInt())}

        override fun onTouchEvent(event: MotionEvent): Boolean {
            val y=event.y
            when(event.action){
                MotionEvent.ACTION_DOWN->{downY=y;lastY=y;moved=false;return true}
                MotionEvent.ACTION_MOVE->{val dy=y-lastY;if(kotlin.math.abs(y-downY)>8f)moved=true;scroll=(scroll-dy).coerceIn(0f,300f);lastY=y;invalidate();return true}
                MotionEvent.ACTION_UP->{
                    if(moved)return performClick()
                    val x=event.x
                    when{
                        y<95f && x<75f->{ selectedTab=2; scroll=0f; invalidate() }
                        y<95f && x>width-115f->{ searchOpen=!searchOpen; invalidate() }
                        y in 88f..145f->{ val tab=(x/(width/5f)).toInt().coerceIn(0,4);selectedTab=tab;invalidate() }
                        y>height-80f && x<width*.35f->{selectedTab=2;invalidate()}
                        else->openGameAt(x,y)
                    }
                    return performClick()
                }
            }
            return true
        }

        private fun openGameAt(x:Float,y:Float){
            if(y<145f || y>height-80f)return
            val contentY=y+scroll
            val row=((contentY-157f)/305f).toInt()
            if(row !in 0..2)return
            val gap=9f;val left=27f;val bw=(width-54f-gap*9f)/10f
            val i=((x-left)/(bw+gap)).toInt()
            if(i !in 0..9)return
            val game=games[row*10+i]
            AlertDialog.Builder(this@MainActivity).setTitle(game.name).setMessage("${game.year} · ${game.players} игрока\n\nИгра находится на полке ${row+1}.").setPositiveButton("Закрыть",null).show()
        }

        override fun performClick(): Boolean { super.performClick(); return true }
    }
}

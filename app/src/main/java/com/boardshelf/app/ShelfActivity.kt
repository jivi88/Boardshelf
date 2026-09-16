package com.boardshelf.app

import android.app.Activity
import android.app.AlertDialog
import android.graphics.*
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.math.min

private data class ShelfGame(val name: String, val year: Int, val players: String)

class ShelfActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(24, 15, 11)
        window.navigationBarColor = Color.rgb(16, 11, 9)
        setContentView(ShelfView())
    }

    private inner class ShelfView : View(this@ShelfActivity) {
        private val games = listOf(
            ShelfGame("Terraforming Mars",2016,"1–5"), ShelfGame("Gloomhaven",2017,"1–4"), ShelfGame("Scythe",2016,"1–5"), ShelfGame("Wingspan",2019,"1–5"), ShelfGame("Azul",2017,"2–4"), ShelfGame("Root",2018,"2–4"), ShelfGame("7 Wonders",2010,"3–7"), ShelfGame("Catan",1995,"3–4"), ShelfGame("Ticket to Ride",2004,"2–5"), ShelfGame("Carcassonne",2000,"2–5"),
            ShelfGame("The Lord of the Rings",2010,"2–4"), ShelfGame("Pandemic",2008,"2–4"), ShelfGame("Brass Birmingham",2018,"2–4"), ShelfGame("El Grande",1995,"2–5"), ShelfGame("Splendor",2014,"2–4"), ShelfGame("7 Wonders Duel",2015,"2"), ShelfGame("Tzolk'in",2012,"2–4"), ShelfGame("Dominion",2008,"2–4"), ShelfGame("Viticulture",2013,"1–6"), ShelfGame("Ark Nova",2021,"1–4"),
            ShelfGame("Spirit Island",2017,"1–4"), ShelfGame("Heat",2022,"1–6"), ShelfGame("The Crew",2019,"2–5"), ShelfGame("Patchwork",2014,"2"), ShelfGame("Lost Ruins of Arnak",2020,"1–4"), ShelfGame("Everdell",2018,"1–4"), ShelfGame("Evergreen",2021,"1–4"), ShelfGame("Innovation",2010,"2–4"), ShelfGame("Galaxy Trucker",2007,"2–6"), ShelfGame("Twilight Imperium",1997,"3–6")
        )
        private val p = Paint(Paint.ANTI_ALIAS_FLAG)
        private val t = Paint(Paint.ANTI_ALIAS_FLAG)
        private var scroll = 0f
        private var downY = 0f
        private var lastY = 0f
        private var moved = false
        private var selected = 2

        init { setLayerType(View.LAYER_TYPE_SOFTWARE, null) }

        override fun onDraw(c: Canvas) {
            val w = width.toFloat(); val h = height.toFloat()
            p.shader = LinearGradient(0f,0f,w,h,Color.rgb(25,16,12),Color.rgb(66,36,22),Shader.TileMode.CLAMP)
            c.drawRect(0f,0f,w,h,p); p.shader=null
            drawHeader(c,w); drawShelves(c,w,h); drawBottom(c,w,h)
        }

        private fun drawHeader(c: Canvas,w:Float) {
            p.color=0xF018110D.toInt(); c.drawRect(0f,0f,w,143f,p)
            drawMenu(c,52f,52f)
            t.color=Color.rgb(245,236,225); t.textSize=24f; t.typeface=Typeface.DEFAULT_BOLD; t.textAlign=Paint.Align.LEFT
            c.drawText("Моя коллекция",88f,60f,t)
            drawSearch(c,w-83f,53f); drawDots(c,w-28f,52f)
            val labels=arrayOf("Все игры","Сыгранное","Стеллаж","Избранное","Профиль")
            val centers=floatArrayOf(w*.10f,w*.30f,w*.50f,w*.70f,w*.90f)
            for(i in labels.indices){ val col=if(i==selected)Color.rgb(255,194,43) else Color.rgb(237,229,220); drawTab(c,centers[i],104f,i,col); t.color=col;t.textSize=13f;t.typeface=if(i==selected)Typeface.DEFAULT_BOLD else Typeface.DEFAULT;t.textAlign=Paint.Align.CENTER;c.drawText(labels[i],centers[i],133f,t) }
            p.color=Color.rgb(255,194,43); c.drawRect(centers[selected]-62f,140f,centers[selected]+62f,144f,p)
        }

        private fun drawShelves(c:Canvas,w:Float,h:Float){
            c.save(); c.clipRect(0f,143f,w,h-72f); c.translate(0f,-scroll)
            repeat(3){ row ->
                val y=157f+row*305f; val board=y+248f
                p.shader=LinearGradient(0f,y,0f,board,Color.rgb(88,50,30),Color.rgb(43,24,17),Shader.TileMode.CLAMP); c.drawRect(18f,y,w-18f,board,p); p.shader=null
                p.shader=RadialGradient(w*.17f,y,42f,0x66FFF0C0,0x00000000,Shader.TileMode.CLAMP); c.drawCircle(w*.17f,y,42f,p)
                p.shader=RadialGradient(w*.83f,y,42f,0x66FFF0C0,0x00000000,Shader.TileMode.CLAMP); c.drawCircle(w*.83f,y,42f,p); p.shader=null
                p.color=0xFFF7E8C2.toInt(); c.drawOval(w*.145f,y-5f,w*.195f,y+1f,p); c.drawOval(w*.805f,y-5f,w*.855f,y+1f,p)
                p.color=Color.rgb(116,67,38); c.drawRect(18f,board,w-18f,board+27f,p); p.color=Color.rgb(51,29,19); c.drawRect(18f,board+22f,w-18f,board+29f,p)
                val pw=190f; val px=w/2f-pw/2f; val path=Path().apply{moveTo(px+12f,board+5f);lineTo(px+pw-12f,board+5f);lineTo(px+pw,board+13f);lineTo(px+pw-12f,board+22f);lineTo(px+12f,board+22f);lineTo(px,board+13f);close()}
                p.color=Color.rgb(112,71,41);c.drawPath(path,p);p.color=Color.rgb(215,163,85);c.drawCircle(px+13f,board+13f,3f,p);c.drawCircle(px+pw-13f,board+13f,3f,p)
                t.color=Color.rgb(242,225,202);t.textSize=15f;t.typeface=Typeface.DEFAULT_BOLD;t.textAlign=Paint.Align.CENTER;c.drawText("Полка ${row+1} · 10 игр",w/2f,board+18f,t)
                val gap=9f;val left=27f;val bw=(w-54f-gap*9f)/10f
                for(i in 0 until 10){ val idx=row*10+i; val x=left+i*(bw+gap); drawBook(c,idx,x,y+18f,bw,board-7f-(i%4)*2f) }
            }; c.restore()
        }

        private fun drawBook(c:Canvas,index:Int,x:Float,top:Float,bw:Float,bottom:Float){
            p.setShadowLayer(9f,1f,4f,0xAA000000.toInt()); p.color=0xFF1A1512.toInt(); c.drawRoundRect(x,top,x+bw,bottom,4f,4f,p); p.clearShadowLayer()
            CoverSprite.drawCover(c,index,Rect(x.toInt(),top.toInt(),(x+bw).toInt(),bottom.toInt()))
        }

        private fun drawBottom(c:Canvas,w:Float,h:Float){
            p.color=0xF317120F.toInt();c.drawRect(0f,h-72f,w,h,p);p.color=0x553C281E;c.drawRect(0f,h-73f,w,h-71f,p)
            drawCollection(c,w*.17f,h-43f,Color.rgb(255,194,43));t.color=Color.rgb(255,194,43);t.textSize=13f;t.typeface=Typeface.DEFAULT_BOLD;t.textAlign=Paint.Align.CENTER;c.drawText("Коллекция",w*.17f,h-15f,t);drawSliders(c,w*.84f,h-39f);t.textAlign=Paint.Align.LEFT
        }

        override fun onTouchEvent(e:MotionEvent):Boolean{ when(e.action){ MotionEvent.ACTION_DOWN->{downY=e.y;lastY=e.y;moved=false;return true}; MotionEvent.ACTION_MOVE->{val dy=e.y-lastY;if(kotlin.math.abs(e.y-downY)>8)moved=true;scroll=(scroll-dy).coerceIn(0f,650f);lastY=e.y;invalidate();return true}; MotionEvent.ACTION_UP->{if(!moved && e.y>=143f && e.y<height-72f){val yy=e.y+scroll;val row=((yy-157f)/305f).toInt();val yIn=yy-row*305f-175f;val bw=(width-54f-9f*9f)/10f;val col=((e.x-27f)/(bw+9f)).toInt();if(row in 0..2 && col in 0..9 && e.x>=27f+col*(bw+9f)&&e.x<=27f+col*(bw+9f)+bw){showGame(row*10+col)}};return true} };return true }

        private fun showGame(i:Int){val g=games[i];AlertDialog.Builder(this@ShelfActivity).setTitle(g.name).setMessage("Год: ${g.year}\nИгроки: ${g.players}\n\nФото коробки взято из референсного изображения.").setPositiveButton("Закрыть",null).show()}
        private fun drawMenu(c:Canvas,x:Float,y:Float){p.color=Color.WHITE;p.strokeWidth=3f;for(i in -1..1)c.drawLine(x-16f,y+i*11f,x+16f,y+i*11f,p)}
        private fun drawSearch(c:Canvas,x:Float,y:Float){p.style=Paint.Style.STROKE;p.strokeWidth=3f;p.color=Color.WHITE;c.drawCircle(x-3f,y-4f,13f,p);c.drawLine(x+7f,y+6f,x+18f,y+17f,p);p.style=Paint.Style.FILL}
        private fun drawDots(c:Canvas,x:Float,y:Float){p.color=Color.WHITE;for(i in -1..1)c.drawCircle(x,y+i*10f,3f,p)}
        private fun drawTab(c:Canvas,x:Float,y:Float,i:Int,col:Int){p.color=col;p.style=Paint.Style.STROKE;p.strokeWidth=2f;when(i){0->{for(a in -1..1)for(b in -1..1)c.drawRect(x-11+b*10f,y-10+a*10f,x-4+b*10f,y-3+a*10f,p)};1->c.drawRect(x-12f,y-13f,x+12f,y+10f,p);2->{c.drawLine(x-10f,y+12f,x-10f,y-13f,p);c.drawLine(x,y+12f,x,y-13f,p);c.drawLine(x+10f,y+12f,x+10f,y-13f,p)};3->c.drawPath(Path().apply{moveTo(x,y-14f);lineTo(x+4f,y-4f);lineTo(x+14f,y-4f);lineTo(x+6f,y+2f);lineTo(x+9f,y+12f);lineTo(x,y+6f);lineTo(x-9f,y+12f);lineTo(x-6f,y+2f);lineTo(x-14f,y-4f);lineTo(x-4f,y-4f);close()},p);4->c.drawCircle(x,y,12f,p)};p.style=Paint.Style.FILL}
        private fun drawCollection(c:Canvas,x:Float,y:Float,col:Int){p.color=col;c.drawRoundRect(x-11f,y-9f,x+11f,y+9f,3f,3f,p);p.color=0xFF302015.toInt();c.drawRect(x-5f,y-5f,x+5f,y+5f,p)}
        private fun drawSliders(c:Canvas,x:Float,y:Float){p.color=Color.WHITE;p.strokeWidth=2f;for(i in -1..1){val yy=y+i*8f;c.drawLine(x-13f,yy,x+13f,yy,p);c.drawCircle(x+(if(i==0)5f else -4f),yy,3f,p)}}
    }
}

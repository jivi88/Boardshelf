package com.boardshelf.app

import android.app.Activity
import android.graphics.*
import android.os.Bundle
import android.view.View

class MainActivity : Activity() {
 override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContentView(ShelfView()) }
 private inner class ShelfView : View(this@MainActivity) {
  private val games = listOf("Terraforming Mars","Gloomhaven","Scythe","Wingspan","Azul","Root","7 Wonders","Catan","Ticket to Ride","Carcassonne","The Lord of the Rings","Pandemic","Brass Birmingham","El Grande","Splendor","7 Wonders Duel","Tzolk'in","Dominion","Viticulture","Ark Nova","Spirit Island","Heat","The Crew","Patchwork","Lost Ruins of Arnak","Everdell","Evergreen","Innovation","Galaxy Trucker","Twilight Imperium")
  private val years = listOf(2016,2017,2016,2019,2017,2018,2010,1995,2004,2000,2010,2008,2018,1995,2014,2015,2012,2008,2013,2021,2017,2022,2019,2014,2020,2018,2021,2010,2007,1997)
  private val p = Paint(Paint.ANTI_ALIAS_FLAG)
  override fun onDraw(c: Canvas) { val w=width.toFloat(); c.drawColor(Color.rgb(28,18,14)); p.color=Color.rgb(34,22,17); c.drawRect(0,0,w,112,p); p.color=Color.WHITE; p.textSize=25f; c.drawText("☰",28,58,p); c.drawText("BoardShelf",92,58,p); c.drawText("⌕",w-72,58,p); p.textSize=13f; p.color=Color.rgb(255,195,45); c.drawText("▥     Сыгранное       Стеллаж       ☆     Профиль",70,96,p); val top=135f; for(row in 0..2) drawShelf(c,top+row*294,row*10); p.color=Color.rgb(30,18,14); c.drawRect(0,height-66,w,height,p); p.color=Color.rgb(255,195,45); p.textSize=14f; c.drawText("▣   Коллекция",35,height-35,p); c.drawText("☷",w-55,height-35,p) }
  private fun drawShelf(c:Canvas,y:Float,start:Int){ p.color=Color.rgb(91,51,29); c.drawRect(18,y+245,width-18,y+270,p); p.color=Color.rgb(118,72,39); c.drawRect(18,y,width-18,y+6,p); val bw=(width-52)/10f; for(i in 0 until 10){ val x=26+i*bw; val h=220-(i%3)*8; p.color=Color.rgb(35+(i*17)%100,45+(i*11)%80,50+(i*7)%70); c.drawRoundRect(x,y+15,x+bw-3,y+15+h,5f,5f,p); p.color=Color.WHITE; p.textSize=14f; c.save(); c.rotate(90f,x+bw/2,y+45); c.drawText(games[start+i],x+bw/2,y+45,p); c.restore(); p.textSize=9f; c.drawText(years[start+i].toString(),x+7,y+15+h-10,p) }; p.color=Color.rgb(235,220,198); p.textSize=14f; c.drawText("Полка ${start/10+1} · 10 игр",width/2-48,y+262,p) }
 }
}

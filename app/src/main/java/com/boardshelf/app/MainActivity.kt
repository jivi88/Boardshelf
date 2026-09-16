package com.boardshelf.app

import android.graphics.*
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
 override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContentView(ShelfView()) }
 private inner class ShelfView : View(this@MainActivity) {
  private val games = listOf("Terraforming Mars","Gloomhaven","Scythe","Wingspan","Azul","Root","7 Wonders","Catan","Ticket to Ride","Carcassonne","The Lord of the Rings","Pandemic","Brass Birmingham","El Grande","Splendor","7 Wonders Duel","Tzolk'in","Dominion","Viticulture","Ark Nova","Spirit Island","Heat","The Crew","Patchwork","Lost Ruins of Arnak","Everdell","Evergreen","Innovation","Galaxy Trucker","Twilight Imperium")
  private val years = listOf(2016,2017,2016,2019,2017,2018,2010,1995,2004,2000,2010,2008,2018,1995,2014,2015,2012,2008,2013,2021,2017,2022,2019,2014,2020,2018,2021,2010,2007,1997)
  private val paints = Paint(Paint.ANTI_ALIAS_FLAG)
  override fun onDraw(c: Canvas) { super.onDraw(c); val w=width.toFloat(); c.drawColor(Color.rgb(28,18,14));
   paints.color=Color.rgb(34,22,17); c.drawRect(0,0,w,112,paints)
   paints.color=Color.WHITE; paints.textSize=25f; c.drawText("☰",28,58,paints); paints.textSize=25f; c.drawText("BoardShelf",92,58,paints); c.drawText("⌕",w-72,58,paints)
   paints.textSize=13f; paints.color=Color.rgb(255,195,45); c.drawText("▥     Сыгранное       Стеллаж       ☆     Профиль",70,96,paints)
   val top=135f; val shelfH=270f; val gap=24f; for(row in 0..2){ val y=top+row*(shelfH+gap); drawShelf(c,y,row*10); }
   paints.color=Color.rgb(30,18,14); c.drawRect(0,height-66,w,height,paints); paints.color=Color.rgb(255,195,45); paints.textSize=14f; c.drawText("▣",35,height-35,paints); c.drawText("Коллекция",80,height-35,paints); c.drawText("☷",w-55,height-35,paints)
  }
  private fun drawShelf(c:Canvas,y:Float,start:Int){ paints.color=Color.rgb(91,51,29); c.drawRect(18,y+245,width-18,y+270,paints); paints.color=Color.rgb(118,72,39); c.drawRect(18,y,width-18,y+6,paints); val count=10; val bw=(width-52)/count.toFloat(); for(i in 0 until count){ val x=26+i*bw; val h=220-(i%3)*8; paints.color=Color.rgb(35+(i*17)%100,45+(i*11)%80,50+(i*7)%70); c.drawRoundRect(x,y+15,x+bw-3,y+15+h,5f,5f,paints); paints.color=Color.WHITE; paints.textSize=14f; c.save(); c.rotate(90f,x+bw/2,y+45); c.drawText(games[start+i],x+bw/2,y+45,paints); c.restore(); paints.textSize=9f; c.drawText(years[start+i].toString(),x+7,y+15+h-10,paints) } paints.color=Color.rgb(235,220,198); paints.textSize=14f; c.drawText("Полка ${start/10+1} · 10 игр",width/2-48,y+262,paints) }
 }
}

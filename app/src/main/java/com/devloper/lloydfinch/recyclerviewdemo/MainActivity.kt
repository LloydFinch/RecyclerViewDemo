package com.devloper.lloydfinch.recyclerviewdemo

import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.devloper.lloydfinch.recyclerviewdemo.recyclerview.RecyclerAdapter

class MainActivity : AppCompatActivity() {


    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = this.findViewById(R.id.recycler_view)

        //设置方向，默认是垂直的
        recyclerView.layoutManager = LinearLayoutManager(this)
        //设置动画 //如果要设置为DefaultItemAnimator的话，就没什么卵用，人家自己默认已经实现了
        recyclerView.itemAnimator = DefaultItemAnimator()
        //设置间隔线
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun onDraw(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
                super.onDraw(c, parent, state)

                //绘制的装饰物会在绘制item之前绘制，也就是绘制在item的下面
                //c?.drawARGB(255, 255, 0, 0) //这里绘制一个red color，直接作为背景
            }

            override fun onDrawOver(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
                super.onDrawOver(c, parent, state)

                //绘制的装饰物会在item绘制之后绘制，也就是绘制在item的上面
                //c?.drawARGB(255, 0, 0, 255) //这里绘制一个green color，直接覆盖内容
            }

            override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
                super.getItemOffsets(outRect, view, parent, state)

                //这里的React就是漏出来让你绘制的分割线
                //view就是对应的itemView
                //State表示当前的状态: 正在布局/正在动画

                //设置item之间的间隔
                //left/right: 控制的不仅仅是分割线的边距，还有item的边距
                //top/bottom: 二者之和是间距(测试结果，需要看源码证明)
                outRect?.apply {
                    left = 8
                    top = 2
                    right = 8
                    bottom = 2
                }
            }
        })

        testBasicFunction()
    }

    //<editor-fold desc = "测试基本用法">
    private fun testBasicFunction() {
        val datas: ArrayList<String> = ArrayList()
        for (i in 0..1000) {
            datas.add("android$i")
        }
        val adapter = RecyclerAdapter(datas)
        recyclerView.adapter = adapter
        recyclerView.setOnClickListener {
            Toast.makeText(this, "click recycler", Toast.LENGTH_SHORT).show()
        }
        //recyclerView.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS

        adapter.notifyDataSetChanged()
        adapter.notifyItemChanged(0)

        recyclerView.isLayoutFrozen = true //不能滑动，设置适配器会自动设置为false
    }
    //<editor-fold>
}

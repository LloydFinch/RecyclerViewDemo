package com.devloper.lloydfinch.recyclerviewdemo

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.devloper.lloydfinch.recyclerviewdemo.recyclerview.RecyclerAdapter

class MainActivity : AppCompatActivity() {


    private lateinit var recyclerView: RecyclerView
    private var adapter: RecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = this.findViewById(R.id.recycler_view)

        //设置LayoutManager，LinearLayoutManager的方向默认是垂直的
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        //设置动画
        addItemAnimation()

        //设置间隔线
        addItemDecoration()

        //测试基础功能
        testBasicFunction()

        //测试一些辅助性能的view
        addAssistView()
    }

    //<editor-fold desc = "测试基本用法">
    private fun testBasicFunction() {
        val datas: ArrayList<String> = ArrayList()
        for (i in 0..1000) {
//            datas.add("android$i")
        }
        adapter = RecyclerAdapter(datas)
        recyclerView.adapter = adapter
        recyclerView.setOnClickListener {
            Toast.makeText(this, "click recycler", Toast.LENGTH_SHORT).show()
        }
        //recyclerView.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS

        adapter?.notifyDataSetChanged()

        //recyclerView.isLayoutFrozen = true //不能滑动，设置适配器会自动设置为false
    }
    //<editor-fold>

    //<editor-fold desc = "添加自定义分割线">
    private fun addItemDecoration() {

        /**
         * 1 画一层背景a
         * 2 根据设置的Rect留下空白区域，被a填满
         * 3 那么被a填满的区域看起来就像分割线一样
         */

        val itemDecoration: RecyclerView.ItemDecoration = object : RecyclerView.ItemDecoration() {
            override fun onDraw(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
                super.onDraw(c, parent, state)
                //这里的绘制是在item绘制之前执行的，也就是画在item的下面，可能作为item的背景
//                c?.drawColor(Color.BLUE)

                parent?.apply {
                    val paint = Paint()
                    paint.color = Color.YELLOW
                    c?.drawCircle((measuredWidth shr 1).toFloat(), (measuredHeight shr 1).toFloat(), (measuredWidth shr 1).toFloat(), paint)
                }
            }

            override fun onDrawOver(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
                super.onDrawOver(c, parent, state)
                //这里的绘制是在item绘制之后执行的，也就是item的上面，直接覆盖内容区域
                //c?.drawColor(Color.YELLOW)
            }

            //这才是设置分割线的核心方法
            override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
                super.getItemOffsets(outRect, view, parent, state)
                //这里返回的Rect作为分割线的大小标准参照物

                //这个玩意实际是用来作为item的padding来计算的，跟分割线的绘制一点关系都没有
                outRect?.apply {
                    left = 50 //不仅影响分割线，还影响item
                    right = 50 //不仅影响分割线，还影响item
                    top = 20
                    bottom = 20
                }
            }
        }
        recyclerView.addItemDecoration(itemDecoration)
    }
    //<editor-fold>

    //<editor-fold desc = "添加header、footer、EmptyView、ItemClickListener">
    @SuppressLint("SetTextI18n")
    private fun addAssistView() {
        //header/footer的思路:判断数据是第一条/最后一条 返回不同的布局
        adapter?.apply {
            val headerView = TextView(this@MainActivity)
            headerView.text = "header"
            setHeaderView(headerView)

            val footerView = TextView(this@MainActivity)
            footerView.text = "footer"
            setFooterView(footerView)
        }
        //EmptyView的设计思路:判断数据集是否为空，设置不同的View
        //来一个FrameLayout，数据为空就显示一个空的布局，这里就不扯犊子了
        val emptyView = TextView(this@MainActivity)
        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER)
        layoutParams.gravity = Gravity.CENTER
        emptyView.layoutParams = layoutParams
        emptyView.gravity = Gravity.CENTER
        emptyView.text = "empty"
        val isEmpty = true //测试一下
        if (isEmpty) {
            //添加EmptyView
            (recyclerView.parent as ViewGroup).addView(emptyView)
        }
        //关闭EmptyView
        (recyclerView.parent as ViewGroup).removeView(emptyView)

        //ItemClickListener:传给适配器一个点击事件即可，然后适配器再设置给ViewHolder的itemView
        //代码看适配器
    }
    //<editor-fold>

    //<editor-fold desc = "添加各种动画">
    private fun addItemAnimation() {
        //如果要设置为DefaultItemAnimator的话，就没什么卵用，人家自己默认已经实现了
        recyclerView.itemAnimator = DefaultItemAnimator()
    }
    //<editor-fold>

    //<editor-fold desc = "添加滑动监听事件">
    private fun addScrollListener() {

    }
    //<editor>
}

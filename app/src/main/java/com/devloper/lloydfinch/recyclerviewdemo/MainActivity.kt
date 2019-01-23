package com.devloper.lloydfinch.recyclerviewdemo

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.*
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.devloper.lloydfinch.recyclerviewdemo.recyclerview.RecyclerAdapter
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import java.util.*

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    private lateinit var btnAdd: Button  //添加一条数据
    private lateinit var btnRemove: Button //移除一条数据

    private lateinit var recyclerView: RecyclerView
    private val datas: ArrayList<String> = ArrayList()
    private var adapter: RecyclerAdapter? = null
    private val pageSize = 20 //默认的一页的大小
    private var lastScrollDY = 0 //最后一次垂直滑动的距离
    private var lastVisiblePosition = 0 //最后一条可见数据的位置
    private var isLoading = false //是否正在加载数据

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnAdd = findViewById(R.id.btn_add)
        btnAdd.setOnClickListener {
            //添加一条数据
            datas.add(0, "add0")
            adapter?.apply {
                notifyItemInserted(if (hadHeader()) 1 else 0)
            }
        }
        btnRemove = findViewById(R.id.btn_remove)
        btnRemove.setOnClickListener {
            //移除一条数据
            datas.removeAt(0)
            adapter?.apply {
                notifyItemRemoved(if (hadHeader()) 1 else 0)
            }
        }

        recyclerView = this.findViewById(R.id.recycler_view)

        //设置LayoutManager，LinearLayoutManager的方向默认是垂直的
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
//        recyclerView.layoutManager = GridLayoutManager(this, 2)
//        recyclerView.layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)

        //设置动画
        addItemAnimation()

        //设置间隔线
        addItemDecoration()

        //测试基础功能
        testBasicFunction()

        //测试一些辅助性能的view
        addAssistView()

        //测试滑动监听
        addScrollListener()
    }

    //<editor-fold desc = "测试基本用法">
    private fun testBasicFunction() {
        //初始化加载两页
        val initSize = pageSize shl 1
        for (i in 1..initSize) {
            datas.add("android$i")
        }
        adapter = RecyclerAdapter(datas)
        recyclerView.adapter = adapter
        recyclerView.setOnClickListener {
            Toast.makeText(this, "click recycler", Toast.LENGTH_SHORT).show()
        }
        //recyclerView.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS

        adapter?.notifyDataSetChanged()
    }

    //加载更多数据，这里一次加载一页
    private fun loadMoreData() {
        //isLoading标记注意线程问题
        if (!isLoading) {
            isLoading = true
            val currentSize = adapter!!.itemCount
            Log.e("$TAG-1", "loadMoreData-before: currentSize = $currentSize")
            for (i in 1..pageSize) {
                datas.add("android${currentSize + i}")
            }
            adapter?.notifyDataSetChanged()
            Log.e("$TAG-1", "loadMoreData-after: currentSize = $currentSize")
            isLoading = false
        }
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
        //重视UI的时候，使用动画 : 数量少但是块头大的数据，比如CardView
        //重视数据的时候，不使用动画 : 数量多而简单的数据，比如消息列表
        //recyclerView.isLayoutFrozen = true //不能滑动，设置适配器会自动设置为false

        //这里添加一个简单的拖拽和侧滑的动画
        //上下左右都能拖拽
        val dragDirs = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        //这里定义能左滑删除
        val swipeDirs = ItemTouchHelper.LEFT
        val callback = object : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                //item移动的时候会回调到这里
                //只要移动超过一个item，就会回调

                try {

                    val initFrom = viewHolder.adapterPosition
                    val initTo = target.adapterPosition

                    var fromPosition = initFrom
                    var toPosition = initTo

                    Log.e("addItemAnimation", "onMove, from:$fromPosition, to:$toPosition")

                    //有header的情况下，header不能作为to
                    if (adapter!!.hadHeader()) {
                        if (toPosition == 0) {
                            return true
                        }

                        //并且header存在的情况下，因为item整体向下移动一个位置，所以数据要整体向上移动一个位置进行修正
                        fromPosition--
                        toPosition--
                    }

                    if (fromPosition < toPosition) {
                        for (i in fromPosition until toPosition) {
                            Collections.swap(datas, i, i + 1)
                        }
                    } else {
                        for (i in toPosition until fromPosition) {
                            Collections.swap(datas, i, i + 1)
                        }
                    }

                    //数据交换完毕，还需要转换为布局的位置进行notify
                    //这里的两个参数不能随便更改，一定要使用参数传入的
                    adapter?.notifyItemMoved(initFrom, initTo)
                } catch (e: Throwable) {
                    Log.e("addItemAnimation", "error:" + e.message)
                }

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //item侧滑的时候会回调到这里
                //只有侧滑完成才会回调
                Log.e("addItemAnimation", "onSwiped")
                datas.removeAt(viewHolder.adapterPosition)
                adapter?.notifyItemRemoved(viewHolder.adapterPosition)
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

//                //onDraw()的时候往这里跑
//                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
//                    //如果是正在滑动删除，则展示一个淡出的透明度动画
//                    val alpha = 1 - Math.abs(dX) / viewHolder.itemView.width.toFloat()
//                    viewHolder.itemView.alpha = alpha
//                    viewHolder.itemView.translationX = dX
//                } else {
//                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

            }
        }

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)


        //来个自定义动画了解一下动画实现机制
        recyclerView.itemAnimator = DefaultItemAnimator()
//        recyclerView.itemAnimator = object : SimpleItemAnimator() {
//            override fun runPendingAnimations() {
//
//            }
//
//            override fun isRunning(): Boolean {
//            }
//
//            override fun endAnimation(item: RecyclerView.ViewHolder?) {
//            }
//
//            override fun endAnimations() {
//            }
//
//            override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
//                return true
//            }
//
//            override fun animateMove(holder: RecyclerView.ViewHolder?, fromX: Int, fromY: Int, toX: Int, toY: Int): Boolean {
//                return true
//            }
//
//            override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {
//                return true
//            }
//
//            override fun animateChange(oldHolder: RecyclerView.ViewHolder?, newHolder: RecyclerView.ViewHolder?, fromLeft: Int, fromTop: Int, toLeft: Int, toTop: Int): Boolean {
//                return true
//            }
//
//        }


    }
    //<editor-fold>

    //<editor-fold desc = "添加滑动监听事件">
    private fun addScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                Log.e("$TAG-2", "onScrollStateChanged:$newState")

                //滑动结束的时候才回调
                //SCROLL_STATE_IDLE         已经停止滑动了
                //SCROLL_STATE_DRAGGING     用户手指还在拖拽滑动
                //SCROLL_STATE_SETTLING     还在依靠惯性滑动

                //当前已经加载的数据量
                val itemCount: Int = adapter!!.itemCount
                //适配器的税局是否展示完了(最后一条可见数据是适配器的最后一条数据)
                val isToAdaperBottom = lastVisiblePosition + 1 == itemCount
                //是否已经停止滑动
                val isStopScroll = RecyclerView.SCROLL_STATE_IDLE == newState
                if (isToAdaperBottom && isStopScroll) {
                    //滑到底部了并且已经停止滑动
                }
                //是否是已经在底部了还在向下滑
                val isStillDragWhenAtBottom = lastScrollDY == 0
                if (isToAdaperBottom && isStopScroll && isStillDragWhenAtBottom) {
                    //到底部了还在向下滑
                }

                lastScrollDY = 0
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                Log.e("$TAG-3", "onScrolled:$dy")

                //滑动过程中不断回调
                //最后一条可见数据的位置(注意的类型转换)
                recyclerView?.apply {
                    if (layoutManager is LinearLayoutManager) {
                        lastVisiblePosition =
                                (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    } else if (layoutManager is GridLayoutManager) {
                        lastVisiblePosition =
                                (layoutManager as GridLayoutManager).findLastVisibleItemPosition()
                    }
                }

                lastScrollDY = dy

                val itemCount: Int = adapter!!.itemCount
                //是否需要加载下一页 //这里如果剩余的数据不足一页就加载下一页
                val needLoadNextPage = (itemCount - (lastVisiblePosition + 1)) < pageSize
                //是否在向下滑
                val isScrollToBottom = lastScrollDY > 0

                Log.e("$TAG-1", "$lastVisiblePosition,$needLoadNextPage,$isScrollToBottom")

                if (needLoadNextPage && isScrollToBottom) {
                    //这里去加载下一页
                    loadMoreData()
                }
            }
        })
    }
    //<editor>
}

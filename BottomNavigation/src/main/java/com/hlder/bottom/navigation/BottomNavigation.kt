package com.hlder.bottom.navigation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout

class BottomNavigation(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    HorizontalScrollView(context, attrs, defStyleAttr) {
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private val contentLayout = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
    }

    // item的选择监听器
    private var onBottomNavigationSelectListener: OnBottomNavigationSelectListener? = null

    // 一屏幕有显示多少个控件，平均分
    private var oneScreenItemNum = 5

    private var bottomNavigationAdapter: BottomNavigationAdapter<out BottomNavigationAdapter.ViewHolder>? =
        null

    // 上一次选中的item
    private var lastIndex = 0

    // 是否在滚动动画中，如果动画中，则禁止touch事件
    private var isAnimScrolling = false

    init {
        overScrollMode = OVER_SCROLL_NEVER // 禁止默认滚动回弹阴影效果
        addView(
            contentLayout, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        )
    }

    /**
     * 设置选中监听器
     */
    fun setOnBottomNavigationSelectListener(onBottomNavigationSelectListener: OnBottomNavigationSelectListener) {
        this.onBottomNavigationSelectListener = onBottomNavigationSelectListener
    }

    /**
     * 滚动到某一个index的位置
     */
    fun scrollToIndex(index: Int) {
        val scrollToX = getIndexScrollX(index)
        animScrollToX(index, scrollToX)
    }

    /**
     * 设置adapter
     */
    fun setAdapter(bottomNavigationAdapter: BottomNavigationAdapter<out BottomNavigationAdapter.ViewHolder>) {
        this.bottomNavigationAdapter = bottomNavigationAdapter
        this.bottomNavigationAdapter?.bottomNavigation = this
    }

    /**
     * 设置一个屏幕一共多少个view
     */
    fun setOnScreenItemNum(num: Int) {
        this.oneScreenItemNum = num
        // 必须是奇数，不然中间item无法在中间,如果是偶数，则加一个空view
        if (this.oneScreenItemNum % 2 != 0) {
            this.oneScreenItemNum++
        }
    }

    /**
     * 禁止惯性滚动
     */
    override fun fling(velocityX: Int) {
    }

    /**
     * 动画，滚动到对应位置
     */
    private fun animScrollToX(index: Int, scrollToX: Int) {
        isAnimScrolling = true
        val xTranslate = ObjectAnimator.ofInt(this, "scrollX", scrollToX)
        val animators = AnimatorSet()
        animators.duration = 200L
        animators.playTogether(xTranslate)
        animators.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
            }
            override fun onAnimationEnd(animation: Animator?) {
                isAnimScrolling = false
                onBottomNavigationSelectListener?.invoke(index)
            }
            override fun onAnimationCancel(animation: Animator?) {
                isAnimScrolling = false
                onBottomNavigationSelectListener?.invoke(index)
            }
            override fun onAnimationRepeat(animation: Animator?) {
            }
        })
        animators.start()
    }

    /**
     * scrollView的touch事件
     */
    override fun onTouchEvent(e: MotionEvent?): Boolean {
        if (!isAnimScrolling) {
            val action = e?.action
            if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
                scrollToIndex(getNowIndex()) //手拿开，滚动到对应index的中间位置
            }
            changeIndexUi(getNowIndex()) // 改变index的UI，比如字体颜色等
        }
        return super.onTouchEvent(e)
    }

    /**
     * 获取当前滚动位置的index下标
     */
    private fun getNowIndex(): Int {
        val itemWidth = width / oneScreenItemNum

        var selectIndex = (scrollX + itemWidth / 2) / itemWidth

        if (selectIndex < 0) { // 小于0则设置为0
            selectIndex = 0
        }
        val size = bottomNavigationAdapter?.getItemCount() ?: 0
        if (selectIndex >= size) { // 超过了则设置最大的
            selectIndex = size - 1
        }
        return selectIndex
    }

    /**
     * 通过index获取scrollX的值
     */
    private fun getIndexScrollX(index: Int): Int {
        val itemWidth = width / oneScreenItemNum
        return index * itemWidth
    }

    /**
     * 滚动至此，改变该index的UI，比如改变字体颜色,大小等
     */
    private fun changeIndexUi(index: Int) {
        bottomNavigationAdapter?.let {
            // 隐藏上一次显示的
            it.disShowItem(lastIndex)
            // 显示这一次
            it.showItem(index)
            lastIndex = index
        }
    }

    /**
     * 刷新子控件
     */
    internal fun refreshChild() {
        if (width > 0) {
            contentLayout.removeAllViews() // 移除所有的view
            addNullView() // 前面的填充
            bottomNavigationAdapter?.let {
                for (i in 0 until it.getItemCount()) {
                    addItemView(it.getViewHolder(i).view)
                }
            }
            addNullView() // 后面的填充
        }
    }

    /**
     * 添加Item
     */
    private fun addItemView(view: View) {
        val itemWidth = width / oneScreenItemNum
        val itemLayoutParams = LinearLayout.LayoutParams(
            itemWidth,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        contentLayout.addView(view, itemLayoutParams)
    }

    /**
     * 填充前后的空View
     */
    private fun addNullView() {
        val itemWidth = width / oneScreenItemNum
        val nullLayoutParams = LinearLayout.LayoutParams(
            itemWidth, 0
        )
        val numViewNum = oneScreenItemNum / 2
        for (i in 1..numViewNum) {
            contentLayout.addView(View(context), nullLayoutParams)
        }
    }

    /**
     * 初始化的时候无法获得width,无法计算
     * 所以等待布局渲染到layout树上，计算出宽度后再去刷新item
     */
    private var isShowed = false
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (!isShowed && width > 0) {
            refreshChild()
            changeIndexUi(getNowIndex())
            isShowed = true
        }
    }
}

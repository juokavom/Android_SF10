package com.sf10.android.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.sf10.android.R
import com.sf10.android.databinding.ActivityGameBinding
class GameActivity : BaseActivity() {
    private lateinit var binding: ActivityGameBinding
    private val playersListViewList = mutableListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createCircularViews()

        this.binding.button2.setOnClickListener {
            this.playersListViewList[2].text = "edited"
        }
    }

    private fun createCircularViews(){
        val n = 4
        val angleIncrement: Float = 360f/n

        for(i in 1..n){
            val tv: TextView = TextView(this@GameActivity)
            tv.id = View.generateViewId()
            tv.text = "Text Field $i"

            val circularView = createCircularView(tv, 50, 50, 120, angleIncrement, i)

            playersListViewList.add(circularView)
            binding.gameLayout.addView(circularView)
        }
    }

    private fun createCircularView(view: TextView, width: Int, height: Int, radius: Int, angleIncrement: Float, place: Int): TextView{
        val layout = ConstraintLayout.LayoutParams(
            width.toPx(),
            height.toPx()
        )
        layout.circleRadius = radius.toPx()
        layout.circleConstraint = R.id.gameRoomCenter
        layout.circleAngle = (place * angleIncrement)
        layout.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layout.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        layout.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        layout.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        view.layoutParams = layout
        return view
    }


    private fun Int.toPx(): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(), resources.displayMetrics
        ).toInt()
}
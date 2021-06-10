package com.sf10.android.activities

import android.icu.util.Measure
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.sf10.android.R
import com.sf10.android.databinding.ActivityGameBinding
class GameActivity : BaseActivity() {
    private lateinit var binding: ActivityGameBinding
    private val playersListViewList = mutableListOf<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createCircularViews()

    }

    private fun createCircularViews(){
        val n = 4
        val angleIncrement: Float = 360f/n

        for(i in 1..n){
            val tv: View = LayoutInflater.from(this).inflate(R.layout.item_public_player_game, null)
            tv.id = View.generateViewId()
            tv.findViewById<TextView>(R.id.tvNameGame).text = "Name $i"

            val circularView = createCircularView(tv, 120, angleIncrement, i)

            playersListViewList.add(circularView)
            binding.gameLayout.addView(circularView)
        }
    }

    private fun createCircularView(view: View, radius: Int, angleIncrement: Float, place: Int): View{
        val layout = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
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
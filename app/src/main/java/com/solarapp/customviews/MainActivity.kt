package com.solarapp.customviews

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.solarapp.customviews.seekbar.BarCodeSeekbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        seekbar.setOnScrollListener(object : BarCodeSeekbar.OnScrollListener {
            override fun onStartScroll(seekbar: BarCodeSeekbar, value: Int) {
                tvProgress.text = value.toString()
                tvLeft.text = seekbar.getTickLeft().toString()
                tvRight.text = seekbar.getTickRight().toString()
            }

            override fun onScroll(seekbar: BarCodeSeekbar, value: Int, b: Boolean) {
                tvProgress.text = value.toString()
                tvLeft.text = seekbar.getTickLeft().toString()
                tvRight.text = seekbar.getTickRight().toString()
            }

            override fun onFling(seekbar: BarCodeSeekbar, value: Int) {

            }

            override fun onScrollFinished(seekbar: BarCodeSeekbar) {

            }

        })
    }
}

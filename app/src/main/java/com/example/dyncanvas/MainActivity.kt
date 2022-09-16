package com.example.dyncanvas
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {


    override fun onClick(p0: View?) {
        when (p0?.id) {

            R.id.smallCanvas -> startARactivity(1)

            R.id.largeCanvas -> startARactivity(2)
        }
    }

    private fun startARactivity(i: Int) {
        val intent = Intent(this,ARCanvasActivity::class.java)
        intent.putExtra("order", i)
        startActivity(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        clickListeners()

    }

    private fun clickListeners() {
        smallCanvas.setOnClickListener(this)
        largeCanvas.setOnClickListener(this)
    }

}
package com.example.galervladapplication

import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.MenuItem
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_show_photo.*
import java.io.File


class ShowPhotoActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

    private lateinit var toolbar: Toolbar
    private var arrayPhotosGenerally = ArrayList<String>()
    private lateinit var file: File
    private var sizeMax = 0
    private var localIndex = 0
    private var time: Long = 0
    private var methodSwipe = 0
    private lateinit var mGestureDetector: GestureDetector
    private val SWIPE_MIN_DISTANCE = 120
    private val SWIPE_MAX_OFF_PATH = 250
    private val SWIPE_THRESHOLD_VELOCITY = 100
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_photo)
        initToolbar()
        arrayPhotosGenerally =
            intent?.getSerializableExtra(Constants.ARRAY_IMAGE) as ArrayList<String>
        time = intent.getLongExtra(Constants.TIME_SELECT, 5000)
        methodSwipe = intent.getIntExtra(Constants.METHOD, 0)
        sizeMax = arrayPhotosGenerally.size - 1
        startShow()
    }

    private fun startShow() {
        val slideInLeftAnimation: Animation =
            AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        val slideOutRight: Animation =
            AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right)

        imageSwitch.setInAnimation(slideInLeftAnimation);
        imageSwitch.setOutAnimation(slideOutRight);

        imageSwitch.setFactory({
            val imageView = ImageView(applicationContext)
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            imageView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.FILL_PARENT
            )
            imageView
        })

        if (methodSwipe == Constants.TIMER) {
            imageSwitch.postDelayed(object : Runnable {
                override fun run() {
                    localIndex = (0..sizeMax).random()
                    imageSwitch.setImageURI(
                        Uri.fromFile(File(arrayPhotosGenerally.get(localIndex)))
                    )
                    imageSwitch.postDelayed(this, time)
                }
            }, time)
            file = File(arrayPhotosGenerally.get(localIndex))
            imageSwitch.setImageURI(Uri.fromFile(file))
        } else {
            mGestureDetector = GestureDetector(this, this)
            localIndex = (0..sizeMax).random()
            imageSwitch.setImageURI(Uri.fromFile(File(arrayPhotosGenerally.get(localIndex))))
        }
    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.TitleShowActivity)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return mGestureDetector.onTouchEvent(event)
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        try {
            if (Math.abs(e1!!.getY() - e2!!.getY()) > SWIPE_MAX_OFF_PATH) return false
            // справа налево
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
            ) {
                localIndex = (0..sizeMax).random()
                imageSwitch.setImageURI(Uri.fromFile(File(arrayPhotosGenerally.get(localIndex))))
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
            ) {
                // слева направо
                localIndex = (0..sizeMax).random()
                imageSwitch.setImageURI(Uri.fromFile(File(arrayPhotosGenerally.get(localIndex))))
            }
        } catch (e: Exception) {
            return true
        }
        return true
    }

    override fun onShowPress(p0: MotionEvent?) {
    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        return false
    }

    override fun onDown(p0: MotionEvent?): Boolean {
        return false
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        return false
    }

    override fun onLongPress(p0: MotionEvent?) {
    }
}
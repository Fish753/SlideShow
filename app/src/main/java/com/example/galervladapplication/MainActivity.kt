package com.example.galervladapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.galervladapplication.databinding.ActivityMainBinding
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var buttonLoading: Button
    private var methodSwipe = Constants.GESTURE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initToolbar()
        initButtonLoading()
        initListenerConstrait()
        initSwitchListener()
    }

    private fun initSwitchListener() {
        switchmultibutton.setOnSwitchListener({ position: Int, tabText: String ->
            if (tabText == resources.getStringArray(R.array.switch_tabs)[0]) {
                methodSwipe = Constants.GESTURE
            } else {
                methodSwipe = Constants.TIMER
            }
        })
    }

    private fun initButtonLoading() {
        buttonLoading = mBinding.buttonLoading
        buttonLoading.setOnClickListener {
            checkPermissionStorage()
        }
    }

    private fun initListenerConstrait() {
        costraitLayout.setOnClickListener {
            if (editTextPhone.isFocused) {
                editTextPhone.clearFocus()
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editTextPhone.getWindowToken(), 0)
            }
        }
    }

    private fun checkPermissionStorage() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Constants.REQUEST_CODE_PERMISSION_READ_PHOTO
                )
            }
        } else {
            createFilePickerBuilder()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == Constants.REQUEST_CODE_PERMISSION_READ_PHOTO) {
            if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                createFilePickerBuilder()
            }
        }
    }

    fun createFilePickerBuilder() {
        FilePickerBuilder.instance
            .enableSelectAll(true)
            .setSelectedFiles(java.util.ArrayList())
            .setActivityTheme(R.style.LibAppTheme)
            .pickPhoto(this)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO) {
                val docPaths = ArrayList<String>()
                docPaths.addAll(
                    Objects.requireNonNull<ArrayList<String>>(
                        data?.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)
                    )
                )
                if (docPaths.isEmpty()) {
                    showToast(getString(R.string.photo_not_selected))
                } else {
                    val intent = Intent(this, ShowPhotoActivity::class.java)
                    if (editTextPhone.text?.isEmpty() == true) {
                        intent.putExtra(Constants.TIME_SELECT, 5000)
                    } else {
                        intent.putExtra(
                            Constants.TIME_SELECT,
                            editTextPhone.text.toString().toLong() * 1000
                        )
                    }
                    intent.putExtra(Constants.METHOD, methodSwipe)
                    intent.putExtra(Constants.ARRAY_IMAGE, docPaths)
                    startActivity(intent)
                }
            }
        }
    }

    private fun initToolbar() {
        toolbar = mBinding.toolbar
        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.title_main_toolbar)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
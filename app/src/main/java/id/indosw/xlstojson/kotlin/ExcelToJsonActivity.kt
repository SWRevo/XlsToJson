package id.indosw.xlstojson.kotlin

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import id.indosw.crashreport.Crasher
import id.indosw.crashreport.Crasher.OnCrashListener
import id.indosw.fileutil.FileUtil.convertUriToFilePath
import id.indosw.jsonviewer.JsonViewer

import id.indosw.xlstojson.kotlin.xlstojsonstring.ExcelConverter

import id.indosw.xlstojson.R
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class ExcelToJsonActivity : AppCompatActivity(), OnCrashListener {
    private var jsonViewer: JsonViewer? = null
    private val pick = Intent(Intent.ACTION_GET_CONTENT)
    private var pathFileText: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exceljson)
        val crasher = Crasher(this)
        crasher.addListener(this)
        crasher.email = "luthfi.otoclash@gmail.com"
        //crasher.setForceStackOverflow(true);
        crasher.isCrashActivityEnabled = true
        initView()
        initLogic()
    }

    private fun initLogic() {
        pick.type = "*/*"
        pick.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        val button = findViewById<Button>(R.id.pickFile)
        pathFileText = findViewById(R.id.pathFileText)
        jsonViewer = findViewById(R.id.jsonViewer)
        button.setOnClickListener { pickDocClicked() }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun pickDocClicked() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                1000
            )
        } else {
            try {
                openSomeActivityForResult()
            } catch (e: Exception) {
                pathFileText!!.text = e.message
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            try {
                openSomeActivityForResult()
            } catch (e: Exception) {
                pathFileText!!.text = e.message
            }
        }
    }

    private fun taskConvertToJson(file: String?) {
        try {
            val converter = ExcelConverter()
            val sheet = converter.loadExcel(file)
            val json = converter.excelToJson(sheet, "S")
            println(json)
            showJsonToView(json)
        } catch (e: Exception) {
            e.printStackTrace()
            pathFileText!!.text = e.message
        }
    }

    @Suppress("DEPRECATION")
    private fun showJsonToView(json: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            jsonViewer!!.setTextColorBool(applicationContext.getColor(R.color.purple_200))
            jsonViewer!!.setTextColorNull(applicationContext.getColor(R.color.light_red))
            jsonViewer!!.setTextColorNumber(applicationContext.getColor(R.color.teal_200))
            jsonViewer!!.setTextColorString(applicationContext.getColor(R.color.jsonViewer_textColorString))
        } else {
            jsonViewer!!.setTextColorBool(resources.getColor(R.color.purple_200))
            jsonViewer!!.setTextColorNull(resources.getColor(R.color.light_red))
            jsonViewer!!.setTextColorNumber(resources.getColor(R.color.teal_200))
            jsonViewer!!.setTextColorString(resources.getColor(R.color.jsonViewer_textColorString))
        }
        try {
            //jsonViewer.setJson(new JSONObject(json));
            jsonViewer!!.setJson(JSONArray(json))
        } catch (e: JSONException) {
            e.printStackTrace()
            pathFileText!!.text = e.message
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickExpend(view: View?) {
        jsonViewer!!.expandJson()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickCollapse(view: View?) {
        jsonViewer!!.collapseJson()
    }

    override fun onCrash(thread: Thread, throwable: Throwable) {}

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    private var someActivityResultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            // There are no request codes
            val filePath = ArrayList<String?>()
            if (data != null) {
                if (data.clipData != null) {
                    for (index in 0 until data.clipData!!.itemCount) {
                        val item = data.clipData!!.getItemAt(index)
                        filePath.add(convertUriToFilePath(applicationContext, item.uri))
                    }
                } else {
                    filePath.add(convertUriToFilePath(applicationContext, data.data!!))
                }
            }
            pathFileText!!.text = filePath[0]
            taskConvertToJson(filePath[0])
        }
    }

    private fun openSomeActivityForResult() {
        someActivityResultLauncher.launch(pick)
    }
}
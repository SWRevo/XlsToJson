package id.indosw.xlstojson.kt

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import id.indosw.fileutil.FileUtil
import id.indosw.xlstojson.R
import java.util.*

@Suppress("PrivatePropertyName")
class MainKtActivity : Activity() {
    private val REQ_CD_PICK = 101
    private val pick = Intent(Intent.ACTION_GET_CONTENT)
    private var editText: EditText? = null
    private var pathFileText: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initLogic()
    }

    private fun initLogic() {
        pick.type = "*/*"
        pick.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    }

    private fun taskConvertToJson(file: String?) {
        try {
            val converter = ExcelConverter()
            val sheet = converter.loadExcel(file)
            val json = converter.excelToJson(sheet, "S")
            println(json)
            editText!!.setText(json)
            //converter.saveJson("pathSave", json);
        } catch (e: Exception) {
            e.printStackTrace()
            editText!!.setText(e.message)
        }
    }

    private fun initView() {
        val button = findViewById<Button>(R.id.pickFile)
        editText = findViewById(R.id.responseText)
        pathFileText = findViewById(R.id.pathFileText)
        button.setOnClickListener { pickDocClicked() }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun pickDocClicked() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            } else {
                try {
                    startActivityForResult(pick, REQ_CD_PICK)
                } catch (e: Exception) {
                    editText!!.setText(e.message)
                }
            }
        } else {
            try {
                startActivityForResult(pick, REQ_CD_PICK)
            } catch (e: Exception) {
                editText!!.setText(e.message)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            try {
                startActivityForResult(pick, REQ_CD_PICK)
            } catch (e: Exception) {
                editText!!.setText(e.message)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CD_PICK) {
            if (resultCode == RESULT_OK) {
                val filePath = ArrayList<String?>()
                if (data.clipData != null) {
                    for (index in 0 until data.clipData!!.itemCount) {
                        val item = data.clipData!!.getItemAt(index)
                        filePath.add(FileUtil.convertUriToFilePath(applicationContext, item.uri))
                    }
                } else {
                    filePath.add(FileUtil.convertUriToFilePath(applicationContext, data.data!!))
                }
                pathFileText!!.text = filePath[0]
                taskConvertToJson(filePath[0])
            }
        }
    }
}
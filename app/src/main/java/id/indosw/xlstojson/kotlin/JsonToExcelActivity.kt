@file:Suppress("SpellCheckingInspection")

package id.indosw.xlstojson.kotlin

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider

import id.indosw.crashreport.Crasher
import id.indosw.crashreport.Crasher.OnCrashListener
import id.indosw.xlstojson.R

import id.indosw.xlstojson.kotlin.jsonstringtoxls.ConvertJsonToExcel
import id.indosw.xlstojson.kotlin.jsonstringtoxls.Customer
import java.io.File
import java.io.IOException

class JsonToExcelActivity : AppCompatActivity(), OnCrashListener {
    private var jsonStringData: EditText? = null
    private var pathFileText: TextView? = null
    private var customers: List<Customer>? = null
    private var jsonStr: String? = null
    private var openExcelFile: ImageView? = null
    private var pathExcel: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jsonexcel)
        val crasher = Crasher(this)
        crasher.addListener(this)
        crasher.email = "luthfi.otoclash@gmail.com"
        //crasher.setForceStackOverflow(true);
        crasher.isCrashActivityEnabled = true
        initView()
        initLogic()
    }

    private fun initLogic() {
        jsonStr =
            "[{\"id\":\"1\",\"name\":\"Ravishankar Kumar\",\"address\":\"Bangalore\",\"age\":32},{\"id\":\"2\",\"name\":\"Chulbul Yadav\",\"address\":\"Motihari\",\"age\":27},{\"id\":\"3\",\"name\":\"Mirchae Devi\",\"address\":\"Buxcer\",\"age\":26},{\"id\":\"4\",\"name\":\"Deepak Jha\",\"address\":\"Mithila\",\"age\":33},{\"id\":\"5\",\"name\":\"Shilpa Rani\",\"address\":\"Nala Road\",\"age\":36}]"
        jsonStringData!!.setText(jsonStr)
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        val convertJsonToXls = findViewById<Button>(R.id.convertJsonToXls)
        jsonStringData = findViewById(R.id.jsonStringData)
        pathFileText = findViewById(R.id.pathFileText)
        openExcelFile = findViewById(R.id.openExcelFile)
        convertJsonToXls.setOnClickListener {
            if (jsonStringData!!.text.toString().isEmpty()) {
                Toast.makeText(applicationContext, "Can Not Empty Data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
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
                pathFileText!!.text = "Ready Convert"
                runConvertJsonToXls(jsonStringData!!.text.toString())
            }
        }
        openExcelFile!!.setOnClickListener {
            //Uri pathUri = Uri.fromFile(new File(pathExcel));
            if (pathExcel!!.isEmpty()) {
                return@setOnClickListener
            }
            val filePath = File(pathExcel!!)
            val pathUri = FileProvider.getUriForFile(
                this@JsonToExcelActivity,
                applicationContext.packageName + ".provider",
                filePath
            )
            openFile(pathUri)
        }
    }

    @SuppressLint("SdCardPath", "SetTextI18n")
    private fun runConvertJsonToXls(s: String) {
        try {
            customers = ConvertJsonToExcel.convertJsonString2Objects(s)
        } catch (e: Exception) {
            pathFileText!!.text = e.message
        }
        jsonStringData!!.setText(customers.toString())
        pathExcel = "/storage/emulated/0/Download/customers.xlsx"
        try {
            ConvertJsonToExcel.writeObjects2ExcelFile(customers!!, pathExcel)
            pathFileText!!.text = "Success generate and save to : $pathExcel"
            jsonStringData!!.setText(jsonStr)
            openExcelFile!!.visibility = View.VISIBLE
        } catch (e: IOException) {
            e.printStackTrace()
            pathFileText!!.text = e.message
            jsonStringData!!.setText(jsonStr)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            pathFileText!!.text = "Ready Convert"
            runConvertJsonToXls(jsonStringData!!.text.toString())
        }
    }

    override fun onCrash(thread: Thread, throwable: Throwable) {}
    fun openFile(uri: Uri) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        if (uri.toString().contains(".doc") || uri.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword")
        } else if (uri.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf")
        } else if (uri.toString().contains(".ppt") || uri.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint")
        } else if (uri.toString().contains(".xls") || uri.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel")
        } else if (uri.toString().contains(".zip") || uri.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav")
        } else if (uri.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf")
        } else if (uri.toString().contains(".wav") || uri.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav")
        } else if (uri.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif")
        } else if (uri.toString().contains(".jpg") || uri.toString()
                .contains(".jpeg") || uri.toString().contains(".png")
        ) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg")
        } else if (uri.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain")
        } else if (uri.toString().contains(".3gp") || uri.toString()
                .contains(".mpg") || uri.toString().contains(".mpeg") || uri.toString()
                .contains(".mpe") || uri.toString().contains(".mp4") || uri.toString()
                .contains(".avi")
        ) {
            // Video files
            intent.setDataAndType(uri, "video/*")
        } else {
            // Other files
            intent.setDataAndType(uri, "*/*")
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }
}
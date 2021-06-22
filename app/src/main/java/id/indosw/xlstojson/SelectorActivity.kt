@file:Suppress("UNUSED_PARAMETER")

package id.indosw.xlstojson

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import id.indosw.xlstojson.java.ExcelToJson2Activity
import id.indosw.xlstojson.java.JsonToExcelActivity

class SelectorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector)
    }

    fun goJsonToExcel(view: View?) {
        val intentJsonToXLS = Intent()
        intentJsonToXLS.setClass(applicationContext, JsonToExcelActivity::class.java)
        startActivity(intentJsonToXLS)
    }

    fun goExcelToJson(view: View?) {
        val intentXlsToJson = Intent()
        intentXlsToJson.setClass(applicationContext, ExcelToJson2Activity::class.java)
        startActivity(intentXlsToJson)
    }
}
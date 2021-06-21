@file:Suppress("UNUSED_PARAMETER")

package id.indosw.xlstojson

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import id.indosw.xlstojson.java.ExcelToJsonActivity
import id.indosw.xlstojson.java.JsonToExcelActivity

class SelectorActivity : Activity() {
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
        intentXlsToJson.setClass(applicationContext, ExcelToJsonActivity::class.java)
        startActivity(intentXlsToJson)
    }
}
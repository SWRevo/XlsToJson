package id.indosw.xlstojson.kotlin.xlstojsonstring

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

object ConvertExcelToJson {
    fun readExcelFile(filePath: String?): List<Customer> {
        return try {
            val excelFile = FileInputStream(File(filePath!!))
            val workbook: Workbook = XSSFWorkbook(excelFile)
            val sheet = workbook.getSheet("Customers")
            val rows: Iterator<Row> = sheet.iterator()
            val lstCustomers: MutableList<Customer> = ArrayList()
            var rowNumber = 0
            while (rows.hasNext()) {
                val currentRow = rows.next()

                // skip header
                if (rowNumber == 0) {
                    rowNumber++
                    continue
                }
                val cellsInRow: Iterator<Cell> = currentRow.iterator()
                val cust = Customer()
                var cellIndex = 0
                while (cellsInRow.hasNext()) {
                    val currentCell = cellsInRow.next()
                    when (cellIndex) {
                        0 -> { // ID
                            cust.id = currentCell.numericCellValue.toString()
                        }
                        1 -> { // Name
                            cust.name = currentCell.stringCellValue
                        }
                        2 -> { // Address
                            cust.address = currentCell.stringCellValue
                        }
                        3 -> { // Age
                            cust.age = currentCell.numericCellValue.toInt()
                        }
                    }
                    cellIndex++
                }
                lstCustomers.add(cust)
            }

            // Close WorkBook
            workbook.close()
            lstCustomers
        } catch (e: IOException) {
            throw RuntimeException("FAIL! -> message = " + e.message)
        }
    }

    fun convertObjects2JsonString(customers: List<Customer?>?): String {
        val mapper = ObjectMapper()
        var jsonString = ""
        try {
            jsonString = mapper.writeValueAsString(customers)
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }
        return jsonString
    }
}
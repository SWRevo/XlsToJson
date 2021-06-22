package id.indosw.xlstojson.kotlin.jsonstringtoxls

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import java.io.IOException

@Suppress("LocalVariableName", "SpellCheckingInspection")
object ConvertJsonToExcel {
    @Suppress("LocalVariableName")
    fun convertJsonString2Objects(jsonString: String?): List<Customer?>? {
        var customers: List<Customer?>? = null
        @Suppress("LocalVariableName")
        try {
            customers =
                ObjectMapper().readValue(jsonString, object : TypeReference<List<Customer?>?>() {})
        } catch (e: JsonParseException) {
            e.printStackTrace()
        } catch (e: JsonMappingException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return customers
    }

    @Throws(IOException::class)
    fun writeObjects2ExcelFile(customers: List<Customer>, filePath: String?) {
        val COLUMNs = arrayOf("Id", "Name", "Address", "Age")
        val workbook: Workbook = XSSFWorkbook()
        val createHelper = workbook.creationHelper
        val sheet = workbook.createSheet("Customers")
        val headerFont = workbook.createFont()
        headerFont.bold = true
        headerFont.color = IndexedColors.BLUE.getIndex()
        val headerCellStyle = workbook.createCellStyle()
        headerCellStyle.setFont(headerFont)
        // Row for Header
        val headerRow = sheet.createRow(0)
        // Header
        for (col in COLUMNs.indices) {
            val cell = headerRow.createCell(col)
            cell.setCellValue(COLUMNs[col])
            cell.cellStyle = headerCellStyle
        }
        // CellStyle for Age
        val ageCellStyle = workbook.createCellStyle()
        ageCellStyle.dataFormat = createHelper.createDataFormat().getFormat("#")
        var rowIdx = 1
        for (customer in customers) {
            val row = sheet.createRow(rowIdx++)
            row.createCell(0).setCellValue(customer.id)
            row.createCell(1).setCellValue(customer.name)
            row.createCell(2).setCellValue(customer.address)
            val ageCell = row.createCell(3)
            ageCell.setCellValue(customer.age.toDouble())
            ageCell.cellStyle = ageCellStyle
        }
        val fileOut = FileOutputStream(filePath)
        workbook.write(fileOut)
        fileOut.close()
        workbook.close()
    }
}
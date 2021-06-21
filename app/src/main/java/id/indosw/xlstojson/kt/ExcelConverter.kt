package id.indosw.xlstojson.kt

import android.annotation.TargetApi
import android.os.Build
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.CellType
import java.io.*
import java.util.*

class ExcelConverter {
    private fun getFormat(type: String?): String {
        return when (type) {
            "STRING" -> {
                FORMAT_STRING
            }
            "NUMERIC" -> {
                FORMAT_VALUE
            }
            "BOOLEAN" -> {
                FORMAT_VALUE
            }
            else -> FORMAT_STRING
        }
    }

    private fun getCellAsString(cell: HSSFCell?): String? {
        var value: String? = null
        if (cell != null) {
            if (cell.cellTypeEnum == CellType.NUMERIC) {
                cell.setCellType(CellType.STRING)
            }
            value = cell.stringCellValue
        }
        return value
    }

    private fun getMaxRow(sheet: HSSFSheet?): Int {
        var maxRow = 0
        if (sheet != null) {
            maxRow = sheet.lastRowNum
            for (i in 0..maxRow) {
                val row = sheet.getRow(i)
                if (row != null) {
                    val value = getCellAsString(row.getCell(0))
                    if ("EndOfFile" == value) {
                        maxRow = i
                        break
                    }
                }
            }
        }
        return maxRow
    }

    private fun getMaxCol(sheet: HSSFSheet?): Int {
        var maxCol = 0
        if (sheet != null) {
            val row = sheet.getRow(0)
            if (row != null) {
                maxCol = row.lastCellNum.toInt()
                for (i in 0..maxCol) {
                    val value = getCellAsString(row.getCell(i))
                    if ("EndOfFile" == value) {
                        maxCol = i
                        break
                    }
                }
            }
        }
        return maxCol
    }

    /**@SuppressWarnings("SimplifiableConditionalExpression")
     * private boolean checkRow(HSSFCell cell) {
     * return cell != null ? "1".equals(getCellAsString(cell)) : false;
     * }
     * @SuppressWarnings("SimplifiableConditionalExpression")
     * private boolean checkCell(String platform, HSSFCell cell) {
     * return cell != null
     * ? "CS".equals(getCellAsString(cell)) || platform.equals(getCellAsString(cell))
     * : false;
     * }
     */
    private fun checkRow(cell: HSSFCell?): Boolean {
        return cell != null && "1" == getCellAsString(cell)
    }

    private fun checkCell(platform: String, cell: HSSFCell?): Boolean {
        return cell != null && ("CS" == getCellAsString(cell) || platform == getCellAsString(cell))
    }

    private fun parse(fieldType: String?, fieldName: String?, fieldValue: String?): String {
        val format = getFormat(fieldType)
        return String.format(format, fieldName, fieldValue)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun analyzer(sheet: HSSFSheet?, platform: String, maxRow: Int, maxCol: Int): String? {
        if (sheet != null) {
            val jsons: MutableList<String> = ArrayList()
            val fieldNameRow = sheet.getRow(FIELD_FIELDNAME_ROW)
            for (i in FIELD_DATA_ROW until maxRow) {
                val dataRow = sheet.getRow(i)
                if (checkRow(dataRow.getCell(0))) {
                    val fields: MutableList<String?> = ArrayList()
                    for (j in FIELD_DATA_COL until maxCol) {
                        val fieldTypeRow = sheet.getRow(FIELD_FIELDTYPE_ROW)
                        val platformRow = sheet.getRow(FIELD_PLATFORM_ROW)
                        if (checkCell(platform, platformRow.getCell(j))) {
                            val fieldType = getCellAsString(fieldTypeRow.getCell(j))
                            val fieldName = getCellAsString(fieldNameRow.getCell(j))
                            val fieldValue = getCellAsString(dataRow.getCell(j))
                            fields.add(parse(fieldType, fieldName, fieldValue))
                        }
                    }
                    if (fields.size > 0) {
                        jsons.add(String.format("{%s}", java.lang.String.join(",", fields)))
                    }
                }
            }
            if (jsons.size > 0) {
                return String.format(
                    "[%s]",
                    java.lang.String.join(",", jsons)
                )
            }
        }
        return null
    }

    fun excelToJson(sheet: HSSFSheet?, platform: String): String? {
        val maxRow = getMaxRow(sheet)
        val maxCol = getMaxCol(sheet)
        return analyzer(sheet, platform, maxRow, maxCol)
    }

    @Throws(IOException::class)
    fun loadExcel(path: String?): HSSFSheet {
        val fileSystem = POIFSFileSystem(FileInputStream(path))
        val workbook = HSSFWorkbook(fileSystem)
        return workbook.getSheet("Sheet1")
    }

    @Throws(IOException::class)
    fun loadExcel(file: File?): HSSFSheet {
        val fileSystem = POIFSFileSystem(FileInputStream(file))
        val workbook = HSSFWorkbook(fileSystem)
        return workbook.getSheet("Sheet1")
    }

    @Throws(IOException::class)
    fun saveJson(path: String?, json: String?) {
        val bw = BufferedWriter(FileWriter(path))
        bw.write(json)
        bw.close()
    }

    companion object {
        private const val FIELD_READNAME_ROW = 0
        private const val FIELD_FIELDNAME_ROW = 1
        private const val FIELD_FIELDTYPE_ROW = 2
        private const val FIELD_PLATFORM_ROW = 3
        private const val FIELD_DATA_ROW = 4
        private const val FIELD_DATA_COL = 1
        private const val FORMAT_STRING = "\"%s\":\"%s\""
        private const val FORMAT_VALUE = "\"%s\":%s"
    }
}
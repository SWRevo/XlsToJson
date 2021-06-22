# XlsToJson
This sample implementation convert XLS to JSON


![alt text](https://firebasestorage.googleapis.com/v0/b/jasauploadplaystore.appspot.com/o/Screenshot_2021-06-22-12-24-53-479_id.indosw.xlstojson.jpg?alt=media&token=99cf41dd-0b3a-48c7-b72d-36b26c090061 "Screenshot1")


![alt text](https://firebasestorage.googleapis.com/v0/b/jasauploadplaystore.appspot.com/o/Screenshot_2021-06-22-12-23-56-439_id.indosw.xlstojson.jpg?alt=media&token=b625db7b-7473-4392-9f94-5a489c9b848e "Screenshot2")

# Convert JSON STRING to EXCEL File

```
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
```

# Convert EXCEL File to JSON STRING


```
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

    private fun taskConvertToJson() {
        if (customers!!.isNotEmpty()) {
            try {
                // Step 2: Convert Java Objects to JSON String
                val jsonString = ConvertExcelToJson.convertObjects2JsonString(customers)
                println(jsonString)
                showJsonToView(jsonString)
            } catch (e: Exception) {
                e.printStackTrace()
                pathFileText!!.text = e.message
            }
        }
    }

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
            try {
                // Step 1: Read Excel File into Java List Objects
                customers = ConvertExcelToJson.readExcelFile(
                    filePath[0]
                )
                taskConvertToJson()
            } catch (e: Exception) {
                e.printStackTrace()
                pathFileText!!.text = e.message
            }
        }
    }

    private fun openSomeActivityForResult() {
        someActivityResultLauncher.launch(pick)
    }


```

## License

    Copyright 2021 indosw

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.




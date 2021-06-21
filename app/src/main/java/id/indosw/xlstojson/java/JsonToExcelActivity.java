package id.indosw.xlstojson.java;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import id.indosw.crashreport.Crasher;
import id.indosw.xlstojson.R;
import id.indosw.xlstojson.jsonstringtoxls.ConvertJsonToExcel;
import id.indosw.xlstojson.jsonstringtoxls.Customer;

public class JsonToExcelActivity extends Activity implements Crasher.OnCrashListener {

    private EditText jsonStringData;
    private TextView pathFileText;
    private List<Customer> customers;

    private String jsonStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jsonexcel);

        Crasher crasher = new Crasher(this);
        crasher.addListener(this);
        crasher.setEmail("luthfi.otoclash@gmail.com");
        //crasher.setForceStackOverflow(true);
        crasher.setCrashActivityEnabled(true);

        initView();
        initLogic();
    }

    private void initLogic() {
        jsonStr = "[{\"id\":\"1\",\"name\":\"Ravishankar Kumar\",\"address\":\"Bangalore\",\"age\":32},{\"id\":\"2\",\"name\":\"Chulbul Yadav\",\"address\":\"Motihari\",\"age\":27},{\"id\":\"3\",\"name\":\"Mirchae Devi\",\"address\":\"Buxcer\",\"age\":26},{\"id\":\"4\",\"name\":\"Deepak Jha\",\"address\":\"Mithila\",\"age\":33},{\"id\":\"5\",\"name\":\"Shilpa Rani\",\"address\":\"Nala Road\",\"age\":36}]";
        jsonStringData.setText(jsonStr);
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        Button convertJsonToXls = findViewById(R.id.convertJsonToXls);
        jsonStringData = findViewById(R.id.jsonStringData);
        pathFileText = findViewById(R.id.pathFileText);
        convertJsonToXls.setOnClickListener(v -> {
            if (jsonStringData.toString().equals("")){
                Toast.makeText(getApplicationContext(),"Can Not Empty Data",Toast.LENGTH_SHORT).show();
                return;
            }

            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 1000);
                }
                else {
                    pathFileText.setText("Ready Convert");
                    runConvertJsonToXls(jsonStringData.toString());
                }
            }
            else {
                pathFileText.setText("Ready Convert");
                runConvertJsonToXls(jsonStringData.toString());
            }
        });
    }

    @SuppressLint({"SdCardPath", "SetTextI18n"})
    private void runConvertJsonToXls(String s) {
        try { customers = ConvertJsonToExcel.convertJsonString2Objects(s);
        } catch (Exception e){pathFileText.setText(e.getMessage());}
        jsonStringData.setText(customers.toString());

        String pathExcel = "/storage/emulated/0/Download/customers.xlsx";

        try { ConvertJsonToExcel.writeObjects2ExcelFile(customers, pathExcel);
            pathFileText.setText("Success generate and save to : " + pathExcel);
            jsonStringData.setText(jsonStr);
        } catch (IOException e)
        {
            e.printStackTrace();
            pathFileText.setText(e.getMessage());
            jsonStringData.setText(jsonStr);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            pathFileText.setText("Ready Convert");
            runConvertJsonToXls(jsonStringData.toString());
        }
    }

    public void onCrash(Thread thread, Throwable throwable) {}
}

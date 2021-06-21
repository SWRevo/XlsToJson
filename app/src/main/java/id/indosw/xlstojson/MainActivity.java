package id.indosw.xlstojson;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.poi.hssf.usermodel.HSSFSheet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.indosw.fileutil.FileUtil;
import id.indosw.xlstojson.jsonstringtoxls.ConvertJsonToExcel;
import id.indosw.xlstojson.jsonstringtoxls.Customer;

public class MainActivity extends Activity /*implements Crasher.OnCrashListener*/ {

    public final int REQ_CD_PICK = 101;
    private final Intent pick = new Intent(Intent.ACTION_GET_CONTENT);
    private EditText editText;
    private TextView pathFileText;
    private List<Customer> customers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Crasher crasher = new Crasher(this);
        crasher.addListener(this);
        crasher.setEmail("luthfi.otoclash@gmail.com");
        //crasher.setForceStackOverflow(true);
        crasher.setCrashActivityEnabled(true);*/

        initView();
        initLogic();
    }

    private void initLogic() {
        pick.setType("*/*");
        pick.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
    }

    private void taskConvertToJson(String file) {
        try {
            ExcelConverter converter = new ExcelConverter();
            HSSFSheet sheet = converter.loadExcel(file);
            String json = converter.excelToJson(sheet, "S");
            System.out.println(json);
            editText.setText(json);
            //converter.saveJson("pathSave", json);
        } catch (Exception e) {
            e.printStackTrace();
            editText.setText(e.getMessage());
        }
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        Button button = findViewById(R.id.pickFile);
        editText = findViewById(R.id.responseText);
        pathFileText = findViewById(R.id.pathFileText);
        button.setOnClickListener(v -> {
            //pickDocClicked();
            pathFileText.setText("Ready Convert");
            runConvertJsonToXls();
        });
    }

    @SuppressLint("SdCardPath")
    private void runConvertJsonToXls() {
        String jsonStr = "[{\"id\":\"1\",\"name\":\"Ravishankar Kumar\",\"address\":\"Bangalore\",\"age\":32},{\"id\":\"2\",\"name\":\"Chulbul Yadav\",\"address\":\"Motihari\",\"age\":27},{\"id\":\"3\",\"name\":\"Mirchae Devi\",\"address\":\"Buxcer\",\"age\":26},{\"id\":\"4\",\"name\":\"Deepak Jha\",\"address\":\"Mithila\",\"age\":33},{\"id\":\"5\",\"name\":\"Shilpa Rani\",\"address\":\"Nala Road\",\"age\":36}]";
        try {
            customers = ConvertJsonToExcel.convertJsonString2Objects(jsonStr);
        } catch (Exception e){
            pathFileText.setText(e.getMessage());
        }
        pathFileText.setText(customers.toString());

        try {
            ConvertJsonToExcel.writeObjects2ExcelFile(customers, "/storage/emulated/0/Download/customers.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
            pathFileText.setText(e.getMessage());
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private void pickDocClicked() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            }
            else {
                try {
                    startActivityForResult(pick, REQ_CD_PICK);
                }catch (Exception e){
                    editText.setText(e.getMessage());
                }
            }
        }
        else {
            try {
                startActivityForResult(pick, REQ_CD_PICK);
            }catch (Exception e){
                editText.setText(e.getMessage());
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            try {
                startActivityForResult(pick, REQ_CD_PICK);
            }catch (Exception e){
                editText.setText(e.getMessage());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CD_PICK) {
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<String> filePath = new ArrayList<>();
                if (data != null) {
                    if (data.getClipData() != null) {
                        for (int index = 0; index < data.getClipData().getItemCount(); index++) {
                            ClipData.Item item = data.getClipData().getItemAt(index);
                            filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), item.getUri()));
                        }
                    } else {
                        filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), data.getData()));
                    }
                }
                pathFileText.setText(filePath.get(0));
                taskConvertToJson(filePath.get(0));
            }
        }
    }

    /*@Override
    public void onCrash(Thread thread, Throwable throwable) {}*/
}
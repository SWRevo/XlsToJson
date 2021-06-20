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
import java.util.ArrayList;
import id.indosw.fileutil.FileUtil;

import id.indosw.xlstojson.kt.ExcelConverter;


public class MainActivity extends Activity {

    public final int REQ_CD_PICK = 101;
    private final Intent pick = new Intent(Intent.ACTION_GET_CONTENT);
    private EditText editText;
    private TextView pathFileText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initLogic();
    }

    private void initLogic() {
        pick.setType("application/*");
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

    private void initView() {
        Button button = findViewById(R.id.pickFile);
        editText = findViewById(R.id.responseText);
        pathFileText = findViewById(R.id.pathFileText);
        button.setOnClickListener(v -> pickDocClicked());
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
}
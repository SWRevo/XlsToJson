package id.indosw.xlstojson.java;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import id.indosw.fileutil.FileUtil;
import id.indosw.jsonviewer.JsonViewer;
import id.indosw.xlstojson.ExcelConverter;
import id.indosw.xlstojson.R;

public class ExcelToJsonActivity extends Activity {

    private JsonViewer jsonViewer;
    public final int REQ_CD_PICK = 101;
    private final Intent pick = new Intent(Intent.ACTION_GET_CONTENT);
    private TextView pathFileText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exceljson);
        initView();
        initLogic();
    }

    private void initLogic() {
        pick.setType("*/*");
        pick.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        Button button = findViewById(R.id.pickFile);
        pathFileText = findViewById(R.id.pathFileText);
        jsonViewer = findViewById(R.id.jsonViewer);
        button.setOnClickListener(v -> pickDocClicked());
    }

    @SuppressLint("ObsoleteSdkInt")
    private void pickDocClicked() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 1000);
            }
            else {
                try {startActivityForResult(pick, REQ_CD_PICK);
                }catch (Exception e)
            {
                    pathFileText.setText(e.getMessage());
                }
            }
        }
        else {
            try { startActivityForResult(pick, REQ_CD_PICK);
            }catch (Exception e)
        {
                pathFileText.setText(e.getMessage());
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
                pathFileText.setText(e.getMessage());
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

    private void taskConvertToJson(String file) {
        try {
            ExcelConverter converter = new ExcelConverter();
            HSSFSheet sheet = converter.loadExcel(file);
            String json = converter.excelToJson(sheet, "S");
            System.out.println(json);
            showJsonToView(json);
        } catch (Exception e) {
            e.printStackTrace();
            pathFileText.setText(e.getMessage());
        }
    }

    private void showJsonToView(String json) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            jsonViewer.setTextColorBool(getApplicationContext().getColor(R.color.purple_200));
            jsonViewer.setTextColorNull(getApplicationContext().getColor(R.color.purple_500));
            jsonViewer.setTextColorNumber(getApplicationContext().getColor(R.color.teal_200));
            jsonViewer.setTextColorString(getApplicationContext().getColor(R.color.jsonViewer_textColorBool));
        }
        else {
            jsonViewer.setTextColorBool(getResources().getColor(R.color.purple_200));
            jsonViewer.setTextColorNull(getResources().getColor(R.color.purple_500));
            jsonViewer.setTextColorNumber(getResources().getColor(R.color.teal_200));
            jsonViewer.setTextColorString(getResources().getColor(R.color.jsonViewer_textColorBool));
        }

        try {
            //jsonViewer.setJson(new JSONObject(json));
            jsonViewer.setJson(new JSONArray(json));
        } catch (JSONException e) {
            e.printStackTrace();
            pathFileText.setText(e.getMessage());
        }
    }

    public void onClickExpend(View view) {
        jsonViewer.expandJson();
    }

    public void onClickCollapse(View view) {
        jsonViewer.collapseJson();
    }
}

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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import id.indosw.crashreport.Crasher;
import id.indosw.fileutil.FileUtil;
import id.indosw.jsonviewer.JsonViewer;
import id.indosw.xlstojson.R;
import id.indosw.xlstojson.xlstojsonstring.ConvertExcelToJson;
import id.indosw.xlstojson.xlstojsonstring.Customer;

public class ExcelToJson2Activity extends AppCompatActivity implements Crasher.OnCrashListener {

    private JsonViewer jsonViewer;
    private final Intent pick = new Intent(Intent.ACTION_GET_CONTENT);
    private TextView pathFileText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exceljson2);

        Crasher crasher = new Crasher(this);
        crasher.addListener(this);
        crasher.setEmail("luthfi.otoclash@gmail.com");
        //crasher.setForceStackOverflow(true);
        crasher.setCrashActivityEnabled(true);

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }
        else {
            try { openSomeActivityForResult();
            }catch (Exception e) {pathFileText.setText(e.getMessage());}
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            try {openSomeActivityForResult();
            }catch (Exception e){pathFileText.setText(e.getMessage());}
        }
    }

    private void taskConvertToJson(String file) {
        // Step 1: Read Excel File into Java List Objects
        List<Customer> customers = ConvertExcelToJson.readExcelFile(file);
        // Step 2: Convert Java Objects to JSON String
        String jsonString = ConvertExcelToJson.convertObjects2JsonString(customers);
        System.out.println(jsonString);
        showJsonToView(jsonString);
        /**try {
            // Step 1: Read Excel File into Java List Objects
            List<Customer> customers = ConvertExcelToJson.readExcelFile(file);
            // Step 2: Convert Java Objects to JSON String
            String jsonString = ConvertExcelToJson.convertObjects2JsonString(customers);
            System.out.println(jsonString);
            showJsonToView(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            pathFileText.setText(e.getMessage());
        }**/
    }

    private void showJsonToView(String json) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            jsonViewer.setTextColorBool(getApplicationContext().getColor(R.color.purple_200));
            jsonViewer.setTextColorNull(getApplicationContext().getColor(R.color.light_red));
            jsonViewer.setTextColorNumber(getApplicationContext().getColor(R.color.teal_200));
            jsonViewer.setTextColorString(getApplicationContext().getColor(R.color.jsonViewer_textColorString));
        }
        else {
            jsonViewer.setTextColorBool(getResources().getColor(R.color.purple_200));
            jsonViewer.setTextColorNull(getResources().getColor(R.color.light_red));
            jsonViewer.setTextColorNumber(getResources().getColor(R.color.teal_200));
            jsonViewer.setTextColorString(getResources().getColor(R.color.jsonViewer_textColorString));
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

    @Override
    public void onCrash(Thread thread, Throwable throwable) {}

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        // There are no request codes
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
            });

    public void openSomeActivityForResult() {
        someActivityResultLauncher.launch(pick);
    }
}

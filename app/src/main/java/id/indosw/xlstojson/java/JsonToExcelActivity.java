package id.indosw.xlstojson.java;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import id.indosw.crashreport.Crasher;
import id.indosw.xlstojson.R;
import id.indosw.xlstojson.jsonstringtoxls.ConvertJsonToExcel;
import id.indosw.xlstojson.jsonstringtoxls.Customer;

public class JsonToExcelActivity extends AppCompatActivity implements Crasher.OnCrashListener {

    private EditText jsonStringData;
    private TextView pathFileText;
    private List<Customer> customers;

    private String jsonStr;

    private ImageView openExcelFile;

    private String pathExcel;

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
        openExcelFile = findViewById(R.id.openExcelFile);
        convertJsonToXls.setOnClickListener(v -> {
            if (jsonStringData.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(),"Can Not Empty Data",Toast.LENGTH_SHORT).show();
                return;
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
            }
            else {
                pathFileText.setText("Ready Convert");
                runConvertJsonToXls(jsonStringData.getText().toString());
            }
        });

        openExcelFile.setOnClickListener(v -> {
            //Uri pathUri = Uri.fromFile(new File(pathExcel));
            if(pathExcel.isEmpty()){
                return;
            }
            File filePath = new File(pathExcel);
            Uri pathUri = FileProvider.getUriForFile(JsonToExcelActivity.this, getApplicationContext().getPackageName() + ".provider", filePath);
            openFile(pathUri);
        });
    }

    @SuppressLint({"SdCardPath", "SetTextI18n"})
    private void runConvertJsonToXls(String s) {
        try { customers = ConvertJsonToExcel.convertJsonString2Objects(s);
        } catch (Exception e){pathFileText.setText(e.getMessage());}
        jsonStringData.setText(customers.toString());
        pathExcel = "/storage/emulated/0/Download/customers.xlsx";
        try { ConvertJsonToExcel.writeObjects2ExcelFile(customers, pathExcel);
            pathFileText.setText("Success generate and save to : " + pathExcel);
            jsonStringData.setText(jsonStr);
            openExcelFile.setVisibility(View.VISIBLE);
        } catch (IOException e)
        {
            e.printStackTrace();
            pathFileText.setText(e.getMessage());
            jsonStringData.setText(jsonStr);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions,
                                           @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            pathFileText.setText("Ready Convert");
            runConvertJsonToXls(jsonStringData.getText().toString());
        }
    }

    public void onCrash(Thread thread, Throwable throwable) {}

    public void openFile(Uri uri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (uri.toString().contains(".doc") || uri.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (uri.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (uri.toString().contains(".ppt") || uri.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (uri.toString().contains(".xls") || uri.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (uri.toString().contains(".zip") || uri.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if (uri.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (uri.toString().contains(".wav") || uri.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (uri.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (uri.toString().contains(".jpg") || uri.toString().contains(".jpeg") || uri.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (uri.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (uri.toString().contains(".3gp") || uri.toString().contains(".mpg") || uri.toString().contains(".mpeg") || uri.toString().contains(".mpe") || uri.toString().contains(".mp4") || uri.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            // Other files
            intent.setDataAndType(uri, "*/*");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
}

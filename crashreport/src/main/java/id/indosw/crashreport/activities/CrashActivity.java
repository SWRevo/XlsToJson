package id.indosw.crashreport.activities;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import java.util.Locale;

import id.indosw.crashreport.BuildConfig;
import id.indosw.crashreport.R;
import id.indosw.crashreport.utils.ColorUtils;
import id.indosw.crashreport.utils.CrashUtils;
import id.indosw.crashreport.utils.ImageUtils;
import james.buttons.Button;

public class CrashActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_NAME = "james.crasher.EXTRA_NAME";
    public static final String EXTRA_MESSAGE = "james.crasher.EXTRA_MESSAGE";
    public static final String EXTRA_STACK_TRACE = "james.crasher.EXTRA_STACK_TRACE";

    public static final String EXTRA_EMAIL = "james.crasher.EXTRA_EMAIL";
    public static final String EXTRA_DEBUG_MESSAGE = "james.crasher.EXTRA_DEBUG_MESSAGE";
    public static final String EXTRA_COLOR = "james.crasher.EXTRA_COLOR";

    private ActionBar actionBar;
    private TextView name;
    private ImageView stackTraceArrow;
    private TextView stackTrace;

    private String body;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);

        name = findViewById(R.id.name);
        TextView message = findViewById(R.id.message);
        TextView description = findViewById(R.id.description);
        Button copy = findViewById(R.id.copy);
        Button share = findViewById(R.id.share);
        Button email = findViewById(R.id.email);
        View stackTraceHeader = findViewById(R.id.stackTraceHeader);
        stackTraceArrow = findViewById(R.id.stackTraceArrow);
        stackTrace = findViewById(R.id.stackTrace);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            actionBar = getSupportActionBar();
        }


        int color = getIntent().getIntExtra(EXTRA_COLOR, ContextCompat.getColor(this, R.color.purple_500));
        int colorDark = ColorUtils.darkColor(color);
        boolean isColorDark = ColorUtils.isColorDark(color);

        if (toolbar != null) {
            toolbar.setBackgroundColor(color);
        }
        if (toolbar != null) {
            toolbar.setTitleTextColor(isColorDark ? Color.WHITE : Color.BLACK);
        }

        copy.setBackgroundColor(colorDark);
        copy.setTextColor(colorDark);
        copy.setOnClickListener(this);

        share.setBackgroundColor(colorDark);
        share.setTextColor(colorDark);
        share.setOnClickListener(this);

        email.setBackgroundColor(colorDark);
        email.setTextColor(colorDark);
        if (getIntent().hasExtra(EXTRA_EMAIL)) {
            email.setOnClickListener(this);
        } else email.setVisibility(View.GONE);

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(String.format(Locale.getDefault(), getString(R.string.title_crasher_crashed), getString(R.string.app_name)));
            actionBar.setHomeAsUpIndicator(ImageUtils.getVectorDrawable(this, R.drawable.ic_crasher_back, isColorDark ? Color.WHITE : Color.BLACK));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(colorDark);
            getWindow().setNavigationBarColor(colorDark);
        }

        String stack = getIntent().getStringExtra(EXTRA_STACK_TRACE);
        String stackCause = CrashUtils.getCause(this, stack);

        String nameString = getIntent().getStringExtra(EXTRA_NAME) + (stackCause != null ? " at " + stackCause : "");
        String messageString = getIntent().getStringExtra(EXTRA_NAME);

        name.setText(nameString);
        if (messageString != null && messageString.length() > 0)
            message.setText(messageString);
        else message.setVisibility(View.GONE);

        description.setText(String.format(Locale.getDefault(), getString(R.string.msg_crasher_crashed), getString(R.string.app_name)));

        stackTrace.setText(stack);
        stackTraceHeader.setOnClickListener(this);
        if (BuildConfig.DEBUG)
            stackTraceHeader.callOnClick();

        body = nameString + "\n" + (messageString != null ? messageString : "") + "\n\n" + stack
                + "\n\nAndroid Version: " + Build.VERSION.SDK_INT
                + "\nDevice Manufacturer: " + Build.MANUFACTURER
                + "\nDevice Model: " + Build.MODEL
                + "\n\n" + (getIntent().hasExtra(EXTRA_DEBUG_MESSAGE) ? getIntent().getStringExtra(EXTRA_DEBUG_MESSAGE) : "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("QueryPermissionsNeeded")
    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.copy) {
            Object service = getSystemService(CLIPBOARD_SERVICE);
            if (service instanceof android.content.ClipboardManager)
                ((android.content.ClipboardManager) service).setPrimaryClip(ClipData.newPlainText(name.getText().toString(), stackTrace.getText().toString()));
            else if (service instanceof android.text.ClipboardManager)
                ((android.text.ClipboardManager) service).setText(stackTrace.getText().toString());
        } else if (v.getId() == R.id.share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, body);
            startActivity(Intent.createChooser(intent, getString(R.string.title_crasher_share)));
        } else if (v.getId() == R.id.email) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, getIntent().getStringExtra(EXTRA_EMAIL));
            intent.putExtra(Intent.EXTRA_SUBJECT, String.format(Locale.getDefault(), getString(R.string.title_crasher_exception), name.getText().toString(), getString(R.string.app_name)));
            intent.putExtra(Intent.EXTRA_TEXT, body);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else if (v.getId() == R.id.stackTraceHeader) {
            if (stackTrace.getVisibility() == View.GONE) {
                stackTrace.setVisibility(View.VISIBLE);
                stackTraceArrow.animate().scaleY(-1).start();
            } else {
                stackTrace.setVisibility(View.GONE);
                stackTraceArrow.animate().scaleY(1).start();
            }
        }
    }
}

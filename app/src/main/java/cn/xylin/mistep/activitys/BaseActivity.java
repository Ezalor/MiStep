package cn.xylin.mistep.activitys;

import android.os.Bundle;
import android.widget.LinearLayout;
import com.google.android.material.appbar.MaterialToolbar;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import cn.xylin.mistep.R;
import cn.xylin.mistep.StepApplication;
import cn.xylin.mistep.utils.Util;

import android.os.Build;
import android.Manifest;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
// import android.os.Bundle;
// import androidx.activity.result.ActivityResultLauncher;
// import androidx.activity.result.contract.ActivityResultContracts;
// import androidx.appcompat.app.AppCompatActivity;
// import android.widget.Toast;

/**
 * @author XyLin
 * @date 2020年11月21日 22:31:00
 * BaseActivity.java
 **/
public abstract class BaseActivity extends AppCompatActivity {

    AppCompatActivity appActivity;
    boolean isCanExitActivity = false;
    long clickBackTime;
    MaterialToolbar trBar;

    private static final int REQUEST_CODE_ACTIVITY_RECOGNITION = 100;

    private void checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, REQUEST_CODE_ACTIVITY_RECOGNITION);
            } /*else {
                // 权限已授予
            }*/
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkAndRequestPermission();
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.content_view);
        appActivity = this;
        StepApplication.add(appActivity);
        trBar = findViewById(R.id.trBar);
        setSupportActionBar(trBar);
        initActivityControl();
        initControlAttribute();
    }

    abstract void initActivityControl();

    abstract void initControlAttribute();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StepApplication.remove(appActivity);
    }

    @Override
    public void onBackPressed() {
        if (isCanExitActivity) {
            if ((System.currentTimeMillis() - clickBackTime) > Util.DELAY_TIME) {
                clickBackTime = System.currentTimeMillis();
                Util.toast(appActivity, "再按一次以退出程序");
                return;
            }
            StepApplication.finishAll();
        }
        super.onBackPressed();
    }

    @Override
    public void setContentView(@LayoutRes int layoutId) {
        ((LinearLayout) findViewById(R.id.llGroup)).addView(getLayoutInflater().inflate(layoutId, null));
    }
}

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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // 必须最先调用
    
        // 先请求权限（异步，不会阻塞）
        checkAndRequestPermission();
    
        // 这里不要立即初始化布局和控件，等权限授权后再初始化
    }
    
    private void checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                    == PackageManager.PERMISSION_GRANTED) {
                initAfterPermissionGranted();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, REQUEST_CODE_ACTIVITY_RECOGNITION);
            }
        } else {
            initAfterPermissionGranted();
        }
    }
    
    private void initAfterPermissionGranted() {
        setContentView(R.layout.content_view);
        // 初始化控件
        trBar = findViewById(R.id.trBar);
        setSupportActionBar(trBar);
        initActivityControl();
        initControlAttribute();
        // 其他初始化
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ACTIVITY_RECOGNITION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initAfterPermissionGranted();
            } else {
                // 权限被拒绝，视情况处理，比如提示用户或依然初始化部分功能
                Toast.makeText(this, "权限被拒绝，无法读取步数", Toast.LENGTH_SHORT).show();
                initAfterPermissionGranted();
            }
        }
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

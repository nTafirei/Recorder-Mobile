package com.marotech.recording;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.marotech.recording.fragments.HomeFragment;
import com.marotech.recording.fragments.LoginFragment;
import com.marotech.recording.fragments.LogoutFragment;
import com.marotech.recording.fragments.RecordingsFragment;
import com.marotech.recording.fragments.RegisterFragment;
import com.marotech.recording.util.ApplicationError;
import com.marotech.recording.util.ErrorAlert;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public abstract class AbstractActivity extends AppCompatActivity implements BottomNavigationView
        .OnNavigationItemSelectedListener {

    public static final String ACTIVITY_MESSAGE = "activityMessage";
    public ViewPager viewPager;
    public ErrorAlert alertDialog;
    public static Context applicationContext = null;
    public static Thread.UncaughtExceptionHandler defaultHandler = null;
    public static Thread.UncaughtExceptionHandler exceptionHandler = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //detectCrash(this);

        if (defaultHandler == null) {
            defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        }

        if (applicationContext == null) {
            applicationContext = getApplicationContext();
        }

        if (exceptionHandler == null) {
            exceptionHandler = new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                    Log.e("Uncaught Exception", paramThrowable.getMessage());
                    logError(getCrashData(paramThrowable));
                    defaultHandler.uncaughtException(paramThread, paramThrowable);
                }
            };
            Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
        }

        if (getIntent().getBooleanExtra(EXIT, false)) {
            finish();
        }
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        if (viewPager != null) {
            viewPager.setCurrentItem(item, smoothScroll);
        }
    }


    protected void setStatusAndColor(Button button, boolean status, int color) {
        if (button != null) {
            button.setEnabled(status);
            button.setTextColor(color);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }


    public void setColor(Button button, int color) {
        if (button != null) {
            button.setTextColor(color);
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    @Override
    public boolean
    onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_login_section:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, loginFragment)
                        .commit();
                return true;
            case R.id.navigation_recordings_info:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, recordingsFragment)
                        .commit();
                return true;

            case R.id.navigation_register_info:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, registerFragment)
                        .commit();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {

        if (getActivityClass().getClass() != MainActivity.class) {
            Intent intent = new Intent(getActivityClass(),
                    MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }
    }

    public void enableButton(Button button) {
        if (button == null) {
            return;
        }
        button.setEnabled(true);
        button.setTextColor(Color.WHITE);
    }

    public void disableButton(Button button) {
        if (button == null) {
            return;
        }
        button.setEnabled(false);
        button.setTextColor(Color.GRAY);
    }

    public void launchActivity(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        this.finish();
    }

    public void launchActivity(Class<?> clazz, String activityMessage) {
        Intent intent = new Intent(this, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(ACTIVITY_MESSAGE, activityMessage);
        startActivity(intent);
        this.finish();
    }

    public String getLocalizedBigDecimalValue(BigDecimal input, Locale locale) {
        final NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        numberFormat.setGroupingUsed(true);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
        return numberFormat.format(input);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void logError(ApplicationError error) {
        try {
            Log.e("Saved error:", error.toString());
        } catch (Exception e) {
        }
    }

    public ApplicationError getCrashData(final Throwable paramThrowable) {

        ApplicationError error = new ApplicationError();

        String stackTrace = "";
        for (int i = 0; i < paramThrowable.getStackTrace().length; i++) {
            stackTrace += paramThrowable.getStackTrace()[i].toString() + "\n";
        }

        Throwable tmp = paramThrowable;
        int j = 0;
        while ((tmp = tmp.getCause()) != null && j < 5) {
            j++;
            stackTrace += "Coused by:\n";
            for (int i = 0; i < tmp.getStackTrace().length; i++) {
                stackTrace += tmp.getStackTrace()[i].toString() + "\n";
            }
        }

        String deviceInfo = "";
        deviceInfo += "OS version   : " + System.getProperty("os.version") + "\n";
        deviceInfo += "API level    : " + Build.VERSION.SDK_INT + "\n";
        deviceInfo += "Manufacturer : " + Build.MANUFACTURER + "\n";
        deviceInfo += "Device       : " + Build.DEVICE + "\n";
        deviceInfo += "Model        : " + Build.MODEL + "\n";
        deviceInfo += "Product      : " + Build.PRODUCT + "\n";
        deviceInfo += "DeviceId     : " + getDeviceId() + "\n";

        error.setDeviceInfo(deviceInfo);
        error.setErrorMessage(paramThrowable.getMessage());
        error.setStackTrace(stackTrace);
        return error;
    }

    public void emailCrashReport(String e) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        String subject = "MyEasyCard Crash Report";
        String body =
                "Mail this error report to MyEasyCard: " +
                        "\n" +
                        e.toString() +
                        "\n";

        sendIntent.putExtra(Intent.EXTRA_EMAIL,
                new String[]{"nkasvosve@gmail.com"});
        sendIntent.putExtra(Intent.EXTRA_TEXT, body);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.setType("message/rfc822");
        this.startActivity(
                Intent.createChooser(sendIntent, "MyEasyCard Crash Report"));
    }

    public void executeTask(
            final AsyncTask task,
            final Object[] array) {
        task.execute(array);
    }

    public String getStackTrace(Throwable e) {
        StackTraceElement[] arr = e.getStackTrace();
        String report = e.toString() + "\n\n";
        report += "--------- Stack trace ---------\n\n";
        for (int i = 0; i < arr.length; i++) {
            report += "    " + arr[i].toString() + "\n";
        }
        report += "-------------------------------\n\n";

// If the exception was thrown in a background thread inside
// AsyncTask, then the actual exception can be found with getCause
        report += "--------- Cause ---------\n\n";
        Throwable cause = e.getCause();
        if (cause != null) {
            report += cause.toString() + "\n\n";
            arr = cause.getStackTrace();
            for (int i = 0; i < arr.length; i++) {
                report += "    " + arr[i].toString() + "\n";
            }
        }
        report += "-------------------------------\n\n";
        return report;
    }

    protected void detectCrash(Activity activity) {
        String line;
        StringBuilder trace = new StringBuilder();
        try {

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(activity
                            .openFileInput("marotech.stack.trace")));
            while ((line = reader.readLine()) != null) {
                trace.append(line + "\n");
            }
        } catch (FileNotFoundException fne) {
            return;
        } catch (IOException e) {
            return;
        }

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        String subject = "Identity Manager Crash Report";
        String body =
                "Mail this error report to Identity Manager: " +
                        "\n" +
                        trace +
                        "\n";

        sendIntent.putExtra(Intent.EXTRA_EMAIL,
                new String[]{"nkasvosve@gmail.com"});
        sendIntent.putExtra(Intent.EXTRA_TEXT, body);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.setType("message/rfc822");

        activity.startActivity(
                Intent.createChooser(sendIntent, "Identity Manager Crash Report"));
        activity.deleteFile("marotech.stack.trace");
    }


    private String getEmulatorDeviceId() {
        return "11b3-e7c7-0000-000046bffd11";
    }

    public String getDeviceId() {

        String deviceId = "";

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        0);
            }
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            final TelephonyManager telephonyManager = (TelephonyManager) getBaseContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = telephonyManager.getDeviceId();
        }

        if (isEmulator()) {
            return getEmulatorDeviceId();
        }

        if (deviceId == null || deviceId.trim().length() == 0) {
            String message = "Not able to read device id";
            alertDialog = new ErrorAlert(getActivityClass());
            alertDialog.showErrorDialog("Device Error", message);
        }
        return deviceId;
    }

    public Activity getActivityClass() {
        return this;
    }


    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    public void addFragment(@IdRes int containerViewId,
                            @NonNull Fragment fragment,
                            @NonNull String fragmentTag) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(containerViewId, fragment, fragmentTag)
                .disallowAddToBackStack()
                .commit();
    }

    protected RegisterFragment registerFragment;
    protected LogoutFragment syncFragment;
    protected LoginFragment loginFragment;
    protected HomeFragment homeFragment;
    protected RecordingsFragment recordingsFragment;

    private static final String EXIT = "EXIT";
    private final String TAG = "recorder_AbstractActivity";
}

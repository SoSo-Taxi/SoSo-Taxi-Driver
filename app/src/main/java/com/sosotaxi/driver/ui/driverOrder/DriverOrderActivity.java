/**
 * @Author 范承祥
 * @CreateTime 2020/7/15
 * @UpdateTime 2020/7/15
 */
package com.sosotaxi.driver.ui.driverOrder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Toast;

import com.sosotaxi.driver.R;
import com.sosotaxi.driver.ui.widget.OnSlideListener;
import com.sosotaxi.driver.ui.widget.SlideButton;

public class DriverOrderActivity extends AppCompatActivity {

    private SlideButton mSlideButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_order);

        Toolbar toolbar = findViewById(R.id.toolbarDriverOrder);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSlideButton =findViewById(R.id.slideButtonArriveStartingPoint);
        mSlideButton.addSlideListener(new OnSlideListener() {
            @Override
            public void onSlideSuccess() {
                Toast.makeText(getApplicationContext(), "解锁成功!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
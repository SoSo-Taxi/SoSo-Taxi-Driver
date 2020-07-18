/**
 * @Author 范承祥
 * @CreateTime 2020/7/15
 * @UpdateTime 2020/7/18
 */
package com.sosotaxi.driver.ui.driverOrder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.TextView;

import com.sosotaxi.driver.R;
import com.sosotaxi.driver.common.OnToolbarListener;

public class DriverOrderActivity extends AppCompatActivity implements OnToolbarListener {

    private Toolbar mToolbar;
    private TextView mTextViewTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_order);

        mToolbar = findViewById(R.id.toolbarDriverOrder);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mTextViewTitle=findViewById(R.id.textViewDriverOrderToolbarTitle);

        // 跳转到达上车地点界面
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frameLayoutDriverOrder,new ReceiveOrderFragment(),null);
        fragmentTransaction.commit();
    }

    /**
     * 设置工具栏标题
     * @param title 标题
     */
    @Override
    public void setTitle(String title) {
        mTextViewTitle.setText(title);
    }

    /**
     * 设置工具栏是否展示
     * @param isShown 是否展示
     */
    @Override
    public void showToolbar(boolean isShown) {
        if(isShown==false){
            getSupportActionBar().hide();
        }else{
            getSupportActionBar().show();
        }
    }
}
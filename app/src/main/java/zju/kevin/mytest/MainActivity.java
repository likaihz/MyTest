/*test github*/
package zju.kevin.mytest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import zju.kevin.mytest.fragment.AddressFragment;
import zju.kevin.mytest.fragment.FindFragment;
import zju.kevin.mytest.fragment.MenuFragment;

public class MainActivity extends FragmentActivity {
    private static FragmentManager fMgr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取FragmentManager实例
        fMgr = getSupportFragmentManager();

        initFragment();
        dealBottomButtonsClickEvent();

    }
    /**
     * 初始化首个Fragment
     */
    private void initFragment() {
        FragmentTransaction ft = fMgr.beginTransaction();
        MenuFragment weiXinFragment = new MenuFragment();
        ft.add(R.id.fragmentRoot, weiXinFragment, "weiXinFragment");
        ft.addToBackStack("weiXinFragment");
        ft.commit();
        ((Button)findViewById(R.id.rbMenu)).setTextColor(Color.WHITE);
    }
    /**
     * 处理底部点击事件
     */
    private void dealBottomButtonsClickEvent() {
        findViewById(R.id.rbMenu).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(fMgr.findFragmentByTag("MenuFragment")!=null && fMgr.findFragmentByTag("MenuFragment").isVisible()) {
                    return;
                }
                ((Button)findViewById(R.id.rbMenu)).setTextColor(Color.WHITE);
                ((Button)findViewById(R.id.rbOrder)).setTextColor(0xFF696969);
                ((Button)findViewById(R.id.rbRestaurant)).setTextColor(0xFF696969);

                popAllFragmentsExceptTheBottomOne();

            }
        });
        findViewById(R.id.rbOrder).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popAllFragmentsExceptTheBottomOne();
                FragmentTransaction ft = fMgr.beginTransaction();
                ft.hide(fMgr.findFragmentByTag("weiXinFragment"));
                AddressFragment sf = new AddressFragment();
                ft.add(R.id.fragmentRoot, sf, "AddressFragment");
                ft.addToBackStack("AddressFragment");
                ft.commit();

                ((Button)findViewById(R.id.rbMenu)).setTextColor(0xFF696969);
                ((Button)findViewById(R.id.rbOrder)).setTextColor(Color.WHITE);
                ((Button)findViewById(R.id.rbRestaurant)).setTextColor(0xFF696969);

            }
        });
        findViewById(R.id.rbRestaurant).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popAllFragmentsExceptTheBottomOne();
                FragmentTransaction ft = fMgr.beginTransaction();
                ft.hide(fMgr.findFragmentByTag("weiXinFragment"));
                FindFragment sf = new FindFragment();
                ft.add(R.id.fragmentRoot, sf, "AddressFragment");
                ft.addToBackStack("FindFragment");
                ft.commit();

                ((Button)findViewById(R.id.rbMenu)).setTextColor(0xFF696969);
                ((Button)findViewById(R.id.rbOrder)).setTextColor(0xFF696969);
                ((Button)findViewById(R.id.rbRestaurant)).setTextColor(Color.WHITE);
            }
        });
        /*findViewById(R.id.rbMe).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popAllFragmentsExceptTheBottomOne();
                FragmentTransaction ft = fMgr.beginTransaction();
                ft.hide(fMgr.findFragmentByTag("weiXinFragment"));
                MeFragment sf = new MeFragment();
                ft.add(R.id.fragmentRoot, sf, "MeFragment");
                ft.addToBackStack("MeFragment");
                ft.commit();
            }
        });*/
    }

    /**
     * 从back stack弹出所有的fragment，保留首页的那个
     */
    public static void popAllFragmentsExceptTheBottomOne() {
        for (int i = 0, count = fMgr.getBackStackEntryCount() - 1; i < count; i++) {
            fMgr.popBackStack();
        }
    }
    //点击返回按钮
    @Override
    public void onBackPressed() {
        if(fMgr.findFragmentByTag("weiXinFragment")!=null && fMgr.findFragmentByTag("weiXinFragment").isVisible()) {
            MainActivity.this.finish();
        } else {
            super.onBackPressed();
        }
    }
}

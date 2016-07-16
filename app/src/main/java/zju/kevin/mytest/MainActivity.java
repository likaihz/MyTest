/*test github*/
package zju.kevin.mytest;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;


import android.app.Fragment;
//import android.app.FragmentActivity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
//import android.support.v4.app.FragmentActivity;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import zju.kevin.mytest.fragment.OrderFragment;
import zju.kevin.mytest.fragment.RestaurantFragment;
import zju.kevin.mytest.fragment.MenuFragment;

public class MainActivity extends Activity {
    private static FragmentManager fMgr;
    private String rmail = new String("10086");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        //获取FragmentManager实例
        fMgr = getFragmentManager();

        initFragment();
        dealBottomButtonsClickEvent();

    }
    /**
     * 初始化首个Fragment
     */
    private void initFragment() {
        FragmentTransaction ft = fMgr.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("rmail", rmail);
        MenuFragment menuFragment = new MenuFragment();
        menuFragment.setArguments(bundle);
        ft.add(R.id.fragmentRoot, menuFragment, "menuFragment");
        ft.addToBackStack("menuFragment");
        ft.commit();
        ((Button)findViewById(R.id.rbMenu)).setTextColor(Color.WHITE);
        ((Button)findViewById(R.id.rbOrder)).setTextColor(0xFF696969);
        ((Button)findViewById(R.id.rbRestaurant)).setTextColor(0xFF696969);

    }
    /**
     * 处理底部点击事件
     */
    private void dealBottomButtonsClickEvent() {
        findViewById(R.id.rbMenu).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(fMgr.findFragmentByTag("menuFragment")!=null && fMgr.findFragmentByTag("menuFragment").isVisible()) {
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
                ft.hide(fMgr.findFragmentByTag("menuFragment"));
                OrderFragment of = new OrderFragment();
                //主activity向fragment传递参数
                Bundle bundle = new Bundle();
                bundle.putString("rmail", rmail);
                of.setArguments(bundle);

                ft.add(R.id.fragmentRoot, of, "orderFragment");
                ft.addToBackStack("orderFragment");
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
                ft.hide(fMgr.findFragmentByTag("menuFragment"));
                RestaurantFragment rf = new RestaurantFragment();
                //主activity向fragment传递参数
                Bundle bundle = new Bundle();
                bundle.putString("rmail", rmail);
                rf.setArguments(bundle);

                ft.add(R.id.fragmentRoot, rf, "restaurantFragment");
                ft.addToBackStack("restaurantFragment");
                ft.commit();

                ((Button)findViewById(R.id.rbMenu)).setTextColor(0xFF696969);
                ((Button)findViewById(R.id.rbOrder)).setTextColor(0xFF696969);
                ((Button)findViewById(R.id.rbRestaurant)).setTextColor(Color.WHITE);
            }
        });

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
        if(fMgr.findFragmentByTag("menuFragment")!=null && fMgr.findFragmentByTag("menuFragment").isVisible()) {
            MainActivity.this.finish();
        } else {
            //super.onBackPressed();
            ((Button)findViewById(R.id.rbMenu)).setTextColor(Color.WHITE);
            ((Button)findViewById(R.id.rbOrder)).setTextColor(0xFF696969);
            ((Button)findViewById(R.id.rbRestaurant)).setTextColor(0xFF696969);

            popAllFragmentsExceptTheBottomOne();
        }
    }
}

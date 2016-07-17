package zju.kevin.mytest.fragment;

import zju.kevin.mytest.AddDish;
import zju.kevin.mytest.ChangePWD;
import zju.kevin.mytest.R;

import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RestaurantFragment extends Fragment{

    private String urlstr  = "http://139.129.6.166/proj/restaurant/";
    private String rmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.i(TAG, "--------onCreate");
        if(getArguments().getString("rmail") != null) rmail = getArguments().getString("rmail");
        if(getArguments().getString("urlstr") != null) urlstr = getArguments().getString("urlstr");

    }


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.restaurant_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
        ((TextView)getView().findViewById(R.id.chg_pwd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ChangePWD.class);
                intent.putExtra("rmail",rmail);
                intent.putExtra("urlstr",urlstr);
                startActivity(intent);
                getActivity().finish();
            }
        });

        ((TextView)getView().findViewById(R.id.add_dish)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), AddDish.class);
                intent.putExtra("rmail",rmail);
                intent.putExtra("urlstr",urlstr);
                startActivity(intent);
                getActivity().finish();
            }
        });


	}
}

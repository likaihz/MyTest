package zju.kevin.mytest.fragment;

import zju.kevin.mytest.R;

import android.app.ListFragment;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
//import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

//public class OrderFragment extends ListFragment {
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		return inflater.inflate(R.layout.order_fragment, container, false);
//	}
//
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onActivityCreated(savedInstanceState);
//		((TextView)getView().findViewById(R.id.tvTop)).setText("订单");
//	}
//}

public class OrderFragment extends ListFragment {

	private String TAG = OrderFragment.class.getName();
	//private ListView list ;
	//private SimpleAdapter adapter;
	/**
	 * @描述 在onCreateView中加载布局
	 * */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		Log.i(TAG, "--------onCreateView");
		return inflater.inflate(R.layout.order_fragment, container,false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "--------onCreate");
		String[] list = {"order 1","order 2","order 3","order 4","order 5"};
		this.setListAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, list));

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		System.out.println(l.getChildAt(position));
		HashMap<String, Object> view= (HashMap<String, Object>) l.getItemAtPosition(position);
		System.out.println(view.get("title").toString()+"+++++++++title");


		Toast.makeText(getActivity(), TAG+l.getItemIdAtPosition(position), Toast.LENGTH_LONG).show();
		System.out.println(v);
		System.out.println(position);


	}

//    private List<? extends Map<String, ?>> getData(String[] strs) {
//        List<Map<String ,Object>> list = new ArrayList<Map<String,Object>>();
//
//        for (int i = 0; i < strs.length; i++) {
//            Map<String, Object> map = new HashMap<String, Object>();
//            map.put("title", strs[i]);
//            list.add(map);
//
//        }
//
//        return list;
//    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i(TAG, "--------onActivityCreated");

	}


}

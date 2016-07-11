package zju.kevin.mytest.fragment;

import zju.kevin.mytest.R;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderFragment extends ListFragment {

	private String TAG = OrderFragment.class.getName();
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
        String[] from=new String[] {"order_id", "name", "contact", "order_time"};
        int[] to = new int[] {R.id.order_id, R.id.name, R.id.contact, R.id.order_time};
		SimpleAdapter adapter=new SimpleAdapter(this.getActivity(),
				getData(), R.layout.order_item, from, to);

        this.setListAdapter(adapter);

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
//		super.onListItemClick(l, v, position, id);
//
//		System.out.println(l.getChildAt(position));
//		HashMap<String, Object> view= (HashMap<String, Object>) l.getItemAtPosition(position);
//		System.out.println(view.get("title").toString()+"+++++++++title");
//
//
//		Toast.makeText(getActivity(), TAG+l.getItemIdAtPosition(position), Toast.LENGTH_LONG).show();
//		System.out.println(v);
//		System.out.println(position);


	}


    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("order_id", "10000000");
        map.put("name", "shabi");
        map.put("contact", "10086");
        map.put("order_time", "2016-07-10 19:00");
        list.add(map);

        return list;
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i(TAG, "--------onActivityCreated");

	}
}

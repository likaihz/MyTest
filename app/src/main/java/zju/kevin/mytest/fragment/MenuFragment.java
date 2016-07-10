package zju.kevin.mytest.fragment;

import zju.kevin.mytest.R;

import android.app.Activity;
import android.app.ListFragment;
//import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.ArrayAdapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MenuFragment extends ListFragment {

    private String TAG = MenuFragment.class.getName();
    //private ListView list ;
    //private SimpleAdapter adapter;
    /**
     * @描述 在onCreateView中加载布局
     * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "--------onCreateView");
        return inflater.inflate(R.layout.menu_fragment, container,false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String [] from =new String[] {"name", "price"};
        final int[] to = new int[] {R.id.dish_name, R.id.dish_price};
        Log.i(TAG, "--------onCreate");

        String[] list = {"dish 1","dish 2","dish 3","dish 4","dish 5"};
        SimpleAdapter adapter=new SimpleAdapter(this.getActivity(),
                getData(), R.layout.menu_item, from, to);
        this.setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//
//        System.out.println(l.getChildAt(position));
//        HashMap<String, Object> view= (HashMap<String, Object>) l.getItemAtPosition(position);
//        System.out.println(view.get("title").toString()+"+++++++++title");
//
//
//        Toast.makeText(getActivity(), TAG+l.getItemIdAtPosition(position), Toast.LENGTH_LONG).show();
//        System.out.println(v);
//        System.out.println(position);


    }


    private List<Map<String, Object>> getData() {
        List<Map<String ,Object>> list = new ArrayList<Map<String,Object>>();

//        for (int i = 0; i < strs.length; i++) {
//            Map<String, Object> map = new HashMap<String, Object>();
//            map.put("title", strs[i]);
//            list.add(map);
//        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "饺子");
        map.put("price", 20.00);
        list.add(map);
        map=new HashMap<String, Object>();
        map.put("name", "noodles");
        map.put("price", 100.00);
        list.add(map);
        //map.clear();

        return list;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "--------onActivityCreated");

    }


}

package zju.kevin.mytest.fragment;

import zju.kevin.mytest.R;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuFragment extends ListFragment {

    private String TAG = MenuFragment.class.getName();
    private List<Map<String, Object>> data;
    /**
     * @描述 在onCreateView中加载布局
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "--------onCreateView");
        return inflater.inflate(R.layout.menu_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "--------onCreate");

        data=getData();
        MenuItemAdapter adapter = new MenuItemAdapter(getActivity());
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        System.out.println("Click On List Item!!!");
        super.onListItemClick(l, v, position, id);
    }


    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "饺子");
        map.put("price", 20.00);
        map.put("img", R.drawable.ic_launcher);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("name", "noodles");
        map.put("price", 100.00);
        map.put("img", R.drawable.ic_launcher);
        list.add(map);

        return list;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "--------onActivityCreated");

    }

    static class ViewHolder {
        public ImageView img;
        public TextView price;
        public TextView name;
        public ImageView del;
        public ImageView edit;
    }
    public class MenuItemAdapter extends BaseAdapter {

        private LayoutInflater mInflater = null;
        MenuItemAdapter(Context context){
            super();
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int pos) {
            return pos;
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                holder = new ViewHolder();
                //根据自定义的Item布局加载布局
                convertView = mInflater.inflate(R.layout.menu_item, null);
                holder.img = (ImageView)convertView.findViewById(R.id.dish_img);
                holder.price = (TextView)convertView.findViewById(R.id.dish_price);
                holder.name = (TextView)convertView.findViewById(R.id.dish_name);
                holder.del = (ImageView)convertView.findViewById(R.id.delete_icon);
                holder.edit = (ImageView)convertView.findViewById(R.id.edit_icon);
                //将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
                convertView.setTag(holder);
            }
            else holder = (ViewHolder)convertView.getTag();

            holder.img.setImageResource((Integer)data.get(position).get("img"));
            holder.price.setText(String.valueOf(data.get(position).get("price")));
            holder.name.setText((String)data.get(position).get("name"));
            holder.del.setOnClickListener(new DelIconListener(position));
            holder.edit.setOnClickListener(new EditIconListener(position));

            return convertView;
        }

        class DelIconListener implements View.OnClickListener {
            private int position;
            DelIconListener(int pos) {position = pos;}
            @Override
            public void onClick(View view)
            {
                int vid=view.getId();
                System.out.println("Click on del icon!");
                Log.i("","Click on del icon!");
            }
        }

        class EditIconListener implements View.OnClickListener {
            private int position;
            EditIconListener(int pos) {position = pos;}
            @Override
            public void onClick(View view)
            {
                int vid = view.getId();
                System.out.println("Click on edit icon!");
                Log.i("","Click on edit icon!");
            }
        }
    }
}
package zju.kevin.mytest.fragment;

import zju.kevin.mytest.QRCodeActivity;
import zju.kevin.mytest.R;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderFragment extends ListFragment {

	private String TAG = OrderFragment.class.getName();
    private List<Map<String, Object>> data;
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

        data = getData();
        OrderItemAdapter adapter = new OrderItemAdapter(getActivity());
        setListAdapter(adapter);

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
        map.put("order_price",105.0);
        map.put("order_time", "2016-07-10 19:00");
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("order_id", "10000000");
        map.put("name", "shabi");
        map.put("contact", "10086");
        map.put("order_price",105.0);
        map.put("order_time", "2016-07-10 19:00");
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("order_id", "10000000");
        map.put("name", "shabi");
        map.put("contact", "10086");
        map.put("order_price",105.0);
        map.put("order_time", "2016-07-10 19:00");
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("order_id", "10000000");
        map.put("name", "shabi");
        map.put("contact", "10086");
        map.put("order_price",105.0);
        map.put("order_time", "2016-07-10 19:00");
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("order_id", "10000000");
        map.put("name", "shabi");
        map.put("contact", "10086");
        map.put("order_price",105.0);
        map.put("order_time", "2016-07-10 19:00");
        list.add(map);

        return list;
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i(TAG, "--------onActivityCreated");

	}

	static class ViewHolder {
        public TextView order_id;
        public TextView name;
        public TextView contact;
        public TextView order_price;
        public TextView order_time;
        public Button btn;
    }

    class OrderItemAdapter extends BaseAdapter{
        private LayoutInflater mInflater = null;

        //confirmed 变量应该动态从数据库获取
        private boolean confirmed = true;
        OrderItemAdapter(Context context){
            //this.mInflater = LayoutInflater.from(context);
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
            if (convertView == null) {
                holder = new ViewHolder();
                //根据自定义的Item布局加载布局
                convertView = mInflater.inflate(R.layout.order_item, null);
                holder.name = (TextView)convertView.findViewById(R.id.name);
                holder.order_id = (TextView)convertView.findViewById(R.id.order_id);
                holder.contact =(TextView)convertView.findViewById(R.id.contact);
                holder.order_price = (TextView)convertView.findViewById(R.id.order_price);
                holder.order_time=(TextView)convertView.findViewById(R.id.order_time);
                holder.btn = (Button)convertView.findViewById(R.id.order_btn);
                //将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
                convertView.setTag(holder);
            }
            else holder = (ViewHolder)convertView.getTag();

            holder.order_id.setText((String)data.get(position).get("order_id"));
            holder.name.setText((String)data.get(position).get("name"));
            holder.contact.setText((String)data.get(position).get("contact"));
            holder.order_price.setText(String.valueOf(data.get(position).get("order_price")));
            holder.order_time.setText((String)data.get(position).get("order_time"));
            /*btn 要单独处理，如果该订单已经被确认，则btn文字设为收款，否则设为确认*/
            if(!confirmed) holder.btn.setText("确认订单");
            else holder.btn.setText("收款");
            holder.btn.setOnClickListener(new OdrBtnListener(position));

            return convertView;
        }

        class OdrBtnListener implements View.OnClickListener{
            private int position;
            OdrBtnListener(int pos) {
                position = pos;
            }
            @Override
            public void onClick(View view)
            {
                int vid = view.getId();
                System.out.println("Click on the btn!");
                Log.i(String.valueOf(vid),"Click on the btn!");
                if(!confirmed) {
                    //此处发送确认订单消息

                    confirmed = true;
                    //刷新该fragment？？

                }

                else{
                    //此处通信获得a_id
                    String a_id = "12345678900";
                    //生成收款码
                    String code = new String((String)data.get(position).get("order_id")+"#"+a_id+"#"+String.valueOf(data.get(position).get("order_price")));
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), QRCodeActivity.class);
                    intent.putExtra("code",code);
                    startActivity(intent);
                }
            }
        }
    }
}

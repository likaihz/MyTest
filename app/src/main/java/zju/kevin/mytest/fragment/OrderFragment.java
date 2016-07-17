package zju.kevin.mytest.fragment;

import zju.kevin.mytest.EditDish;
import zju.kevin.mytest.OrderInfo;
import zju.kevin.mytest.QRCodeActivity;
import zju.kevin.mytest.R;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class OrderFragment extends ListFragment {

    private String urlstr = "http://139.129.6.166/proj/restaurant/";
	private String TAG = OrderFragment.class.getName();
    private List<Map<String, Object>> data = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();
    private String rmail = new String();
    private String a_id = new String();
    private String name = new String();
    OrderItemAdapter adapter;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    Handler handler = new Handler();

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
        if(getArguments().getString("rmail") != null) rmail = getArguments().getString("rmail");
        adapter = new OrderItemAdapter(getActivity());
        setListAdapter(adapter);

        //在新线程中连接服务器
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Log.i("","In the thread!");
                getOrders();    //网络数据请求，耗时操作
                Log.i("","get orders done!");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getData();          //处理得到的数据
                        adapter.notifyDataSetChanged();     //更新UI
                    }
                });
            }
        });
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

        Log.i("","Click On order list Item!!!");
        System.out.println("item clicked");
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent();
        intent.setClass(getActivity(), OrderInfo.class);
        intent.putExtra("order_id", (String)data.get(position).get("order_id"));
//        intent.putExtra("contact", (Double)data.get(position).get("contact"));
//        intent.putExtra("dish_img",(Uri)data.get(position).get("img"));
        intent.putExtra("rmail", rmail);
        intent.putExtra("urlstr",urlstr);
        startActivity(intent);
        //getActivity().finish();
	}


    private void getData() {
        if(orders == null) return;
        for(Order order : orders) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("order_id", order.order_id);
            //map.put("name", order.name);
            map.put("contact", order.contact);
            map.put("order_price", order.order_price);
            map.put("order_time", order.order_time);
            map.put("confirmed", order.confirmed);
            data.add(map);
        }
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i(TAG, "--------onActivityCreated");
	}

    //在工作线程中调用
    public void getOrders() {
        try{
            URL url = new URL(urlstr+"rorder.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();

            //POST请求向服务器输出
//            Map<String, String> map = new HashMap<>();
//            map.put("rmail",rmail);
            StringBuffer buf = new StringBuffer();
            buf.append("rmail").append("=").append(URLEncoder.encode(rmail, "UTF-8"));
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(buf.toString().getBytes());
            //读取服务器响应输入JSON数据
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String json;
            StringBuffer sb = new StringBuffer("");
            while ((json = reader.readLine()) != null) {
                json = URLDecoder.decode(json, "utf-8");
                sb.append(json);
            }
            reader.close();

            json = new String(sb);

            System.out.println(json);
            Gson gson = new Gson();
            JsonBean jsonBean = gson.fromJson(json, JsonBean.class);
            if(jsonBean.result.equals("1")){

                a_id = jsonBean.a_id;
                name = jsonBean.name;
                //Log.i("","result");
                for(JsonBean.J_order i: jsonBean.order) {
                    Order order = new Order(i.id, i.mail, Double.valueOf(i.t_price).doubleValue(),
                                            i.time, Integer.valueOf(i.issure).intValue());
                    orders.add(order);
                }
            }
        }
        catch (Exception e) {
            Log.i("thread expection!","1");
            return;
        }
    }

    public class JsonBean {
        public List<J_order> order;
        public String name;
        public String a_id;
        public String result;

        public class J_order {
            public String id;
            public String mail;
            public String time;
            public String t_price;
            public String ispaid;
            public String issure;
        }
    }

	static class ViewHolder {
        public TextView order_id;
        public TextView contact;
        public TextView order_price;
        public TextView order_time;
        public Button btn;
        public int confirmed;
    }

    class Order {
        public String order_id;
        //public String name;
        public String contact;
        public Double order_price;
        public String order_time;
        public int confirmed;
        Order(String i, String c, Double p, String t, int o){
            order_id = i;  contact = c;
            order_price = p; order_time = t; confirmed = o;
        }
    }

    class OrderItemAdapter extends BaseAdapter{
        private LayoutInflater mInflater = null;

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
                //holder.name = (TextView)convertView.findViewById(R.id.name);
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
            //holder.name.setText((String)data.get(position).get("name"));
            holder.contact.setText((String)data.get(position).get("contact"));
            holder.order_price.setText(String.valueOf(data.get(position).get("order_price")));
            holder.order_time.setText((String)data.get(position).get("order_time"));
            holder.confirmed = (Integer)data.get(position).get("confirmed");
            /*btn 要单独处理，如果该订单已经被确认，则btn文字设为收款，未确认则设为确认，否则设为不可点击*/
            //0为未确认, 1为已确认, 2为已取消, 3为已付款
            switch (holder.confirmed) {
                case 0:
                    holder.btn.setText("确认订单");
                    holder.btn.setOnClickListener(new OdrBtnListener(position));
                    break;
                case 1:
                    holder.btn.setText("收款");
                    holder.btn.setOnClickListener(new OdrBtnListener(position));
                    break;
                case 2:
                    holder.btn.setText("已取消");
                    holder.btn.setClickable(false);
                    break;
                case 3:
                    holder.btn.setText("已付款");
                    holder.btn.setClickable(false);
                    break;
            }
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
                int confirmed = (Integer)data.get(position).get("confirmed");
                Log.i(String.valueOf(vid),"Click on the btn!");
                if(confirmed == 0) {        //未确认订单
                    ((Button)view).setClickable(false);
                    //此处发送确认订单消息
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                URL url = new URL(urlstr+"r_sureorder.php");
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                                if(conn == null) {System.out.println("conn==null");}
                                conn.setDoOutput(true);
                                conn.setRequestMethod("POST");
                                conn.setConnectTimeout(5000);
                                conn.setReadTimeout(5000);
                                conn.setRequestProperty("Charset", "UTF-8");
                                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                                conn.connect();

                                StringBuffer buf = new StringBuffer();
                                buf.append("id").append("=").append(URLEncoder.encode((String)(data.get(position).get("order_id")), "UTF-8"));
                                System.out.println(buf);
                                OutputStream outputStream = conn.getOutputStream();
//                                if(outputStream == null) {System.out.println("os==null");}
                                outputStream.write(buf.toString().getBytes());
                                outputStream.flush();

                                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                String json;
                                StringBuffer sb = new StringBuffer("");
                                while ((json = reader.readLine()) != null) {
                                    json = URLDecoder.decode(json, "utf-8");
                                    sb.append(json);
                                }
                                reader.close();
                                System.out.println(sb);

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        data.get(position).put("confirmed", 1);
                                        adapter.notifyDataSetChanged();
                                        new AlertDialog.Builder(getActivity())
                                                .setMessage("订单已确认")
                                                .setPositiveButton("确定",null)
                                                .show();
                                    }
                                });
                            } catch (Exception e) {System.out.println("Exception in confirm");}

                        }
                    });
                }

                else{
                    //生成二维码，向数据库请求账户id和餐厅名称
//                    executorService.submit(new Runnable() {
//                        @Override
//                        public void run() {
//                            try{
//                                URL url = new URL(urlstr+"getid.php");
//                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
////                                if(conn == null) {System.out.println("conn==null");}
//                                conn.setDoOutput(true);
//                                conn.setRequestMethod("POST");
//                                conn.setConnectTimeout(5000);
//                                conn.setReadTimeout(5000);
//                                conn.setRequestProperty("Charset", "UTF-8");
//                                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//                                conn.connect();
//
//                                StringBuffer buf = new StringBuffer();
//                                buf.append("rmail").append("=").append(URLEncoder.encode(rmail, "UTF-8"));
//                                OutputStream outputStream = conn.getOutputStream();
//                                outputStream.write(buf.toString().getBytes());
//                                //读取服务器响应输入JSON数据
//                                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                                String json;
//                                StringBuffer sb = new StringBuffer("");
//                                while ((json = reader.readLine()) != null) {
//                                    json = URLDecoder.decode(json, "utf-8");
//                                    sb.append(json);
//                                }
//                                reader.close();
//
//                                json = new String(sb);
//                            } catch (Exception e) {}
//                        }
//                    });

                    //生成收款码
                    String code = new String((String)data.get(position).get("order_id")+"#"+a_id+"#"+String.valueOf(data.get(position).get("order_price")));
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), QRCodeActivity.class);
                    intent.putExtra("code",code);
                    intent.putExtra("name",name);
                    startActivity(intent);
                }
            }
        }
    }
}

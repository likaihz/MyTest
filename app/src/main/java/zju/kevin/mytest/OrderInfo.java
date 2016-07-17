package zju.kevin.mytest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
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

public class OrderInfo extends AppCompatActivity {

    private String rmail;
    private String order_id;
    private String urlstr;
    private double totalprice;
    private String mail;
    private String time;
    private List<Map<String, Object>> data = new ArrayList<>();
    private List<Orderd> orderds = new ArrayList<>();
    private InfoAdapter adapter;
    private Handler handler;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTitle("订单详情");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);
        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        rmail = bundle.getString("rmail");
        order_id = bundle.getString("order_id");
        urlstr = bundle.getString("urlstr");
        handler = new Handler();

        ((TextView)findViewById(R.id.order_id)).setText(order_id);
        ListView lv = (ListView)findViewById(R.id.odrinfo_list);
        adapter = new InfoAdapter(this);
        lv.setAdapter(adapter);

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Log.i("","in the thread");
                getInfo();              //请求信息
                Log.i("","get info done");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        getData();         //处理数据
                        Log.i("out",data.size()+"");
                        ((TextView)findViewById(R.id.contact)).setText(mail);
                        ((TextView)findViewById(R.id.order_time)).setText(time);
                        ((TextView)findViewById(R.id.order_price)).setText(String.valueOf(totalprice));
                        adapter.notifyDataSetChanged();
                    }
                });


            }
        });
    }



    public class InfoAdapter extends BaseAdapter{
        private LayoutInflater mInflater = null;
        InfoAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
//            super();
//            mInflater = (LayoutInflater) context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            Log.i("in",data.size()+"");
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            Log.i("likai","in getview");
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                //根据自定义的Item布局加载布局
                convertView = mInflater.inflate(R.layout.info_item, null);
                holder.dish_name = (TextView)convertView.findViewById(R.id.info_name);
                holder.price =(TextView)convertView.findViewById(R.id.info_price);
                holder.cnt = (TextView)convertView.findViewById(R.id.info_cnt);
                //将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
                convertView.setTag(holder);
            }
            else holder = (ViewHolder)convertView.getTag();

            holder.dish_name.setText((String)data.get(position).get("dish_name"));
            System.out.println((String)data.get(position).get("dish_name"));
            //holder.name.setText((String)data.get(position).get("name"));
            holder.price.setText(String.valueOf(data.get(position).get("price")));
            System.out.println(String.valueOf(data.get(position).get("price")));

            holder.cnt.setText(String.valueOf(data.get(position).get("cnt")));

            return convertView;
        }
    }
    public void getInfo() {
        try{
            System.out.println("In getInfo");
            URL url = new URL(urlstr+"rorderd.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();

            StringBuffer buf = new StringBuffer();
            buf.append("id").append("=").append(URLEncoder.encode(order_id, "UTF-8"));
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
                totalprice = Double.valueOf(jsonBean.t_price).doubleValue();
                mail = jsonBean.mail;
                time = jsonBean.time;

                for(JsonBean.JSON_Order i : jsonBean.orderd){
                    Orderd orderd = new Orderd(i.name, Double.valueOf(i.price).doubleValue(),
                                                Integer.valueOf(i.count).intValue());
                    orderds.add(orderd);
                }
            }
        } catch (Exception e) {System.out.println("In getInfo exception");}
    }

    public void getData() {
        if(orderds.isEmpty()) return;
        for(Orderd order : orderds) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("dish_name", order.name);
            //map.put("name", order.name);
            map.put("price", order.price);
            map.put("cnt", order.cnt);
            data.add(map);
        }
    }

    public class JsonBean {
        public String id;
        public String rmail;
        public String name;
        public String mail;
        public String issure;
        public String ispaid;
        public String time;
        public String t_price;
        public String t_count;
        public String adr;
        public List<JSON_Order> orderd;
        public String result;
        public class JSON_Order {
            public String name;
            public String count;
            public String price;
        }
    }

    public class Orderd {
        public String name;
        public double price;
        public int cnt;

        Orderd(String n, double p, int c) {
            name = n; price = p; cnt = c;
        }
    }

    static class ViewHolder {
        public TextView dish_name;
        public TextView price;
        public TextView cnt;

    }
}

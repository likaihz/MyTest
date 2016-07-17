package zju.kevin.mytest.fragment;

import zju.kevin.mytest.EditDish;
import zju.kevin.mytest.R;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

public class MenuFragment extends ListFragment{

    private String urlstr = "http://139.129.6.166/proj/restaurant/";
    private String rmail = new String();
    private String TAG = MenuFragment.class.getName();
    private List<Map<String, Object>> data = new ArrayList<>();
    private List<Dish> dishes = new ArrayList<>();
    MenuItemAdapter adapter;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    Handler handler = new Handler();
    /**
     * @描述 在onCreateView中加载布局
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "--------onCreateView");
        View view;
        view = inflater.inflate(R.layout.menu_fragment, container, false);
        //view.findViewById(R.id.add_icon).setOnClickListener(new AddIconListener());
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "--------onCreate");
        if(getArguments().getString("rmail") != null) rmail = getArguments().getString("rmail");
        adapter = new MenuItemAdapter(getActivity());
        setListAdapter(adapter);
        //引入线程池管理线程
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Log.i("","In the thread!");
                getDishes();    //网络数据请求，耗时操作
                Log.i("","get dished done!");
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
        Log.i("","Click On List Item!!!");
        super.onListItemClick(l, v, position, id);
    }

    private void getData() {
        if(dishes == null || dishes.isEmpty()) return;
        for(Dish dish : dishes) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", dish.Name);
            map.put("price", dish.Price);
            map.put("img", dish.Image);
            data.add(map);
        }
   }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "--------onActivityCreated");

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
            if(!data.isEmpty()){
                if(data.get(position).get("img")== null) holder.img.setBackgroundResource(R.drawable.ic_launcher);
                else holder.img.setImageURI((Uri)data.get(position).get("img"));
                holder.price.setText(String.valueOf(data.get(position).get("price")));
                holder.name.setText((String)data.get(position).get("name"));
                holder.del.setOnClickListener(new DelIconListener(position));
                holder.edit.setOnClickListener(new EditIconListener(position));
            }
            return convertView;
        }

        class EditIconListener implements View.OnClickListener {
            private int position;
            EditIconListener(int pos) {position = pos;}
            @Override
            public void onClick(View view)
            {
                //点击编辑图标跳转到编辑菜品界面
                int vid=view.getId();
                Log.i("","Click on edit icon!");
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), EditDish.class);
                        intent.putExtra("dish_name", (String)data.get(position).get("name"));
                        intent.putExtra("dish_price", (Double)data.get(position).get("price"));
                        intent.putExtra("dish_img",(Uri)data.get(position).get("img"));
                        intent.putExtra("rmail", rmail);
                        intent.putExtra("urlstr",urlstr);
                        startActivity(intent);
                    }
                });
                getActivity().finish();

            }
        }

        class DelIconListener implements View.OnClickListener {
            private int position;
            DelIconListener(int pos) {position = pos;}
            @Override
            public void onClick(View view)
            {
                int vid = view.getId();
                System.out.println("Click on del icon!");
                //删除菜品行为逻辑
                //先将按钮设为不可点击
                view.setClickable(false);
                //再开启线程，发送删除命令
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        //服务器删除接口
                        System.out.println("Delete icon pressed!");
                        try{
                            URL url = new URL(urlstr+"r_delmenu.php");
                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                            httpURLConnection.setDoOutput(true);
                            httpURLConnection.setRequestMethod("POST");
                            httpURLConnection.setConnectTimeout(5000);
                            httpURLConnection.setReadTimeout(5000);
                            httpURLConnection.setRequestProperty("Charset", "UTF-8");
                            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            httpURLConnection.connect();

                            //POST提交删除信息
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("rmail",rmail);
                            map.put("mname",URLEncoder.encode((String)data.get(position).get("name"),"UTF-8"));
                            //map.put("mname",(String)(data.get(position).get("namne")));
                            StringBuffer buf = new StringBuffer();
                            for(Map.Entry<String,String> entry : map.entrySet()){
                                buf.append(entry.getKey()).append("=")
                                        .append(URLEncoder.encode(entry.getValue(),"UTF-8")).append("&");
                                //.append(entry.getValue()).append("&");
                            }
                            buf.deleteCharAt(buf.length()-1);
                            OutputStream outputStream = httpURLConnection.getOutputStream();
                            if(outputStream !=null){
                                outputStream.write(buf.toString().getBytes());
                                outputStream.flush();
                                outputStream.close();

                                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                                String json;
                                StringBuffer sb = new StringBuffer("");
                                while ((json = reader.readLine()) != null) {
                                    json = URLDecoder.decode(json, "utf-8");
                                    sb.append(json);
                                }
                                reader.close();

                                json = new String(sb);
                                System.out.println(json);

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //更新UI
                                        data.remove(position);
                                        adapter.notifyDataSetChanged();
                                        //弹出提示框
                                        new AlertDialog.Builder(getActivity())
                                                .setMessage("删除成功！")
                                                .setPositiveButton("确定",null)
                                                .show();
                                    }
                                });
                            }
                            else { handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //更新UI
//                                    data.remove(position);
                                    adapter.notifyDataSetChanged();
                                    //弹出提示框
                                    new AlertDialog.Builder(getActivity())
                                            .setMessage("删除失败！")
                                            .setPositiveButton("确定",null)
                                            .show();
                                }
                            });}
                        }
                        catch (Exception e) {System.out.println("Del exception");}
                    }
                });
            }
        }
    }

    //在工作线程中调用！
    private void getDishes() {
        Log.i("","in getDishes!");
        //请求数据！！！
        try {
            URL url = new URL(urlstr+"rmenu.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();

            //POST请求
            Map<String, String> map = new HashMap<>();
            map.put("rmail",rmail);
            StringBuffer buf = new StringBuffer();
            buf.append("rmail").append("=").append(URLEncoder.encode(rmail, "UTF-8"));
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(buf.toString().getBytes());

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String json;
            StringBuffer sb = new StringBuffer("");
            while ((json = reader.readLine()) != null) {
                json = URLDecoder.decode(json, "utf-8");
                sb.append(json);
            }
            reader.close();

            json = new String(sb);
            //Log.i("",json);
            Gson gson = new Gson();
            JsonBean jsonBean = gson.fromJson(json, JsonBean.class);
            if(jsonBean.result.equals("1")){
                //Log.i("","result");
                for(JsonBean.J_dish i: jsonBean.menu) {
                    Bitmap bmp;
                    if(i.Image.equals("null")) bmp =  null;
                    else bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(i.Image));
                    Dish dish = new Dish(bmp, i.Name, Double.valueOf(i.Price).doubleValue());
                    dishes.add(dish);
                }
            }
            return;
        }

        catch (Exception e) {Log.i("","getDishes exception!"); return;}
    }

    public class JsonBean {
        public List<J_dish> menu;
        public String result;

        public  class J_dish {
            public String Name;
            public String Price;
            public String Image;
        }
    }

    private class Dish {
        public Bitmap Image;
        public String Name;
        public Double Price;
        Dish(Bitmap bmp, String name, Double price){
            Image = bmp;
            Name = name;
            Price = price;
        }
    }

    static class ViewHolder {
        public ImageView img;
        public TextView price;
        public TextView name;
        public ImageView del;
        public ImageView edit;
    }
}
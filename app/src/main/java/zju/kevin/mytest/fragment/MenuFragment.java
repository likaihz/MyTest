package zju.kevin.mytest.fragment;

import zju.kevin.mytest.EditDish;
import zju.kevin.mytest.R;
import zju.kevin.mytest.StreamTool;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MenuFragment extends ListFragment{

    private String TAG = MenuFragment.class.getName();
    private List<Map<String, Object>> data = new ArrayList<>();
    private List<Dish> dishes = new ArrayList<>();
    //private Thread mThread;
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
        return inflater.inflate(R.layout.menu_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "--------onCreate");
        adapter = new MenuItemAdapter(getActivity());
        setListAdapter(adapter);
        //if(mThread == null ||!mThread.isAlive()){
            //mThread = new Thread(){
        //引入线程池管理线程
        executorService.submit(new Runnable() {
                @Override
                public void run() {
                    getDishes();    //网络数据请求，耗时操作
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            getData();          //处理得到的数据
                            adapter.notifyDataSetChanged();     //更新UI
                        }
                    });
                }
            //};
            //mThread.start();
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
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("name", "饺子");
//        map.put("price", 20.00);
//        map.put("img", R.drawable.ic_launcher);
//        list.add(map);
//
//        return list;
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
                holder.img.setImageURI((Uri)data.get(position).get("img"));
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
                Log.i("","Click on del icon!");
                Intent intent = new Intent();
                intent.setClass(getActivity(), EditDish.class);
                intent.putExtra("dish_name", (String)data.get(position).get("name"));
                intent.putExtra("dish_price", (Double)data.get(position).get("price"));
                intent.putExtra("dish_img",(Uri)data.get(position).get("img"));
                startActivity(intent);
            }
        }

        class DelIconListener implements View.OnClickListener {
            private int position;
            DelIconListener(int pos) {position = pos;}
            @Override
            public void onClick(View view)
            {
                int vid = view.getId();
                Log.i("","Click on edit icon!");
                //删除菜品行为逻辑
                //先将按钮设为不可点击
                view.setClickable(false);
                //再开启线程，发送删除命令
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        //服务器删除接口
            /*          ==============          */
            /*          |            |          */
            /*          |            |          */
            /*          |   Delete!  |          */
            /*          |            |          */
            /*          |            |          */
            /*          ==============          */
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
                });
            }
        }
    }

    //在工作线程中调用！
    private void getDishes() {
        //请求数据！！！
        try {
            //List<Dish> dishes = new ArrayList<>();
            URL url = new URL("");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            InputStream in = conn.getInputStream();
            JSONArray array = new JSONArray(StreamTool.readInputStream(in));
            int len = array.length();
            for(int i = 0; i < len; i++){
                JSONObject object = array.getJSONObject(i);
                //这里要改！！不知道JSON中img域实际名称
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(object.getString("img")));
                Dish dish = new Dish(bmp, object.getString("name"),object.getDouble("price"));
                dishes.add(dish);
            }
            return;
        }

        catch (Exception e) { return;}
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
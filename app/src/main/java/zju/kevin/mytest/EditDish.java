package zju.kevin.mytest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditDish extends Activity {
    private String TAG = EditDish.class.getName();
    private String name;
    private Double price;
    private Bitmap img;
    private String rmail;
    private String urlstr;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "  onCreate");
        setContentView(R.layout.activity_edit_dish);
        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        name = bundle.getString("dish_name");
        price = bundle.getDouble("dish_price");
        rmail = bundle.getString("rmail");
        urlstr = bundle.getString("urlstr");
        ((EditText)findViewById(R.id.edit_name)).setText(name);
        ((EditText)findViewById(R.id.edit_price)).setText(String.valueOf(price));
        ((Button)findViewById(R.id.edit_btn)).setOnClickListener(new EditBtnListener());
        //Uri img_uri = Uri.parse(bundle.getString("dish_img"));

//        executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//
//                //获取菜品图片！！！！！
//            /*          ==============          */
//            /*          |            |          */
//            /*          |            |          */
//            /*          |  Picture!  |          */
//            /*          |            |          */
//            /*          |            |          */
//            /*          ==============          */
//
//                final Bitmap bmp = null;
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        img = bmp;
//                        ((ImageView)findViewById(R.id.dish_img_fill)).setImageBitmap(img);
//                    }
//                });
//            }
//        });
    }

    class EditBtnListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(urlstr+"r_updatemenu.php");
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
                        buf.append("rmail").append("=").append(URLEncoder.encode(rmail, "UTF-8")).append("&")
                        .append("mname").append("=").append(URLEncoder.encode(name, "UTF-8")).append("&")
                        .append("price").append("=").append(URLEncoder.encode(((EditText)findViewById(R.id.edit_price)).getText().toString(), "UTF-8"));
                        OutputStream outputStream = conn.getOutputStream();
                        System.out.println(buf);
                        outputStream.write(buf.toString().getBytes());

                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String json;
                        StringBuffer sb = new StringBuffer("");
                        while ((json = reader.readLine()) != null) {
                            json = URLDecoder.decode(json, "utf-8");
                            sb.append(json);
                        }
                        reader.close();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(EditDish.this)
                                        .setMessage("修改成功！")
                                        .setPositiveButton("确定",null)
                                        .show();
                                Intent intent = new Intent();
                                intent.setClass(EditDish.this, MainActivity.class);
                                intent.putExtra("rmail", rmail);
                                intent.putExtra("urlstr",urlstr);
                                startActivity(intent);
                                EditDish.this.finish();
                            }
                        });
                        return;
                    }
                    catch (Exception e){}
                }
            });
            //提交表单！！！
        }
    }
}

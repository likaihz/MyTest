package zju.kevin.mytest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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

public class AddDish extends AppCompatActivity {

    private String rmail;
    private String urlstr;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dish);
        setTitle("添加菜品");

        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        rmail = bundle.getString("rmail");
        urlstr = bundle.getString("urlstr");

        findViewById(R.id.add_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                            buf.append("rmail=").append(URLEncoder.encode(rmail, "UTF-8")).append("&")
                                    .append("name=").append(URLEncoder.encode(((EditText)findViewById(R.id.add_price)).getText().toString(), "UTF-8"))
                                    .append("price=").append(URLEncoder.encode(((EditText)findViewById(R.id.add_price)).getText().toString(), "UTF-8"));
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
                                    new AlertDialog.Builder(AddDish.this)
                                            .setMessage("添加成功！")
                                            .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which){
                                                    Intent intent = new Intent();
                                                    intent.setClass(AddDish.this, MainActivity.class);
                                                    intent.putExtra("rmail", rmail);
                                                    intent.putExtra("urlstr",urlstr);
                                                    startActivity(intent);
                                                    AddDish.this.finish();
                                                }})
                                            .show();


                                }
                            });
                            return;
                        }
                        catch (Exception e){}
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        intent.setClass(AddDish.this, MainActivity.class);
        intent.putExtra("rmail", rmail);
        intent.putExtra("urlstr",urlstr);
        startActivity(intent);
        AddDish.this.finish();
    }
}

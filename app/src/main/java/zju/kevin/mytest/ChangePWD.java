package zju.kevin.mytest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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

public class ChangePWD extends AppCompatActivity {
    private String rmail;
    private String urlstr;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        setTitle("修改密码");


        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        rmail = bundle.getString("rmail");
        urlstr = bundle.getString("urlstr");
        findViewById(R.id.chgpwd_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    URL url = new URL(urlstr+"changel.php");
                    Log.i("chgpwd url", urlstr);
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
                    buf.append("mail=").append(URLEncoder.encode(rmail, "UTF-8")).append("&")
                            .append("old=").append(URLEncoder.encode(((EditText)findViewById(R.id.old_pwd)).getText().toString(), "UTF-8")).append("&")
                            .append("new=").append(URLEncoder.encode(((EditText)findViewById(R.id.new_pwd)).getText().toString(), "UTF-8"));
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
                    json = new String(sb);
                    Log.i("json",json);
                    Gson gson = new Gson();
                    JsonBean jsonBean = gson.fromJson(json, JsonBean.class);
                    if(jsonBean.result.equals("1")) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(ChangePWD.this)
                                    .setMessage("修改成功！")
                                    .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which){
                                            Intent intent = new Intent();
                                            intent.setClass(ChangePWD.this, MainActivity.class);
                                            intent.putExtra("rmail", rmail);
                                            intent.putExtra("urlstr",urlstr);
                                            startActivity(intent);
                                            ChangePWD.this.finish();
                                        }})
                                    .show();


                        }
                    });}

                    else  new AlertDialog.Builder(ChangePWD.this)
                            .setMessage("密码错误！")
                            .setPositiveButton("确定",null)
                            .show();
                    return;
                }
                catch (Exception e){Log.i("chgowd","exception");}
            }
        });
    }

    public class JsonBean {
        public String result;
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        intent.setClass(ChangePWD.this, MainActivity.class);
        intent.putExtra("rmail", rmail);
        intent.putExtra("urlstr",urlstr);
        startActivity(intent);
        ChangePWD.this.finish();
    }
}

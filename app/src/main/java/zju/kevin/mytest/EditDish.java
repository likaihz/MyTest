package zju.kevin.mytest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditDish extends Activity {
    private String TAG = EditDish.class.getName();
    private String name;
    private Double price;
    private Bitmap img;
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
        ((EditText)findViewById(R.id.edit_name)).setText(name);
        ((EditText)findViewById(R.id.edit_price)).setText(String.valueOf(price));
        Uri img_uri = Uri.parse(bundle.getString("dish_img"));

        executorService.submit(new Runnable() {
            @Override
            public void run() {

                //获取菜品图片！！！！！
            /*          ==============          */
            /*          |            |          */
            /*          |            |          */
            /*          |  Picture!  |          */
            /*          |            |          */
            /*          |            |          */
            /*          ==============          */

                final Bitmap bmp = null;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        img = bmp;
                        ((ImageView)findViewById(R.id.dish_img_fill)).setImageBitmap(img);
                    }
                });
            }
        });
    }

    class EditBtnListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            //提交表单！！！
        }
    }
}

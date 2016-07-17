package zju.kevin.mytest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

public class QRCodeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);

        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        Bitmap QR = CreateQRCode(bundle.getString("code"));
        //(TextView)(findViewById(R.id.res_name)).setText(bundle.getString("name"));
        TextView tv = (TextView)findViewById(R.id.res_name);
        tv.setText(bundle.getString("name"));
        ImageView iv = (ImageView) findViewById(R.id.charge_qrcode);
        iv.setImageBitmap(QR);
    }


    public Bitmap CreateQRCode(String code){
        try
        {
            int QR_WIDTH = 700, QR_HEIGHT = 700;
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(code, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++)
            {
                for (int x = 0; x < QR_WIDTH; x++)
                {
                    if (bitMatrix.get(x, y))
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    else
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            return bitmap;
        }
        catch (WriterException e){return null;}
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

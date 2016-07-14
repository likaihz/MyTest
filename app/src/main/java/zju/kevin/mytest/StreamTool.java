package zju.kevin.mytest;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class StreamTool {
    public static String readInputStream(InputStream inStream){
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while( (len = inStream.read(buffer)) !=-1 ){
                outStream.write(buffer, 0, len);
            }
            byte[] data = outStream.toByteArray();//网页的二进制数据
            outStream.close();
            inStream.close();
            return new String(data,"UTF-8");
        }
        catch (Exception e) {return null;}
    }
}

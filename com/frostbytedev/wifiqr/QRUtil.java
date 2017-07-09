package com.frostbytedev.wifiqr;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRUtil {
    SharedPreferences prefs;

    public Bitmap create(String WIFIQRCODE, SharedPreferences prefs) {
        String str = prefs.getString("qrcodecolor", "#000000");
        String str2 = prefs.getString("qrcodebackgroundcolor", "#FFFFFF");
        Bitmap bmp = null;
        try {
            BitMatrix bitMatrix = new QRCodeWriter().encode(WIFIQRCODE, BarcodeFormat.QR_CODE, 300, 300);
            int height = bitMatrix.getHeight();
            int width = bitMatrix.getWidth();
            bmp = Bitmap.createBitmap(width, height, Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.parseColor(str) : Color.parseColor(str2));
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bmp;
    }
}

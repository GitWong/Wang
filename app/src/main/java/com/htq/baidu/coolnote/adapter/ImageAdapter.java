package com.htq.baidu.coolnote.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.htq.baidu.coolnote.R;
import com.htq.baidu.coolnote.entity.Photo;
import com.htq.baidu.coolnote.widget.GalleryView;

public class ImageAdapter extends BaseAdapter {
    private ImageView[] mImages;        //

    private Context mContext;
    private List<Photo> photos;
    public List<Map<String, Object>> list;


   /* public Integer[] imgs = {R.drawable.image01, R.drawable.image02, R.drawable.image03,
            R.drawable.image04, R.drawable.image05};
    public String[] titles = {"美图01", "美图02", "美图03", "美图04", "美图05", "美图06", "美图07"};*/

    public ImageAdapter(Context c, List<Photo> photos) {
        this.mContext = c;
        this.photos = photos;


       /* list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < imgs.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("image", imgs[i]);
            list.add(map);
        }*/

        mImages = new ImageView[photos.size()];
    }

    /**
     *
     */
    public boolean createReflectedImages() {


        ContentResolver resolver = mContext.getContentResolver();
        final int reflectionGap = 4;
        final int Height = 200;
        int index = 0;
        for (int j = 0; j < mImages.length; j++) {

//            Integer id = (Integer) list.get(j).get("image");
     /*      Bitmap originalImage = null;
            try {
               originalImage = MediaStore.Images.Media.getBitmap(resolver, Uri.parse(photos.get(j).getUrl()));
//                originalImage = BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(photos.get(j).getUrl())));
//                originalImage = BitmapFactory.decodeFile(Environment.getExternalStorageState()+"/"+"DCIM/Camera");
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            Bitmap originalImage = BitmapFactory.decodeFile(photos.get(j).getUrl());
//            Bitmap originalImage = BitmapFactory.decodeResource(mContext.getResources(), id);
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();
            float scale = Height / (float) height;

            Matrix sMatrix = new Matrix();
            sMatrix.postScale(scale, scale);
            Bitmap miniBitmap = Bitmap.createBitmap(originalImage, 0, 0,
                    originalImage.getWidth(), originalImage.getHeight(), sMatrix, true);


            originalImage.recycle();

            int mwidth = miniBitmap.getWidth();
            int mheight = miniBitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.preScale(1, -1);
            Bitmap reflectionImage = Bitmap.createBitmap(miniBitmap, 0, mheight / 2, mwidth, mheight / 2, matrix, false);    // ��ȡԭͼ�°벿��
            Bitmap bitmapWithReflection = Bitmap.createBitmap(mwidth, (mheight + mheight / 2), Config.ARGB_8888);            // ������ӰͼƬ���߶�Ϊԭͼ3/2��

            Canvas canvas = new Canvas(bitmapWithReflection);
            canvas.drawBitmap(miniBitmap, 0, 0, null);
            Paint paint = new Paint();
            canvas.drawRect(0, mheight, mwidth, mheight + reflectionGap, paint);
            canvas.drawBitmap(reflectionImage, 0, mheight + reflectionGap, null);

            paint = new Paint();
            LinearGradient shader = new LinearGradient(0, miniBitmap.getHeight(), 0, bitmapWithReflection.getHeight()
                    + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
            paint.setShader(shader);
            paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
            canvas.drawRect(0, mheight, mwidth, bitmapWithReflection.getHeight() + reflectionGap, paint);        // ���Ƶ�Ӱ����ӰЧ��

            ImageView imageView = new ImageView(mContext);
            imageView.setImageBitmap(bitmapWithReflection);
            imageView.setLayoutParams(new GalleryView.LayoutParams((int) (width * scale),
                    (int) (mheight * 3 / 2.0 + reflectionGap)));
            imageView.setScaleType(ScaleType.MATRIX);
            mImages[index++] = imageView;
        }
        return true;
    }

    @Override
    public int getCount() {
        return mImages.length;
    }

    @Override
    public Object getItem(int position) {
        return mImages[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mImages[position];
    }

    public float getScale(boolean focused, int offset) {
        return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
    }

}

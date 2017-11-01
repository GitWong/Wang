package com.htq.baidu.coolnote.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.htq.baidu.coolnote.R;
import com.htq.baidu.coolnote.adapter.CardAdapter;
import com.htq.baidu.coolnote.adapter.ImageAdapter;
import com.htq.baidu.coolnote.library.CardScaleHelper;
import com.htq.baidu.coolnote.utils.SharepUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xinghongfei on 16/8/20.
 */
public class MeiziFragment extends Fragment {


    /* @BindView(R.id.tvTitle)
     TextView tvTitle;
     @BindView(R.id.mygallery)
     GalleryView mygallery;*/
    @BindView(R.id.blurView)
    ImageView blurView;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.iv_bg)
    ImageView ivBg;
    @BindView(R.id.pd)
    ProgressBar pd;
    private CardScaleHelper mCardScaleHelper = null;

    private ImageAdapter adapter;
    private Context context;
    private List<String> photos;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:

                    if (photos.size() > 0) {
                        ivBg.setVisibility(View.GONE);
                        pd.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        intialDate();
                    }

                    break;
                case 1:
                    Toast.makeText(context, "该文件夹没有资源", Toast.LENGTH_SHORT).show();
                    break;
            }


            return false;
        }
    });


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.meizi_fragment_layout, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }*/
        initialView();


        super.onViewCreated(view, savedInstanceState);
    }

    private void intialDate() {


        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(new CardAdapter(photos, context));
        // mRecyclerView绑定scale效果
        mCardScaleHelper = new CardScaleHelper();
        mCardScaleHelper.setCurrentItemPos(2);
        mCardScaleHelper.attachToRecyclerView(mRecyclerView);
    }


    private void initialView() {


        new Thread() {
            @Override
            public void run() {
                super.run();

                final String filePath = Environment.getExternalStorageDirectory().toString() + File.separator
                        + "DCIM";
                photos = new ArrayList<>();

                if (SharepUtils.getString(getContext(), "fpath", "kugou") != null) {
                    getImagePathFromSD(Environment.getExternalStorageDirectory().toString() + File.separator
                            + SharepUtils.getString(getContext(), "fpath", "kugou"));
                } else {
                    getImagePathFromSD(filePath);
                }

                handler.sendEmptyMessage(0);


            }
        }.start();


    }

    private ProgressDialog mProgressDialog;

    /**
     * 从sd卡获取图片资源
     *
     * @return
     */
    private void getImagePathFromSD(String filePath) {

        // 得到sd卡内image文件夹的路径   File.separator(/)
        // 得到该路径文件夹下所有的文件
        File fileAll = new File(filePath);
        File[] files = fileAll.listFiles();
        // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
//        Log.i("66", files.length + "文件的个数");
        if (files == null) {
            handler.sendEmptyMessage(1);
            return;
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                getImagePathFromSD(file.getPath());
            }
            if (checkIsImageFile(file.getPath())) {


                photos.add(file.getPath());

//                imagePathList.add(file.getPath());
            }
        }

        // 返回得到的图片列表

    }

    /**
     * 检查扩展名，得到图片格式的文件
     *
     * @param fName 文件名
     * @return
     */
    @SuppressLint("DefaultLocale")
    private boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("jpg") || FileEnd.equals("png")
                || FileEnd.equals("jpeg") || FileEnd.equals("bmp")) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

}

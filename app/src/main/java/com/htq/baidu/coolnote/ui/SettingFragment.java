package com.htq.baidu.coolnote.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.htq.baidu.coolnote.R;
import com.htq.baidu.coolnote.server.MusicServer;
import com.htq.baidu.coolnote.utils.SharepUtils;
import com.htq.baidu.coolnote.utils.SystemUtils;
import com.htq.baidu.coolnote.widget.ToggleButton;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by htq on 2016/8/12.
 */
public class SettingFragment extends Fragment {
    @BindView(R.id.tb_lock)
    ToggleButton mTbLock;
    @BindView(R.id.tb_tran)
    ToggleButton mTbTran;
    @BindView(R.id.lin)
    LinearLayout mLinL;
    @BindView(R.id.tb_music)
    ToggleButton tbMusic;
    @BindView(R.id.et_file)
    EditText etFile;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container,
                false);

        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        SharepUtils.putString(getContext(), "fpath", etFile.getText().toString());
    }

    @Override
    public void onStart() {
        initData();
        super.onStart();
    }


    private void initData() {

        if (SharepUtils.getString(getContext(), "fpath", null) != null) {
            etFile.setText(SharepUtils.getString(getContext(), "fpath", null));
        }

        SystemUtils util = new SystemUtils(getActivity());
        boolean isSetLock = util.getBoolean("isSetLock");
        if (isSetLock)
            mTbLock.setToggleOn();
        else
            mTbLock.setToggleOff();
        mTbTran.toggleOn();
        boolean isTran = util.getBoolean("isTran");

        if (isTran) {
            mTbTran.setToggleOn();
            mLinL.setAlpha(0.55f);
        } else {
            mTbTran.setToggleOff();

        }

        boolean isMusic = util.getBoolean("isMusic");
        if (isMusic) {
            tbMusic.setToggleOn();
            mLinL.setAlpha(0.55f);
        } else {
            tbMusic.setToggleOff();

        }


    }

    private void initView() {
        mTbLock.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                // AppContext.setLoadImage(on);
                if (on) {

                    Intent intent = new Intent(getActivity(), SetLockActivity.class);
                    startActivity(intent);
                } else {
                    new SystemUtils(getActivity()).setBoolean("isSetLock", false);
                }
            }
        });
        mTbTran.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                // AppContext.setLoadImage(on);
                if (on) {

                    new SystemUtils(getActivity()).setBoolean("isTran", true);
                    mLinL.setAlpha(0.55f);
                } else {
                    new SystemUtils(getActivity()).setBoolean("isTran", false);
                    mLinL.setAlpha(1);
                }
            }
        });
        tbMusic.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                // AppContext.setLoadImage(on);
                if (on) {

                    new SystemUtils(getActivity()).setBoolean("isMusic", true);
                    getActivity().startService(new Intent(getActivity(), MusicServer.class));

                } else {
                    new SystemUtils(getActivity()).setBoolean("isMusic", false);
                    getActivity().stopService(new Intent(getActivity(), MusicServer.class));

                }
            }
        });


    }

}

package com.htq.baidu.coolnote.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.htq.baidu.coolnote.R;
import com.htq.baidu.coolnote.db.NoteDatabase;
import com.htq.baidu.coolnote.entity.UpdataEvent;
import com.htq.baidu.coolnote.entity.User;
import com.htq.baidu.coolnote.fragment.MeiziFragment;
import com.htq.baidu.coolnote.server.MusicServer;
import com.htq.baidu.coolnote.utils.Constants;
import com.htq.baidu.coolnote.utils.SharepUtils;
import com.htq.baidu.coolnote.utils.SystemUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileFilter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //此处不能定义为全局的，否则在调用changFragment函数时会报java.lang.IllegalStateException: commit already called
//    private   FragmentManager fm=getSupportFragmentManager();
//     private  FragmentTransaction ft=fm.beginTransaction();

    protected FloatingActionButton fab;
    private boolean isFirstUse;
    protected DrawerLayout drawer;
    private long mBackPressedTime = 0;
    private NoteBookFragment noteBookFragment;
    private ImageView headIcon;
    private View baseView;
    String avatarFile = "avatarIcon.png";
    private TextView tv_name;
    private TextView tv_des;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isFirstUse = new SystemUtils(MainActivity.this).isFirstUse();

        if (isFirstUse) {
            initIntruduceData();
        }
        SystemUtils util = new SystemUtils(this);
        boolean isMusic = util.getBoolean("isMusic");
        if (isMusic) {
            startService(new Intent(this, MusicServer.class));
        }

        initMainFragment();
        initUi();
        initHead();
        initBgPic();
        //  User.refreshAvatarFromLocal(BmobConstants.MyAvatarDir+"avatarIcon.png",headIcon);
        //注册EventBus
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, MusicServer.class));
    }

    /**
     * 用于刷新头像的
     *
     * @param event
     */
    @Subscribe
    public void onEventBackgroundThread(UpdataEvent event) {
        if (event.getType() == UpdataEvent.UPDATE_USER_INFOS) {
            User.initDefaultAvatar(this, headIcon);
            //Snackbar.make(headIcon,"update",Snackbar.LENGTH_LONG).show();
            // mPresenter.setUserNickName();


        }
    }


    private void initUi() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    toggleActionButton();
                Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(NoteEditFragment.NOTE_FROMWHERE_KEY,
                        NoteEditFragment.QUICK_DIALOG);
                intent.putExtra(Constants.BUNDLE_KEY_ARGS, bundle);
                startActivity(intent);
            }
        });
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        baseView = navigationView.getHeaderView(0);
        headIcon = (ImageView) baseView.findViewById(R.id.imageView);
        tv_name = (TextView) baseView.findViewById(R.id.tv_name);
        tv_des = (TextView) baseView.findViewById(R.id.tv_des);


        headIcon.setOnClickListener(headIconOnTouchListener);


    }

    private View.OnClickListener headIconOnTouchListener = new View.OnClickListener() {
        @Override
        public void onClick(View vt) {

            Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
            startActivity(intent);

        }
    };

    private void initBgPic() {

        SystemUtils systemUtils = new SystemUtils(this);
        String path = systemUtils.getPath();
        if (path != null) {
            Bitmap bitmap = systemUtils.getBitmapByPath(this, path);
            if (bitmap != null) {
                drawer.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));

            }
        }else
        {
            drawer.setBackgroundDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(this.getResources(), R.drawable.one)));
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        initHead();

    }

    private void initHead() {

        if (SharepUtils.getString(this, "hpath", null) != null) {
            headIcon.setImageBitmap(BitmapFactory.decodeFile(SharepUtils.getString(this, "hpath", null)));
            Log.i("66", SharepUtils.getString(this, "hpath", null));
        } else {
            headIcon.setImageResource(R.drawable.logo);
        }

        if (SharepUtils.getString(this, "name", null) != null) {
            tv_name.setText(SharepUtils.getString(this, "name", null));
        }
        if (SharepUtils.getString(this, "des", null) != null) {
            tv_des.setText(SharepUtils.getString(this, "des", null));
        }

    }

    private void initMainFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        noteBookFragment = new NoteBookFragment();

        ft.replace(R.id.main_fraglayout, noteBookFragment, null);
        ft.commit();
    }


    protected void changeFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_fraglayout, fragment, null);

        //    ft.addToBackStack(fragment.toString());
        ft.commit();

    }


    private void initIntruduceData() {
        NoteDatabase noteDb = new NoteDatabase(MainActivity.this);
        noteDb.insertIntroduce(this);
        new SystemUtils(MainActivity.this).set("isFirstUse", "false");
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            long curTime = SystemClock.uptimeMillis();
            if ((curTime - mBackPressedTime) < (2 * 1000)) {
                finish();
                System.exit(0);
            } else {
                mBackPressedTime = curTime;
                Snackbar.make(drawer, "双击退出程序", Snackbar.LENGTH_LONG).show();
            }
            //   super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_change_bg) {

            setTitle("更换皮肤");
            ChangeBgFragment changeBgFragment = new ChangeBgFragment();
            changeFragment(changeBgFragment);
            fab.hide();
        }
        if (id == R.id.nav_photo) {

            setTitle("相册");
            MeiziFragment photofragment = new MeiziFragment();
            changeFragment(photofragment);
            fab.hide();
        } else if (id == R.id.nav_home) {
            setTitle("笔记");
            initMainFragment();

        } else if (id == R.id.nav_setting) {
            setTitle("设置");
            SettingFragment settingFragment = new SettingFragment();
            changeFragment(settingFragment);
            fab.hide();

        } else if (id == R.id.nav_about) {

            setTitle("光与影");
            AboutAppFragment aboutAppFragment = new AboutAppFragment();
            changeFragment(aboutAppFragment);
            fab.hide();
        } else if (id == R.id.nav_exit) {
            this.finish();
            System.exit(0);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

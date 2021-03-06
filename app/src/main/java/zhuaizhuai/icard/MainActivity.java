package zhuaizhuai.icard;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bean.Leftlist;
import bean.Tab;
import fragment.Fragment1;
import fragment.Fragment2;
import fragment.Fragment3;
import fragment.Fragment4;
import fragment.Fragment5;
import fragment.aboutFragment;

public class MainActivity extends AppCompatActivity
{
    private Message msg = new Message();
    private long exitTime = 0;//双击退出
    private LayoutInflater layoutInflater;
    private List<Tab> mTabs = new ArrayList<Tab>(4);
    private Leftlist leftlist = new Leftlist();

    //项目其他class public实例化
    public DrawerLayout drawerLayout;
    public zhuaizhuai.icard.FragmentTabHost mTabhost;
    public Tooltitle tooltitle;
    public TextView toolbartitle;
    public ImageButton toolbarright;
    public ImageButton toolbarback;
    public Fragment1 fragment1;
    public ListView listview;
    public ImageView leftImageview;
    public String yonghuming;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        tooltitle = (Tooltitle) findViewById(R.id.toolbar);
        setSupportActionBar(tooltitle);
        toolbartitle = (TextView) findViewById(R.id.tv_nav_title);
        toolbarright = (ImageButton) findViewById(R.id.iv_nav_right);
        toolbarright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        toolbarback = (ImageButton) findViewById(R.id.iv_nav_back);
        toolbarback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mTabhost.setCurrentTab(0);
            }
        });

        yonghuming = getIntent().getStringExtra("yonghuming");
        if (yonghuming.equals(""))
        {
            try
            {
                String sqlstr = "select yonghuming from qq";
                Cursor cursor = Splash.splashthis.db.rawQuery(sqlstr, null);
                cursor.move(1);
                yonghuming = cursor.getString(0);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        TextView guanyu = (TextView) findViewById(R.id.guanyu);
        guanyu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                aboutFragment aboutFragment = new aboutFragment();
                aboutFragment.show(getFragmentManager(),"aboutfragment");
            }
        });

        TextView tuichu = (TextView) findViewById(R.id.tuichu);
        tuichu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                getApplication().deleteDatabase("Icardsqlite");
                finish();
                System.exit(0);

            }
        });

        TextView erweima = (TextView) findViewById(R.id.erweima);
        erweima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this,com.dtr.zxing.activity.CaptureActivity.class);
                startActivity(intent);
            }
        });

        TextView xuanfuchuang = (TextView)findViewById(R.id.xuanfuchuang);
        xuanfuchuang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (Settings.canDrawOverlays(MainActivity.this)) {
                        Intent intent = new Intent(getApplicationContext(),FloatWindowService.class);
                        startService(intent);
                    } else {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(),FloatWindowService.class);
                    startService(intent);
                }
            }
        });

        fragment1 = new Fragment1();

        //Tabhost
        layoutInflater = LayoutInflater.from(this);
        mTabhost = (zhuaizhuai.icard.FragmentTabHost) this.findViewById(android.R.id.tabhost);
        mTabhost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        leftImageview = (ImageView) findViewById(R.id.leftimageView);
        initTab();
    }


    //tabhost添加图标
    private void initTab()
    {
        Tab tab_home = new Tab("首页", R.drawable.tab_home_btn, Fragment1.class);
        Tab tab_tianjia = new Tab("添加", R.drawable.tab_tianjia_btn, Fragment5.class);
        Tab tab_message = new Tab("记录", R.drawable.tab_message_btn, Fragment2.class);
        Tab tab_yipai = new Tab("消息", R.drawable.tab_yipai_btn, Fragment3.class);
//        Tab tab_user = new Tab("设置", R.drawable.tab_user_btn, Fragment4.class);

        mTabs.add(tab_home);
        mTabs.add(tab_tianjia);
        mTabs.add(tab_message);
        mTabs.add(tab_yipai);
//        mTabs.add(tab_user);

        for (Tab tab : mTabs)
        {
            TabHost.TabSpec tabspec = mTabhost.newTabSpec(tab.getTitle());
            View view = layoutInflater.inflate(R.layout.tab_indicator, null);
            ImageView img = (ImageView) view.findViewById(R.id.imageview);
            TextView text = (TextView) view.findViewById(R.id.textview);
            img.setImageDrawable(getResources().getDrawable(tab.getIcom()));

            text.setText(tab.getTitle());
            tabspec.setIndicator(view);
            mTabhost.addTab(tabspec, tab.getFragment(), null);
        }
        //取消分割线
        mTabhost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        //默认选择第一个
        mTabhost.setCurrentTab(0);
    }

    //双击退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if ((System.currentTimeMillis() - exitTime) > 2000)
            {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else
            {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

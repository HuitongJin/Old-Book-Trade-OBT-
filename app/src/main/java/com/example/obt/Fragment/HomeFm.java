package com.example.obt.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.obt.Activity.MainActivity;
import com.example.obt.Activity.SearchActivity;
import com.example.obt.Adapter.BookHomeAdapter;
import com.example.obt.Adapter.BookShopAdapter;
import com.example.obt.Adapter.HomeAdapter;
import com.example.obt.Class.BookShop;
import com.example.obt.Mysql.DBConnectHelper;
import com.example.obt.R;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HomeFm extends Fragment implements ViewPager.OnPageChangeListener, View.OnTouchListener {

    private static final int VIEW_PAGER_DELAY = 2000;    // 轮播延时
    private static final int INIT_FINISH = -200;
    private HomeAdapter mAdapter;   // ViewPager的适配器
    private List<ImageView> mItems; // 轮播的图片
    private ImageView[] mBottomImages;  // 轮播的ImageView
    private LinearLayout mBottomLiner;  // 底部显示圆点的Layout
    private static ViewPager mViewPager;    // 轮播的ViewPager
    private static int currentViewPagerItem;    // 当前播放的图片
    private static boolean isAutoPlay;  // 是否自动轮播
    private Thread mThread; // 子线程，用于计时
    private EditText searchEdit;    // 搜索框
    private View mview; // HomeFm对应的view
    private QMUITopBarLayout topbar;    // 沉浸式标题栏
    private RecyclerView recyclerView;
    private BookHomeAdapter bookHomeAdapter;
    private List<BookShop> bookShops = new ArrayList<>();
    private String phone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mview = inflater.inflate(R.layout.fm_home, container, false);
        /***
         * 设置沉浸式标题
         */
        QMUIStatusBarHelper.translucent(getActivity());
        topbar = (QMUITopBarLayout)mview.findViewById(R.id.topbar);
        topbar.setTitle("OBT旧书交易平台").setTextColor(ContextCompat.getColor(getActivity(), R.color.qmui_config_color_white));
        topbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.qmui_btn_blue_bg));

        /***
         * 配置轮播图ViewPager
         */
        mViewPager = mview.findViewById(R.id.vp_pager);
        mItems = new ArrayList<>();
        mAdapter = new HomeAdapter(mItems, mview.getContext());
        mViewPager.setAdapter(mAdapter);    // 配置适配器
        mViewPager.setOnTouchListener(this);    // 设置点击监听
        mViewPager.addOnPageChangeListener(this);   // 添加页面变化监听
        isAutoPlay = true;  // 设置自动轮播

        /***
         * 添加ImageView
         */
        addImageView();
        mAdapter.notifyDataSetChanged(); // 通知适配器数据发生变化

        /***
         * 设置底部小点
         */
        setBottomIndicator();

        /***
         * 设置Editext点击事件
         */
        searchEdit = mview.findViewById(R.id.search_edit);
        searchEdit.setFocusable(false);
        searchEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = this.getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        phone = bundle.getString("Phone");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView = mview.findViewById(R.id.home_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        bookHomeAdapter = new BookHomeAdapter(bookShops, phone);
        recyclerView.setAdapter(bookHomeAdapter);

        return mview;
    }

    /***
     * 添加ImageView
     */
    private void addImageView() {
        ImageView view0 = new ImageView(mview.getContext());
        view0.setImageResource(R.drawable.news1);
        ImageView view1 = new ImageView(mview.getContext());
        view1.setImageResource(R.drawable.news2);
        ImageView view2 = new ImageView(mview.getContext());
        view2.setImageResource(R.drawable.news3);
        ImageView view3 = new ImageView(mview.getContext());
        view3.setImageResource(R.drawable.news4);
        ImageView view4 = new ImageView(mview.getContext());
        view4.setImageResource(R.drawable.news5);

        view0.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view2.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view3.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view4.setScaleType(ImageView.ScaleType.CENTER_CROP);

        mItems.add(view0);
        mItems.add(view1);
        mItems.add(view2);
        mItems.add(view3);
        mItems.add(view4);
    }

    /***
     * 设置底部小点
     */
    private void setBottomIndicator() {
        mBottomLiner = mview.findViewById(R.id.ll_container);
        mBottomImages = new ImageView[mItems.size()];
        for (int i=0; i<mBottomImages.length; i++) {
            ImageView imageView = new ImageView(mview.getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(15, 15);
            params.setMargins(5, 0, 5, 0);
            imageView.setLayoutParams(params);

            // 如果当前是第一个，设置为选中状态
            if (i == 0) {
                imageView.setImageResource(R.drawable.point_select);
            }else {
                imageView.setImageResource(R.drawable.point_normal);
            }
            mBottomImages[i] = imageView;
            // 添加到父容器
            mBottomLiner.addView(imageView);
        }

        //   让其在最大值的中间开始滑动， 一定要在mBottomImages初始化之前完成
        int mid = HomeAdapter.MAX_SCROLL_VALUE / 2;
        mViewPager.setCurrentItem(mid);
        currentViewPagerItem = mid;

        //    定时发送消息
        mThread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    try {
                        Thread.sleep(VIEW_PAGER_DELAY);
                        MyHandler.sendEmptyMessage(0);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //  MyHandler.sendEmptyMessage(0);
                }
            }
        };
        mThread.start();
    }

    /***
     * ViewPager的监听事件
     * @param position 当前位置
     * @param positionOffset 偏移量
     * @param positionOffsetPixels 偏移间距
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentViewPagerItem = position;
        if (mItems != null) {
            position %= mBottomImages.length;
            int total = mBottomImages.length;

            for (int i=0; i<total; i++) {
                if (i == position) {
                    mBottomImages[i].setImageResource(R.drawable.point_select);
                }else{
                    mBottomImages[i].setImageResource(R.drawable.point_normal);
                }
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                isAutoPlay = false;
                break;
            case MotionEvent.ACTION_UP:
                isAutoPlay = true;
                break;
        }
        view.performClick();
        return false;
    }

    @SuppressLint("HandlerLeak")
    Handler MyHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    WeakReference<MainActivity> mainActivityWeakReference = new WeakReference<MainActivity>((MainActivity)getActivity());
                    MainActivity activity = mainActivityWeakReference.get();
                    if (isAutoPlay){
                        mViewPager.setCurrentItem(++currentViewPagerItem);
                    }
                    break;
                case INIT_FINISH:
                    bookShops = (List) msg.obj;
                    bookHomeAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection = DBConnectHelper.getConn();
                String sql = "SELECT BID, IMAGE, BNAME, PRICE, AUTHOR, PRESS FROM BOOK WHERE SOLD=0";
                Statement st;
                List<BookShop> books = new ArrayList<>();
                try {
                    st = connection.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    while (rs.next()) {
                        // 读取数据
                        int BID = rs.getInt("BID");
                        byte[] imageBytes = Base64.decode(rs.getString("IMAGE"), Base64.DEFAULT);
                        Bitmap book_image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        String book_name = rs.getString("BNAME");
                        String book_author = rs.getString("AUTHOR");
                        String book_press = rs.getString("PRESS");
                        Double book_price = rs.getDouble("PRICE");

                        BookShop bookShop = new BookShop(BID, book_name, book_image, book_author, book_press, book_price);
                        books.add(bookShop);
                    }
                    // 向主线程返回结果
                    Message msg = new Message();
                    msg.what = INIT_FINISH;
                    MyHandler.sendMessage(msg);
                    msg.obj = books;
                    st.close();
                    connection.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onStart() {
        if (bookShops.isEmpty()) {
            initData();
        }
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}

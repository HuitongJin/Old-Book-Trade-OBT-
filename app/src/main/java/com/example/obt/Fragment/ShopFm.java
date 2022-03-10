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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.obt.Activity.AddNew;
import com.example.obt.Adapter.BookShopAdapter;
import com.example.obt.Class.BookShop;
import com.example.obt.Mysql.DBConnectHelper;
import com.example.obt.R;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class ShopFm extends Fragment {

    private View mview;
    private QMUITopBarLayout topbar;
    private ImageView addNew;
    private ImageView tradeRecord;
    private ImageView manage;
    private RecyclerView recyclerView;
    private BookShopAdapter bookShopAdapter;
    private List<BookShop> bookShops = new ArrayList<>();
    private String phone;
    final private int INIT_FINISH = -200;
    final private int INIT_FAIL = -300;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case INIT_FINISH:
                    bookShops = (List) msg.obj;
                    bookShopAdapter.notifyDataSetChanged();
                    break;
                case INIT_FAIL:
                    Toast.makeText(getContext(), "加载数据失败", Toast.LENGTH_SHORT).show();
                    break;
                    default:
                        break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mview = inflater.inflate(R.layout.fm_shop, container, false);
        /***
         * 设置沉浸式标题栏
         */
        QMUIStatusBarHelper.translucent(getActivity());
        topbar = (QMUITopBarLayout)mview.findViewById(R.id.topbar);
        topbar.setTitle("我的商店").setTextColor(ContextCompat.getColor(getActivity(), R.color.qmui_config_color_white));
        topbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.qmui_btn_blue_bg));

        Intent intent = this.getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        phone = bundle.getString("Phone");

        /***
         * 初始化控件
         */
        addNew = mview.findViewById(R.id.putnew);
        tradeRecord = mview.findViewById(R.id.trade);
        manage = mview.findViewById(R.id.manage);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView = mview.findViewById(R.id.book_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        bookShopAdapter = new BookShopAdapter(bookShops, phone);
        recyclerView.setAdapter(bookShopAdapter);
        /***
         * 获取数据源
         */
        initData();

        /***
         * 设置控件监听器
         */
        // 上新
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent parent_intent = getActivity().getIntent();
                Bundle bundle = parent_intent.getExtras();
                Intent intent = new Intent(getContext(), AddNew.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        // 查看交易记录
        tradeRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // 管理图书
        manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return mview;
    }
    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection = DBConnectHelper.getConn();
                String sql = "SELECT BID, IMAGE, BNAME, PRICE, AUTHOR, PRESS FROM BOOK WHERE OWNER='"+phone+"' AND SOLD=0";
                Statement st;
                List<BookShop> books = new ArrayList<>();
                try{
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
                    handler.sendMessage(msg);
                    msg.obj = books;
                    st.close();
                    connection.close();

                }catch (SQLException e) {
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
}


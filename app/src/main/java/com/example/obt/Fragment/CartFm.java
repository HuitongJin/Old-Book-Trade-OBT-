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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.obt.Adapter.BookCartAdapter;
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

public class CartFm extends Fragment {
    private View mview;
    private QMUITopBarLayout topbar;
    private RecyclerView recyclerView;
    private List<BookShop> books = new ArrayList<>();
    private BookCartAdapter bookCartAdapter;
    private String phone;
    final private int INIT_FINISH = -200;
    final private int INIT_FAIL = -300;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case INIT_FINISH:
                    books = (List)msg.obj;
                    bookCartAdapter.notifyDataSetChanged();
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
        mview = inflater.inflate(R.layout.fm_cart, container, false);
        /***
         * 设置沉浸式标题栏
         */
        QMUIStatusBarHelper.translucent(getActivity());
        topbar = (QMUITopBarLayout)mview.findViewById(R.id.topbar);
        topbar.setTitle("我的购物车").setTextColor(ContextCompat.getColor(getActivity(), R.color.qmui_config_color_white));
        topbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.qmui_btn_blue_bg));

        Intent intent = this.getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        phone = bundle.getString("Phone");

        /***
         * 初始化控件
         */
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView = mview.findViewById(R.id.cart_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        bookCartAdapter = new BookCartAdapter(books, phone);
        recyclerView.setAdapter(bookCartAdapter);

        /***
         * 初始化数据源
         */
        initData();
        return mview;
    }

    private void initData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection = DBConnectHelper.getConn();
                String sql = "SELECT shop.BID, shop.IMAGE, shop.BNAME, shop.PRICE, shop.AUTHOR, shop.PRESS FROM BOOK as shop, CART as cart WHERE cart.PHONE='"+phone+"' AND shop.BID= cart.BID";
                Statement st;
                List<BookShop> bookShops = new ArrayList<>();
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

                        BookShop book = new BookShop(BID, book_name, book_image, book_author, book_press, book_price);
                        bookShops.add(book);
                    }
                    // 向主线程返回结果
                    Message msg = new Message();
                    msg.what = INIT_FINISH;
                    msg.obj = bookShops;
                    handler.sendMessage(msg);
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
        if (books.isEmpty()) {
            initData();
        }
        super.onStart();
    }
}

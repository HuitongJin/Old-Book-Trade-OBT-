package com.example.obt.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.obt.Class.BookShop;
import com.example.obt.Mysql.DBConnectHelper;
import com.example.obt.R;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class BookShopAdapter extends RecyclerView.Adapter<BookShopAdapter.ViewHolder> {

    private List<BookShop> bookShops;
    private String phone;
    private static int DELETE_FINISH = -400;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == DELETE_FINISH) {
                int position = (int)msg.obj;
                removeItem(position);
            }
        }
    };

    // 这里我们定义一个内部类，其作用是在RecyclerView滚动时设置值
    static class ViewHolder extends  RecyclerView.ViewHolder {
        ImageView bookImage;
        TextView bookName;
        TextView price;
        TextView bookAuthor;
        TextView punishName;
        ImageView trash;
        ImageView edit;
        View bookView;

        public ViewHolder(View view) {
            super(view);
            bookView = view;
            bookImage = view.findViewById(R.id.book_image);
            bookName = view.findViewById(R.id.book_name);
            bookAuthor = view.findViewById(R.id.author_name);
            punishName = view.findViewById(R.id.punish_name);
            trash = view.findViewById(R.id.trash);
            edit = view.findViewById(R.id.edit);
            price = view.findViewById(R.id.price);
        }
    }

    // 构造函数，用于传入数据源
    public BookShopAdapter(List<BookShop> bookShops, String phone) {
        this.bookShops = bookShops;
        this.phone = phone;
    }

    // 以下三个函数一般都需要重写
    // 其中onCreateViewHolder方法用于创建ViewHolder实例， 在这个方法当中
    // 我们需要把RecyclerView的子项布局加载进来，并返回ViewHolder的实例

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.shop_book_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        // 添加点击事件
        holder.bookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                BookShop bookShop = bookShops.get(position);
                Context context = view.getContext();
                Toast.makeText(context, "功能尚未开放，敬请期待！", Toast.LENGTH_SHORT).show();
                // 跳转
            }
        });

        holder.trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                BookShop bookShop = bookShops.get(position);
                Context context = view.getContext();
                // 弹窗下发
                alertWarning(bookShop.getBID(), context, position);
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                BookShop bookShop = bookShops.get(position);
                Context context = view.getContext();
                Toast.makeText(context, "进入修改界面", Toast.LENGTH_SHORT).show();
                // 跳转
            }
        });
        return holder;
    }

    // onBindViewHolder方法用于对RecyclerView子项的数据进行赋值， 会在每个子项
    // 被滚动到屏幕内的时候执行，我们通过position参数获得当前的BookShop实例
    // 然后将数据设置到ViewHolder的bookImage和bookName中即可

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookShop bookShop = bookShops.get(position);
        // 这里将图片的byte数组转化成ImageView可调用的bitmap对象
       // byte[] book_image = bookShop.getBookImage();
       // Bitmap bitmap = BitmapFactory.decodeByteArray(book_image, 0, book_image.length);
        holder.bookImage.setImageBitmap(bookShop.getBookImage());

        holder.bookName.setText(bookShop.getBookName());
        String price_text = "￥" + bookShop.getPrice();
        holder.price.setText(price_text);
        holder.punishName.setText(bookShop.getPunishName());
        holder.bookAuthor.setText(bookShop.getBookAuthor());
    }

    //  getItemCount方法用于告诉RecyclerView一共有几个子项，这里我们直接返回数据源的长度
    @Override
    public int getItemCount() {
        return bookShops.size();
    }

    private void removeItem(int position) {
        bookShops.remove(position);
        // 删除动画
        notifyItemChanged(position);
        // 刷新
        notifyDataSetChanged();
    }

    private void alertWarning(final int BID, final Context context, final int position) {
        //构造器
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("确定要从我的商店移除吗？");
        builder.setMessage("确定后，我们将会从商店中移除这个商品，移除后，这个商品将不再出售");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();  // 关闭dialog
                // 从数据库删除图书
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Connection connection = DBConnectHelper.getConn();
                        // mysql语句
                        String sql = "DELETE FROM BOOK WHERE OWNER='"+phone+"' and BID='"+BID+"'";
                        Statement statement;
                        try {
                            statement = connection.createStatement();
                            statement.executeUpdate(sql);
                            Message msg = new Message();
                            msg.what = DELETE_FINISH;
                            msg.obj = position;
                            handler.sendMessage(msg);
                            statement.close();
                        }catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                Toast.makeText(context, "已删除", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(context, "已取消", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();
    }
}

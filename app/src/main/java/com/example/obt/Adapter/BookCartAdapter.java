package com.example.obt.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.obt.Class.BookShop;
import com.example.obt.Fragment.HomeFm;
import com.example.obt.R;

import java.util.List;

public class BookCartAdapter extends RecyclerView.Adapter<BookCartAdapter.ViewHolder> {

    private List<BookShop> books;
    private String phone;

    // 这里我们定义一个内部类，其作用是在RecyclerView滚动时设置值
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        TextView bookName;
        TextView price;
        TextView bookAuthor;
        TextView punishName;
        ImageView trash;
        ImageView buyNow;
        View bookView;

        public ViewHolder(View view) {
            super(view);
            bookView = view;
            bookImage = view.findViewById(R.id.book_image);
            bookName = view.findViewById(R.id.book_name);
            bookAuthor = view.findViewById(R.id.author_name);
            punishName = view.findViewById(R.id.punish_name);
            trash = view.findViewById(R.id.trash);
            buyNow = view.findViewById(R.id.buy_now);
            price = view.findViewById(R.id.price);
        }
    }
    // 构造函数，用于传入数据源
    public BookCartAdapter(List<BookShop> books, String phone) {
        this.phone = phone;
        this.books = books;
    }

    // 以下三个函数一般都需要重写
    // 其中onCreateViewHolder方法用于创建ViewHolder实例， 在这个方法当中
    // 我们需要把RecyclerView的子项布局加载进来，并返回ViewHolder的实例


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_book_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        // 添加点击事件
        holder.bookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.bookName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return holder;
    }

    // onBindViewHolder方法用于对RecyclerView子项的数据进行赋值， 会在每个子项
    // 被滚动到屏幕内的时候执行，我们通过position参数获得当前的BookShop实例
    // 然后将数据设置到ViewHolder的bookImage和bookName中即可

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookShop book = books.get(position);
        holder.bookImage.setImageBitmap(book.getBookImage());
        holder.bookName.setText(book.getBookName());
        String price_text = "￥" + book.getPrice();
        holder.price.setText(price_text);
        holder.punishName.setText(book.getPunishName());
        holder.bookAuthor.setText(book.getBookAuthor());
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}

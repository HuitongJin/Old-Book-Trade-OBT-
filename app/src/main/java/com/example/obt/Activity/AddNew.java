package com.example.obt.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.obt.Mysql.DBConnectHelper;
import com.example.obt.R;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

public class AddNew extends AppCompatActivity implements View.OnClickListener{

    /**
     * 定义内部变量
     */
    private QMUITopBarLayout topbar;
    private ImageView bookImage;    // 图书照片
    private EditText bookClass; // 图书分类
    private EditText bookName;  // 图书名称
    private EditText bookAuthor;    // 图书作者
    private EditText bookPunish;    // 图书出版社
    private EditText bookPrice; // 图书售价
    private EditText bookIntro; // 图书简介
    private Button bookSubmit;  // 提交按钮
    private String imageString = null;
    private int INSERT_FINISH = -100;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == INSERT_FINISH) {
                    // 跳转回MainActivity
                    AddNew.this.finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        /***
         * 设置沉浸式标题栏
         */
        topbar = (QMUITopBarLayout)findViewById(R.id.topbar);
        topbar.setTitle("添加新书").setTextColor(ContextCompat.getColor(this, R.color.qmui_config_color_white));
        topbar.setBackgroundColor(ContextCompat.getColor(this, R.color.qmui_btn_blue_bg));

        /***
         * 初始化控件
         */
        bookImage = findViewById(R.id.add_photo);
        bookClass = findViewById(R.id.book_class);
        bookName = findViewById(R.id.book_name);
        bookAuthor = findViewById(R.id.book_author);
        bookPunish = findViewById(R.id.book_punish);
        bookIntro = findViewById(R.id.book_intro);
        bookSubmit = findViewById(R.id.book_submit);
        bookPrice = findViewById(R.id.book_price);

        /***
         * 设置监听事件
         */
        bookImage.setOnClickListener(this);
        bookSubmit.setOnClickListener(this);
    }

    /***
     * 添加监听触发事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_photo:
                // 从本地图库中选取图书照片
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
                galleryIntent.setType("image/*"); // 图片
                startActivityForResult(galleryIntent, 0);
                break;
            case R.id.book_submit:
                // 获取Editext输入的内容
                String book_name = bookName.getText().toString();
                String book_author = bookAuthor.getText().toString();
                String book_class = bookClass.getText().toString();
                String book_punish = bookPunish.getText().toString();
                String book_intro = bookIntro.getText().toString();
                Double book_price = Double.parseDouble(bookPrice.getText().toString());

                // 先检查信息是否填写完成
               if (imageString.equals(null) || book_name.equals(null) || book_author.equals(null) || book_class.equals(null)
               || book_punish.equals(null) || book_intro.equals(null) || TextUtils.isEmpty(bookPrice.getText())) {
                   Toast.makeText(this, "请将信息填写完整", Toast.LENGTH_SHORT).show();
               } else {
                   // 弹出确认对话框
                   alertWarning(book_name);
               }
                break;
        }
    }

    /***
     * 重写onActivityResult，处理读取图片的返回结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == -1) {
            Uri uri = data.getData();
            bookImage.setImageURI(uri);

            // 将图片转换成Base64编码
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 弹窗确认下发
     */
    private void alertWarning(final String book_name) {
        //构造器
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认上传图书吗");
        builder.setMessage("确认后，我们将会上架图书"+book_name+"到您的个人商城");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();  // 关闭dialog
                // 上传图书到数据库
                Intent intent = AddNew.this.getIntent();
                Bundle bundle = intent.getExtras();
                final String book_author = bookAuthor.getText().toString();
                final String book_class = bookClass.getText().toString();
                final String book_punish = bookPunish.getText().toString();
                final String book_intro = bookIntro.getText().toString();
                final Double book_price = Double.parseDouble(bookPrice.getText().toString());
                final String phone = bundle.getString("Phone"); // 获取用户手机号
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date(System.currentTimeMillis());
                final String now = simpleDateFormat.format(date); // 获取当前日期
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Connection connection = DBConnectHelper.getConn();
                        String sql = "INSERT INTO BOOK(BNAME, OWNER, PRICE, AUTHOR, PRESS, IMAGE, LAUCHDATE, SOLD, CLASS, INTRO)"
                                + "VALUES('"+book_name+"', '"+phone+"', '"+book_price+"', '"+book_author+"', '"+book_punish+"', " +
                                "'"+imageString+"', '"+now+"', 0, '"+book_class+"', '"+book_intro+"')";
                        Statement statement;
                        try {
                            statement = connection.createStatement();
                            statement.executeUpdate(sql);
                            Message msg = new Message();
                            msg.what = INSERT_FINISH;
                            handler.sendMessage(msg);
                            statement.close();
                        }catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                Toast.makeText(AddNew.this, "已发送", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(AddNew.this, "已取消", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();
    }
}

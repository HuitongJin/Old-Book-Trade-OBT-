package com.example.obt.LoginAndRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.obt.Activity.MainActivity;
import com.example.obt.DataBase.MyDatabaseHelper;
import com.example.obt.Mysql.DBConnectHelper;
import com.example.obt.Mysql.DBHelper;
import com.example.obt.R;
import com.example.obt.Tools.IdentifyCode;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cn.smssdk.ui.companent.CircleImageView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int LOGIN_JUDGE_CODE = 101;  // 登录判断账号密码是否正确
    private DBHelper dbHelper;
    private MyDatabaseHelper SQhelper;
    private CircleImageView account_icon;
    private ImageView iv_shoeCode; // 验证码图片
    private EditText et_phoneCode; // 用户填写的验证码
    private String realCode; // 生成的验证码
    private Button login; // 登录按钮
    private Button register; // 注册按钮
    private TextView forgetPassword; // 忘记密码text
    private EditText telPhone; // 手机号码输入框
    private EditText passWord; // 账号密码输入框
    private CheckBox remember_pass; // 是否记住密码
    private String name;    // 用户名称
    private String image;   // 用户头像

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case LOGIN_JUDGE_CODE:
                    String input_password = passWord.getText().toString();
                    String passkey = (String)msg.obj;
                    if (passkey == null) {
                        Toast.makeText(LoginActivity.this, "账号不存在", Toast.LENGTH_LONG).show();
                        iv_shoeCode.setImageBitmap(IdentifyCode.getInstance().createBitmap());
                        realCode = IdentifyCode.getInstance().getCode().toLowerCase();
                    }else if (! input_password.equals(passkey)) {
                        Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_LONG).show();
                        iv_shoeCode.setImageBitmap(IdentifyCode.getInstance().createBitmap());
                        realCode = IdentifyCode.getInstance().getCode().toLowerCase();
                    }else{
                        SQLiteDatabase db = SQhelper.getWritableDatabase();
                        if (remember_pass.isChecked()) {
                            ContentValues values = new ContentValues();
                            values.put("account", telPhone.getText().toString());
                            values.put("password", passWord.getText().toString());
                            db.insert("rememberAccount", null, values);
                        }
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("Phone", telPhone.getText().toString());
                        bundle.putString("Name", name);
                        bundle.putString("Image", image);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 隐藏系统自带的标题栏
        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // 初始化mysql数据库
        dbHelper = new DBHelper("user");
        SQhelper = new MyDatabaseHelper(this, "rememberAccount", null, 1);
        // 获取图片验证码
        iv_shoeCode = findViewById(R.id.iv_showCode);
        iv_shoeCode.setImageBitmap(IdentifyCode.getInstance().createBitmap());
        realCode = IdentifyCode.getInstance().getCode().toLowerCase();
        // 初始化控件
        account_icon = findViewById(R.id.account_icon);
        et_phoneCode = findViewById(R.id.identify_code);
        login = findViewById(R.id.login_button);
        forgetPassword = findViewById(R.id.forget_pass);
        register = findViewById(R.id.register_button);
        telPhone = findViewById(R.id.phone);
        passWord = findViewById(R.id.password);
        remember_pass = findViewById(R.id.remember_pass);

        SQLiteDatabase db = SQhelper.getWritableDatabase();
        Cursor cursor = db.query("rememberAccount", null, null, null,
                null, null, null, null);
        if (cursor.moveToFirst()){
            telPhone.setText(cursor.getString(cursor.getColumnIndex("account")));
            passWord.setText(cursor.getString(cursor.getColumnIndex("password")));
            remember_pass.setChecked(true);
            String account = cursor.getString(cursor.getColumnIndex("account"));
            String password = cursor.getString(cursor.getColumnIndex("password"));
            db.delete("rememberAccount", "account=?", new String[]{account});
            db.delete("rememberAccount", "password=?", new String[] {password});
        }

        iv_shoeCode.setOnClickListener(this);
        login.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);
        register.setOnClickListener(this);

        telPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final String input_phone = telPhone.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 检测账号，是否存在
                        Connection connection = DBConnectHelper.getConn();
                        String sql = "SELECT PASSKEY FROM USER WHERE PHONE='" + input_phone + "' limit 1";
                        String sql_2 = "SELECT IMAGE FROM USERINFO WHERE PHONE='"+input_phone+"' limit 1";
                        Statement st;
                        // 如果存在获取头像
                        try {
                            st = connection.createStatement();
                            ResultSet rs = st.executeQuery(sql);
                            if (rs.first()) {
                                ResultSet imgRs = st.executeQuery(sql_2);
                                while (imgRs.next()) {
                                    byte[] imageBytes = Base64.decode(imgRs.getString("IMAGE"), Base64.DEFAULT);
                                    final Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                    // 在主线程修改头像
                                    LoginActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            account_icon.setImageBitmap(decodedImage);
                                        }
                                    });
                                }
                            } else {
                               // Toast.makeText(LoginActivity.this, "TRUE", Toast.LENGTH_SHORT).show();
                                // 在主线程修改头像
                                LoginActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        account_icon.setImageResource(R.drawable.takephoto);
                                    }
                                });
                            }
                            st.close();
                            connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_showCode:
                iv_shoeCode.setImageBitmap(IdentifyCode.getInstance().createBitmap());
                realCode = IdentifyCode.getInstance().getCode().toLowerCase();
                break;

            case R.id.login_button:
                if (judgeIdentifyCode()) {
                    judgeAccount();
                }
                break;

            case R.id.forget_pass:
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
                break;

            case R.id.register_button:
                Intent intent1 = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent1);
                LoginActivity.this.finish();
                break;
        }
    }

    // 判断账号密码是否正确
    private void   judgeAccount() {
        final String input_phone = telPhone.getText().toString();
        String input_password = passWord.getText().toString();
        if (TextUtils.isEmpty(input_phone)) {
            Toast.makeText(LoginActivity.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(input_password)) {
            Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Connection connection = DBConnectHelper.getConn();
                    String sql = "SELECT USER.PASSKEY AS PASSKEY, Info.SNAME AS SNAME, Info.IMAGE AS IMAGE FROM USER AS USER, USERInfo as Info WHERE USER.PHONE='"+input_phone+"' AND Info.PHONE='"+input_phone+"'limit 1";
                    Statement st;
                    try {
                        st = connection.createStatement();
                        ResultSet rs = st.executeQuery(sql);
                        if (rs.first()) {
                            String passkey = rs.getString("PASSKEY");
                            Message msg = new Message();
                            msg.what = LOGIN_JUDGE_CODE;
                            msg.obj = passkey;
                            name = rs.getString("SNAME");
                            image = rs.getString("IMAGE");
                            handler.sendMessage(msg);
                        }else {
                            Message msg = new Message();
                            msg.what = LOGIN_JUDGE_CODE;
                            msg.obj = null;
                            handler.sendMessage(msg);
                        }
                        st.close();
                        connection.close();
                    }catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    // 判断验证码是否正确
    private boolean judgeIdentifyCode() {
        String phoneCode = et_phoneCode.getText().toString().toLowerCase();
        if ("".equals(phoneCode)){
            Toast.makeText(LoginActivity.this, "请输入验证码",
                    Toast.LENGTH_SHORT).show();
            return false;
        }else if(phoneCode.equals(realCode)) {
            return true;
        }else{
            Toast.makeText(LoginActivity.this, "验证码错误",
                    Toast.LENGTH_SHORT).show();
            iv_shoeCode.setImageBitmap(IdentifyCode.getInstance().createBitmap());
            realCode = IdentifyCode.getInstance().getCode().toLowerCase();
            return false;
        }
    }
}

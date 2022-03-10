package com.example.obt.LoginAndRegister;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

import com.example.obt.Activity.MainActivity;
import com.example.obt.Mysql.DBConnectHelper;
import com.example.obt.R;
import com.mob.MobSDK;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.ui.companent.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    public static final int PHONE_EXIT_CODE = 102;
    public static final int PHONE_NOT_EXIT_CODE = 103;
    public static final int INSERT_FINISH = 104;
    private CircleImageView account_icon;
    private EditText userPhone;
    private EditText userName;
    private EditText userPassword1;
    private EditText usersPassword2;
    private Button sendMsg;
    private Button register;
    private EditText identifyCode;
    private EditText studentId;
    private EditText user_grade;
    private EditText user_major;

    private int TIME = 60;
    public String country="86"; // 中国区号
    private String phone;
    private String password_1;
    private String password_2;
    private String name;
    private String id;
    private int grade;
    private String major;
    private String imageString = null;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case PHONE_EXIT_CODE:
                    Toast.makeText(RegisterActivity.this, "账号已存在！", Toast.LENGTH_LONG).show();
                    break;
                case PHONE_NOT_EXIT_CODE:
                    alertWarning();
                    break;
                case INSERT_FINISH:
                    // 使用bundle和intent进行活动之间的通信
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    RegisterActivity.this.finish();
                    break;
                case -9:
                    sendMsg.setText("重新发送("+TIME+")");
                    break;
                case -8:
                    sendMsg.setText("重新发送");
                    sendMsg.setClickable(true);
                    TIME = 60;
                    break;
                case -7:
                    int i = msg.arg1;
                    int i1 = msg.arg2;
                    Object o = msg.obj;
                    if (i1 == SMSSDK.RESULT_COMPLETE) {
                        // 短信注册成功后，进入MainActivity
                        if (i == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            insertUser();
                        } else if (i == SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE) {
                            Toast.makeText(RegisterActivity.this, "正在获取验证码", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        ((Throwable)o).printStackTrace();
                        String str = o.toString();
                        Toast.makeText(RegisterActivity.this, str, Toast.LENGTH_LONG).show();
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
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 注册短信验证
        MobSDK.submitPolicyGrantResult(true, null);
        MobSDK.init(this, "318f381b3c423", "a0274ec6db3ab5d71e8df7ba3a2ce567");
        SMSSDK.registerEventHandler(eh); // 注册短信回调，注意销毁，防止内存泄露
        // 初始化控件
        account_icon = findViewById(R.id.account_icon);
        sendMsg = findViewById(R.id.send_identifyCode);
        register = findViewById(R.id.register_button);
        userPhone = findViewById(R.id.user_phone);
        identifyCode = findViewById(R.id.user_identifyNum);
        userName = findViewById(R.id.user_name);
        userPassword1 = findViewById(R.id.user_password_1);
        usersPassword2 = findViewById(R.id.user_password_2);
        studentId = findViewById(R.id.user_studentId);
        user_grade = findViewById(R.id.user_grade);
        user_major = findViewById(R.id.user_major);

        sendMsg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                phone = userPhone.getText().toString().trim().replaceAll("/s","");
                if (!TextUtils.isEmpty(phone)) {
                    // 定义需要匹配的正则表达式的规则
                    String REGEX_MOBILE_SIMPLE = "[1][358]\\d{9}";
                    // 把正则表达式的规则编译成模板
                    Pattern pattern = Pattern.compile(REGEX_MOBILE_SIMPLE);
                    // 把需要匹配的字符给模板匹配，获得匹配器
                    Matcher matcher = pattern.matcher(phone);
                    // 通过匹配器查找是否有该字符，不可重复调用matcher.find()
                    if (! matcher.find()) {
                        Toast.makeText(RegisterActivity.this, "手机号码格式错误",
                                Toast.LENGTH_SHORT).show();
                    }else {
                        // 判断账号是否已经注册
                        judgeExit(phone);
                    }
                }else {
                    Toast.makeText(RegisterActivity.this,"手机号码不能为空！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                password_1 = userPassword1.getText().toString();
                password_2 = usersPassword2.getText().toString();
                if (TextUtils.isEmpty(password_1) || TextUtils.isEmpty(password_2)){
                    Toast.makeText(RegisterActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }else if (!password_1.equals(password_2)) {
                    Toast.makeText(RegisterActivity.this, "前后密码不一致，请重新检查", Toast.LENGTH_SHORT).show();
                }else {
                    // 密码长度要求8到16位，可以包含数字、、字母和字符，不能包含中文
                    String Regex_password = "^(?![^a-zA-Z]+$)(?!\\D+$).{8,}$";
                    Pattern pattern = Pattern.compile(Regex_password);  // 将正则表达式编译成模板
                    // 进行匹配
                    Matcher matcher = pattern.matcher(password_1);
                    if (!matcher.find()){
                        Toast.makeText(RegisterActivity.this, "密码格式错误", Toast.LENGTH_SHORT).show();
                    }else {
                        id = studentId.getText().toString();
                        // 学号以201开头的10位数字
                        String Regex_id = "[201]\\d{7}";
                        Pattern pattern1 = Pattern.compile(Regex_id);
                        Matcher matcher1 = pattern1.matcher(id);
                        if (!matcher1.find()) {
                            Toast.makeText(RegisterActivity.this, "请输入正确的学号", Toast.LENGTH_SHORT).show();
                        }else {
                            // 获取用户输入的验证码
                            String code = identifyCode.getText().toString().replaceAll("/s","");
                            if (!TextUtils.isEmpty(code)) {
                                SMSSDK.submitVerificationCode(country, phone, code);
                            }else {
                                Toast.makeText(RegisterActivity.this, "请输入验证码", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                }
            }
        });

        account_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 从本地图库中选取头像
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
                galleryIntent.setType("image/*"); // 图片
                startActivityForResult(galleryIntent, 0);
            }
        });
    }

    private void judgeExit(final String phone) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection = DBConnectHelper.getConn();
                String sql = "SELECT PHONE FROM USER WHERE PHONE='"+phone+"' limit 1";
                Statement st;
                try {
                    st = connection.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    if (rs.first()) {
                        Message msg = new Message();
                        msg.what = PHONE_EXIT_CODE;
                        handler.sendMessage(msg);
                    }else {
                        Message msg = new Message();
                        msg.what = PHONE_NOT_EXIT_CODE;
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

    private void insertUser() {
        phone = userPhone.getText().toString();
        password_1 = userPassword1.getText().toString();
        name = userName.getText().toString();
        id = studentId.getText().toString();
        grade = Integer.parseInt(user_grade.getText().toString());
        major = user_major.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection = DBConnectHelper.getConn();
                String sql_1 = "INSERT INTO USERINFO(PHONE, SID, SNAME, GRADE, MAJOR, QPOINT, IMAGE) " +
                        "VALUES('"+phone+"','"+id+"','"+name+"','"+grade+"','"+major+"', 0, '"+imageString+"')";
                String sql_2 =  "INSERT INTO USER(PHONE, PASSKEY) " +
                        "VALUES('"+phone+"','"+password_1+"')";
                Statement statement;
                try {
                    statement = connection.createStatement();
                    statement.executeUpdate(sql_1);
                    statement.executeUpdate(sql_2);
                    Message msg = new Message();
                    msg.what = INSERT_FINISH;
                    handler.sendMessage(msg);
                    statement.close();
                }catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 信息回调
    EventHandler eh = new EventHandler(){
        @Override
        public void afterEvent(int event, int result, Object data) {
            Message msg = new Message();
            msg.arg1 = event;
            msg.arg2 = result;
            msg.obj = data;
            msg.what = -7;
            handler.sendMessage(msg);
        }
    };

    // 重写返回键的逻辑，使得用户返回后重新回到登陆界面，而不是退出程序
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        if (!RegisterActivity.this.isFinishing()) {
            RegisterActivity.this.finish();
        }
    }

    // 弹窗确认下发
    private void alertWarning() {
        // 构造器
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("温馨提示");
        builder.setMessage("我们将要发送到"+phone+"验证");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // 关闭dialog
                // 通过sdk发送短信验证码(请求获取短信验证码，在监听（eh）中返回）
                SMSSDK.getVerificationCode(country,phone);
                // 做倒计时操作
                Toast.makeText(RegisterActivity.this, "已发送", Toast.LENGTH_SHORT).show();
                sendMsg.setClickable(false);
                // register.setEnabled(false);
                sendMsg.setText("重新发送("+TIME+")");
                // 开启一个子线程进行一分钟倒计时
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(; TIME>0; TIME--) {
                            handler.sendEmptyMessage(-9);  // 这个线程如果没有结束，则返回-9
                            if (TIME <= 0)
                                break;
                            try {
                                Thread.sleep(1000);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                        handler.sendEmptyMessage(-8);  // 这个线程如果结束，则返回-8
                    }
                }).start();

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(RegisterActivity.this, "已取消", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();
    }

    // 销毁短信注册
    @Override
    protected void onDestroy() {
        SMSSDK.unregisterAllEventHandler();  // 这里注意取消注册，否则会造成内存泄露
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == -1) {
            Uri uri = data.getData();
            account_icon.setImageURI(uri);

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
}

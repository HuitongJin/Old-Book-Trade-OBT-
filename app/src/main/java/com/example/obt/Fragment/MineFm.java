package com.example.obt.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.obt.LoginAndRegister.LoginActivity;
import com.example.obt.Mysql.DBConnectHelper;
import com.example.obt.R;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.hdodenhof.circleimageview.CircleImageView;

public class MineFm extends Fragment {
    private View mview;
    private QMUITopBarLayout topbar;
    private Bundle father_bundle;
    private final int QQUERY_FINISH = -12;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case QQUERY_FINISH:
                    double q = (double)msg.obj;
                    System.out.println("Q点"+q);
                    QAlert(q);
                    break;
                    default:
                        break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mview = inflater.inflate(R.layout.fm_mine, container, false);
        QMUIStatusBarHelper.translucent(getActivity());
        topbar = (QMUITopBarLayout)mview.findViewById(R.id.topbar);
        topbar.setTitle("个人中心").setTextColor(ContextCompat.getColor(getActivity(), R.color.qmui_config_color_white));
        topbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.qmui_btn_blue_bg));

        Intent father_intent = getActivity().getIntent();
        father_bundle = father_intent.getExtras();

        /*设置用户头像*/
        CircleImageView userImage = mview.findViewById(R.id.user_image);
        byte[] imageBytes = Base64.decode(father_bundle.getString("Image"), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        userImage.setImageBitmap(decodedImage);

        /*设置用户账号和手机号*/
        TextView userName = mview.findViewById(R.id.user_name);
        userName.setText(father_bundle.getString("Name"));
        TextView userPhone = mview.findViewById(R.id.user_phone);
        userPhone.setText(father_bundle.getString("Phone"));

        LinearLayout changeCount = (LinearLayout)mview.findViewById(R.id.change_count);
        LinearLayout Qpoint = (LinearLayout)mview.findViewById(R.id.q_point);
        LinearLayout Xinyong = (LinearLayout)mview.findViewById(R.id.xinyong);

        changeCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCountAlert();
            }
        });

        Qpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               QpointQuery();
            }
        });

        Xinyong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return mview;
    }

    /*切换账号对话框*/
    private void changeCountAlert(){
        // 构造器
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("温馨提示：");
        builder.setMessage("确定要退出当前账号吗？");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();   // 关闭dialog
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(getContext(), "已取消", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();
    }

    /*Q点查询*/
    private void QpointQuery() {
        final String phone = father_bundle.getString("Phone");
        System.out.println("手机号码：" + phone);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection = DBConnectHelper.getConn();
                // mysql语句
                String sql = "SELECT QPOINT FROM USERINFO WHERE PHONE='"+phone+"'";
                Statement statement;
                try {
                    statement = connection.createStatement();
                    ResultSet rs = statement.executeQuery(sql);
                    if (rs.first()) {
                        double q = rs.getDouble("QPOINT");
                        Message message = new Message();
                        message.what = QQUERY_FINISH;
                        message.obj =  q;
                        handler.sendMessage(message);
                    }
                    statement.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /*Q点查询对话框*/
    private void QAlert(double q) {
        // 构造器
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("您当前Q点余额为：");
        builder.setMessage("                         "+q);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();   // 关闭dialog
            }
        });
        builder.create().show();
    }
}

package com.example.obt.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.obt.Adapter.MsgAdapter;
import com.example.obt.Class.MsgClass;
import com.example.obt.R;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.util.ArrayList;
import java.util.List;

public class MsgFm extends Fragment {
    private View mview;
    private QMUITopBarLayout topbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mview = inflater.inflate(R.layout.fm_msg, container, false);
        QMUIStatusBarHelper.translucent(getActivity());
        topbar = (QMUITopBarLayout)mview.findViewById(R.id.topbar);
        topbar.setTitle("消息中心").setTextColor(ContextCompat.getColor(getActivity(), R.color.qmui_config_color_white));
        topbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.qmui_btn_blue_bg));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        RecyclerView recyclerView = mview.findViewById(R.id.msg_recycler_view);
        recyclerView.setLayoutManager(layoutManager);

        List<MsgClass> msgs = new ArrayList<>();
        msgs.clear();

        Bitmap image_bit = BitmapFactory.decodeResource(getResources(), R.drawable.sysmsg);
        String userName = "系统通知";
        String msgInfo = "欢迎来到深圳大学旧书交易平台";
        MsgClass msg = new MsgClass(image_bit, userName, msgInfo);
        msgs.add(msg);

        image_bit = BitmapFactory.decodeResource(getResources(), R.drawable.usermsg1);
        userName = "郑佳";
        msgInfo = "同学您好，最近看到您一款数据库的图书，非常感兴趣，是否有兴趣进一步深入交流";
        msg = new MsgClass(image_bit, userName, msgInfo);
        msgs.add(msg);

        image_bit = BitmapFactory.decodeResource(getResources(), R.drawable.kefumsg);
        userName = "客服";
        msgInfo = "本次客服结束，欢迎您对我们的服务做出评价，回复1-10进行打分，期待为您下次服务";
        msg = new MsgClass(image_bit, userName, msgInfo);
        msgs.add(msg);

        image_bit = BitmapFactory.decodeResource(getResources(), R.drawable.expressmsg);
        userName = "物流通知";
        msgInfo = "您购买的旧书发货啦！！点击查看详情";
        msg = new MsgClass(image_bit, userName, msgInfo);
        msgs.add(msg);

        image_bit = BitmapFactory.decodeResource(getResources(), R.drawable.usermsg2);
        userName = "子谦";
        msgInfo = "我最近上架了一款新书，欢迎购买哦~";
        msg = new MsgClass(image_bit, userName, msgInfo);
        msgs.add(msg);

        image_bit = BitmapFactory.decodeResource(getResources(), R.drawable.usermsg3);
        userName = "茶人";
        msgInfo = "同学你好~ 请问你目前有数据库系统应用二手书吗？";
        msg = new MsgClass(image_bit, userName, msgInfo);
        msgs.add(msg);

        image_bit = BitmapFactory.decodeResource(getResources(), R.drawable.usermsg4);
        userName = "彭小刚";
        msgInfo = "我已在理工楼，您还有多久到？";
        msg = new MsgClass(image_bit, userName, msgInfo);
        msgs.add(msg);

        image_bit = BitmapFactory.decodeResource(getResources(), R.drawable.usermsg5);
        userName = "Hi girl~";
        msgInfo = "喜欢这款宝贝吗，欲购从速哦";
        msg = new MsgClass(image_bit, userName, msgInfo);
        msgs.add(msg);

        MsgAdapter msgAdapter = new MsgAdapter(msgs);
        recyclerView.setAdapter(msgAdapter);
        return mview;
    }
}

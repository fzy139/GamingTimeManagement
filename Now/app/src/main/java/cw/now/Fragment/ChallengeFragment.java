package cw.now.Fragment;

/**
 * Created by Thinkpad on 2018/2/14.
 */

import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cw.now.DdlSetting;
import cw.now.List.ListAdapter;
import cw.now.List.ListItem;
import cw.now.Main;
import cw.now.R;

import static android.content.Context.MODE_PRIVATE;

public class ChallengeFragment extends Fragment {

    public boolean editing=false;
    public int GoalCounts=0;
    public int GoalID=0;
    private List<ListItem>GoalList=new ArrayList<>();
    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
    ListAdapter listAdapter=new ListAdapter(GoalList);
    public String returnData;
    public int returnSpan;
    String spanShow=new String();

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityResult(int code,int result,Intent data){ //接收DdlSetting活动接收到的intent数据包
        if(data!=null){
            returnData=data.getStringExtra("ddl_return");
            returnSpan=data.getIntExtra("span_return",0);
            Log.d("onActivityResult: ",returnData+returnSpan);
            //Log.d("ddl", "find text view "+returnData);
            }

        else Log.e("onActivityResult:" ,"Intent null" );// Attempt to invoke virtual method 'java.lang.String android.
        // content.Intent.getStringExtra(java.lang.String)' on a null object reference可能是未接收到data？
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View viewF=inflater.inflate(R.layout.challenge_fragment, container, false);

        final SharedPreferences.Editor goal_recorder=getActivity().getSharedPreferences("Goals",MODE_PRIVATE).edit();
        final SharedPreferences goals=getActivity().getSharedPreferences("Goals",MODE_PRIVATE);//二者顺序,第一次运行相关，先创建才能获取到
        //final View input_view=getLayoutInflater().inflate(R.layout.input_bar,null);
        final View input_view=LayoutInflater.from(getContext()).inflate(R.layout.input_bar, null, false);

        //在所在fragment中寻找recyclerview,并初始化
        RecyclerView recyclerView=(RecyclerView)viewF.findViewById(R.id.goal_list);
        if(recyclerView!=null){
            Log.d("initItem", "initItem: found list in fragment");
            recyclerView.setAdapter(listAdapter);
            recyclerView.setLayoutManager(linearLayoutManager);}
        else Log.e("initItem", "initItem: not found list in fragment");

        initItem(getActivity());//初始化


        final SwipeRefreshLayout swipeRefreshLayout=(SwipeRefreshLayout)viewF.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               initItem(getActivity());
                handler.sendEmptyMessage(100);
            }
        });

        //获取目标数
        if(goals.contains("Counts"))
        {
            GoalCounts=goals.getInt("Counts",-1);
            Log.d("Goal","Current"+GoalCounts);
        }
        else{
            Log.d("Goal","firstcount");
            goal_recorder.putInt("Counts",0);
            goal_recorder.putInt("GoalID",0);
            goal_recorder.apply();
        }



        final FloatingActionButton add = (FloatingActionButton) viewF.findViewById(R.id.floatingActionButton);
        if (add == null) {//添加事件
            Log.e("floating button", "not found,check layout loaded");
        }
        final PopupWindow input=new PopupWindow(input_view);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BottomNavigationView navigation = (BottomNavigationView) getActivity().findViewById(R.id.navigation);

                    Log.d("edit", "onClick:first ");
                    navigation.setTranslationY(300);//点击添加按钮后，先隐藏
                    editing = true;//正在编辑flag
                    add.setImageResource(R.drawable.up);//修改图标


                    input.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                    input.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    input.setFocusable(true);
                    input.setOutsideTouchable(false);//居然必须设置false才可以点击外部关闭window？？？
                    input.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    input.setElevation(10);//添加高度 可显示阴影
                    input.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//防止软键盘挡住window
                   // input.setTouchable(true);
                    input.showAtLocation(viewF, Gravity.BOTTOM,0,0);//最好按顺序以上

                    input.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            add.setImageResource(R.drawable.add);
                            navigation.setTranslationY(0);
                        }
                    });


                    final EditText name = (EditText) input_view.findViewById(R.id.newname);
                    name.setFocusable(true);
                    name.setFocusableInTouchMode(true);//注意顺序，开启后才可获取
                    name.requestFocus();
                    name.findFocus();
                    //设置焦点可捕捉，可弹出键盘，防止其他页面捕捉，已在属性中false
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(name, InputMethodManager.SHOW_FORCED);
                    } //弹出输入法 https://www.cnblogs.com/niupi/p/6251663.html

                    ImageButton obstacle=(ImageButton)input_view.findViewById(R.id.set_obs);
                    obstacle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final String[]obs_item={"手机干扰","难度太大","动力不足"};
                            final String[]obs_set=new String[10];
                             int num=0;
                            final AlertDialog.Builder obs=new AlertDialog.Builder(getContext())
                                    .setTitle("选择您的阻力源")
                                    .setIcon(R.drawable.obst)
                                    .setMultiChoiceItems(obs_item, new boolean[]{false, false, false}, new DialogInterface.OnMultiChoiceClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                            int num=0;
                                            obs_set[num++]=obs_item[i];
                                            Toast.makeText(getContext(),"阻力"+obs_item[i],Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    })
                                    .setNegativeButton("先不选", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                                    obs.create().show();

                        }
                    });

                    ImageButton deadline = (ImageButton) input_view.findViewById(R.id.set_ddl);//ddl设定界面
                    deadline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
                            }
                            startActivityForResult(new Intent(getContext(), DdlSetting.class), 001);//带数据返回的Intent
                            // TODO:后面的三行不运行了

                            initItem(getActivity());//刷新列表，重新显示该项日期
                            TextView ddl_show=(TextView)input_view.findViewById(R.id.ddl_show);
                            ddl_show.setText(returnData);
                        }
                    });

                    //提交部分
                    //
                    ImageButton ok=(ImageButton)input_view.findViewById(R.id.ok);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            add.setImageResource(R.drawable.add);
                            Log.d("edit", "onClick:sec ");
                            final EditText name = (EditText)input_view.findViewById(R.id.newname);
                            String newName = name.getText().toString();
                            Log.d("Goal", "onClick:" + newName);
                            if (!newName.isEmpty()) {//有输入数据的话保存
                                GoalCounts = goals.getInt("Counts", -1);//获取旧的目标数量
                                GoalID = goals.getInt("GoalID", -1);
                                GoalCounts += 1;
                                GoalID += 1;//当前序号为数量+1
                                Log.d("Goal", "onClick:" + GoalCounts + "now");
                                goal_recorder.putInt("Counts", GoalCounts);//更新目标数量
                                goal_recorder.putInt("GoalID", GoalID);
                                goal_recorder.putString("GID_name" + GoalID, newName);//键值格式 名字+NO
                                goal_recorder.apply();
                               initItem(getActivity());
                                navigation.setTranslationY(0);//恢复导航栏位置
                                editing = false;
                                input.dismiss();
                                //关闭输入法及自动捕捉edittext焦点
                                name.setText("");
                                name.setFocusableInTouchMode(false);
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (imm != null) {
                                    imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
                                }
                            }else {//没有数据保存 直接关闭
                                Log.d( "onClick: ","no data");
                                final FloatingActionButton add = (FloatingActionButton) viewF.findViewById(R.id.floatingActionButton);
                                editing = false;
                                add.setImageResource(R.drawable.add);

                                input.dismiss();
                                name.setText("");
                                name.setFocusableInTouchMode(false);
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (imm != null) {
                                    imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
                                }
                                navigation.setTranslationY(0);//恢复导航栏位置
                            }
                        }
                    });


                }



        });

        return viewF;
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case 100:
                    SwipeRefreshLayout swipeRefreshLayout=getView().findViewById(R.id.refresh);

                    initItem(getActivity());
                    listAdapter.notifyDataSetChanged();

                    if(swipeRefreshLayout.isRefreshing()){
                        swipeRefreshLayout.setRefreshing(false);
                    }
            }
        }



    };


    public void initItem(Activity activity) {
        int Span;

        final SharedPreferences goals =activity.getSharedPreferences("Goals", MODE_PRIVATE);
        int CountsNew = goals.getInt("GoalID", -1);
        if (CountsNew != 0) {//
            GoalList.removeAll(GoalList);
            Log.d("Goal", "Total" + CountsNew);
            for (int i = 1; i <= CountsNew; i++) {
                String GoalName = goals.getString("GID_name" + i, "null");//第i个提取第i个目标，获取名字
                Boolean Finished = goals.getBoolean("GID_finished" + i, false);//TODO:删除的已完成仍显示
                if (!GoalName.equals("null")) {
                    Span = goals.getInt("GID_span" + i, 0);
                    String Ddl = goals.getString("GID_ddl" + i, " ");

                    Log.d("date", "initItem:" + Span + Ddl);
                    int key = i;
                    Log.d("initItem", "initItem:" + key);
                    String Reward = "巧克力";
                    int min = 00;
                    int hour = 10;
                    int day = 15;
                    int month = 4;
                    int cat = 3;
                    if(Span/60!=0 || Span%60!=0){
                        spanShow = "预计"+(Span / 60) + "时" + (Span % 60) + "分";}
                    else if(Ddl.equals(" "))spanShow="快设置一个期限吧";
                    if(!Ddl.equals(" ")){
                        spanShow+=" "+Ddl+"截止";
                    }
                    Log.d("Goal", GoalName + "building");

                    ListItem goal = new ListItem(GoalName, Reward, spanShow, cat, min, hour, day, month, key, Finished);
                    GoalList.add(goal);

                }

            }

        }


    }


}
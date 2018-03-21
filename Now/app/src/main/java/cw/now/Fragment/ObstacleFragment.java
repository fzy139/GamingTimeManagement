package cw.now.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import cw.now.R;
import cw.now.SimpleList.Item;
import cw.now.SimpleList.SimpleAdapter;

/**
 * Created by Thinkpad on 2018/2/26.
 */

public class ObstacleFragment  extends Fragment {
    int OID=0;
    private List<cw.now.SimpleList.Item> obs_list=new ArrayList<>();
    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        final SharedPreferences.Editor editor=getActivity().getSharedPreferences("Obstacle",Context.MODE_PRIVATE).edit();
        final SharedPreferences reader=getActivity().getSharedPreferences("Obstacle",Context.MODE_PRIVATE);//以obstacle为文件名

        //获取阻力数
        if(reader.contains("OID"))
        {
            OID=reader.getInt("OID",-1);
            Log.d("Obstacle","OID"+OID);
        }
        else{
            Log.d("Obstacle","firstcount");
            editor.putInt("Counts",0);
            editor.putInt("OID",0);
            editor.apply();
        }
    }
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.obstacle_fragment,container,false);//加载布局
        final SharedPreferences.Editor editor=getActivity().getSharedPreferences("Obstacle",Context.MODE_PRIVATE).edit();
        final SharedPreferences reader=getActivity().getSharedPreferences("Obstacle",Context.MODE_PRIVATE);//以obstacle为文件名



        RecyclerView obslist=view.findViewById(R.id.obs_list);//布局中寻找list
        if(obslist!=null){
            obslist.setAdapter(new SimpleAdapter(obs_list));
            obslist.setLayoutManager(linearLayoutManager);
            refreshItem();
        }

        final FloatingActionButton add=(FloatingActionButton)view.findViewById(R.id.add_obs);
        if(add!=null){
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final BottomNavigationView navigation = (BottomNavigationView) getActivity().findViewById(R.id.navigation);
                    navigation.setTranslationY(300);//点击添加按钮后，先隐藏

                    View input_view=getLayoutInflater().inflate(R.layout.simple_input,null);
                    final PopupWindow input=new PopupWindow(input_view);
                    input.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                    input.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

                    input.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    input.setOutsideTouchable(false);//设置背景才可以相应外部点击和back
                    input.setFocusable(true);
                    input.showAtLocation(view, Gravity.BOTTOM,0,0);

                    //back返回或外部点击，则恢复导航栏
                    input.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            add.setImageResource(R.drawable.add);
                            navigation.setTranslationY(0);
                        }
                    });

                    //输入框相关
                    final EditText name=(EditText) input_view.findViewById(R.id.obs_name);
                    name.setFocusable(true);
                    name.setFocusableInTouchMode(true);//注意顺序，开启后才可获取
                    name.requestFocus();
                    name.findFocus();
                    //设置焦点可捕捉，可弹出键盘，防止其他页面捕捉，已在属性中false
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(name, InputMethodManager.SHOW_FORCED);
                    } //弹出输入法 https://www.cnblogs.com/niupi/p/6251663.html

                    //提交相关
                    ImageButton commit=(ImageButton)input_view.findViewById(R.id.obs_commit);
                    commit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String obs_name=name.getText().toString();
                            if (!obs_name.isEmpty()){

                            int OID=reader.getInt("OID",0);
                            int Counts=reader.getInt("Counts",0);
                            int OID_new=OID+1;
                            int Counts_new=Counts+1;
                            editor.putString(""+OID,obs_name);//存储格式
                            editor.putInt("OID",OID_new);
                            editor.putInt("Counts",Counts_new);
                            editor.apply();
                            input.dismiss();
                            navigation.setTranslationY(0);

                            Item obsitem=new Item(obs_name,"干扰",OID_new);
                            obs_list.add(obsitem);
                            refreshItem();
                            }
                            else {
                                input.dismiss();
                                navigation.setTranslationY(0);
                            }
                        }
                    });

                }
            });
        }else Log.e("onCreateView: ", "fab not found");


        return view;
    }




    private void refreshItem()
    {
        final SharedPreferences.Editor editor=getActivity().getSharedPreferences("Obstacle",Context.MODE_PRIVATE).edit();
        final SharedPreferences reader=getActivity().getSharedPreferences("Obstacle",Context.MODE_PRIVATE);//以obstacle为文件名

        OID=reader.getInt("OID",0);
        Log.d( "refreshItem: ",OID+"now");

        if(OID!=0) {
            obs_list.removeAll(obs_list);
            for (int i = 0; i < OID; i++) {
                String name = reader.getString("" + i, " ");
                if(name!=" ") {
                    Item obsitem = new Item(name, "专注", i);
                    obs_list.add(obsitem);
                    Log.d("refreshItem: ", name + "building");
                }
            }

        }
    }
}

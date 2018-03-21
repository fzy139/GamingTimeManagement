package cw.now;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class ScrollingActivity extends AppCompatActivity {
   // private List<ListItem> DetailList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);

        Intent receive=getIntent();
        final String getname=receive.getStringExtra("GoalName");
        final int getkey=receive.getIntExtra("Key",0);//从adapater获取点击项目的键值

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton back=(ImageButton)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageButton ddl=(ImageButton)findViewById(R.id.ddl);
        if(ddl!=null){
        ddl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ddl=new Intent(ScrollingActivity.this,DdlSetting.class);
                ddl.putExtra("key",getkey);
                startActivity(ddl);
            }
        });}else Log.e("ddlseting","not found" );

        CollapsingToolbarLayout mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        mCollapsingToolbarLayout.setTitle(getname+"详情");
        mCollapsingToolbarLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        final SharedPreferences save=getSharedPreferences(getname,MODE_PRIVATE);
        final SharedPreferences.Editor editor=getSharedPreferences(getname,MODE_PRIVATE).edit();


        final EditText detail=(EditText)findViewById(R.id.edit_detail);
        final EditText reward=(EditText)findViewById(R.id.edit_reward);
        final EditText obstacle=(EditText)findViewById(R.id.edit_obstacle);
        final EditText strategy=(EditText)findViewById(R.id.edit_strategy);

        if(save.contains("detail")){
            Log.d("edit", "onCreate: saved d");
            detail.setText(save.getString("detail",""));
        }
        if(save.contains("reward")){
            Log.d("edit", "onCreate: saved r");
            reward.setText(save.getString("reward",""));
        }
        if(save.contains("obstacle")){
            Log.d("edit", "onCreate: saved o");
            obstacle.setText(save.getString("obstacle",""));
        }
        if(save.contains("strategy")){
            Log.d("edit", "onCreate: saved s");
            strategy.setText(save.getString("strategy",""));
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("detail",detail.getText().toString());
                editor.putString("reward",reward.getText().toString());
                editor.putString("obstacle",obstacle.getText().toString());
                editor.putString("strategy",strategy.getText().toString());
                editor.apply();

                Snackbar.make(view, "已保存", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent start=new Intent(ScrollingActivity.this,Focus.class);
                start.putExtra("Key",getkey);
                start.putExtra("name",getname);
                Log.d("start ",""+getkey);
                startActivity(start);

            }
        });
/*
        initItem();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.goallist);
        ListAdapter adapter = new ListAdapter(DetailList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);*/
    }
    /*private void initItem() {
        ListItem goal1 = new ListItem("APP开发","大学生创新创业计划", 3);
        DetailList.add(goal1);
        ListItem goal2 = new ListItem("APP开发","大学生创新创业计划", 3);
        DetailList.add(goal2);
        ListItem goal3 = new ListItem("APP开发","大学生创新创业计划", 3);
        DetailList.add(goal3);

    }*/
}

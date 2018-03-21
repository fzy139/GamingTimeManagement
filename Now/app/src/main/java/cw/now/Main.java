package cw.now;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.MenuItem;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import cw.now.Fragment.ChallengeFragment;
import cw.now.Fragment.HonorFragment;
import cw.now.Fragment.ObstacleFragment;
import cw.now.List.ListAdapter;
import cw.now.List.ListItem;

public class Main extends FragmentActivity  {



    public int GoalCounts=0;

    private ChallengeFragment challengeFragment;
    private HonorFragment honorFragment;
    private ObstacleFragment obsFragment;
    private Fragment[] fragments;
    public int lastShowFragment = 0;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        initHonorFragments();

        TextView total=(TextView)findViewById(R.id.total);
        if(total==null){
            Log.d("swFragments: ","textview not found");
        }else
            Log.d("swFragments: ","completed");



        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setElevation(10);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        if(navigation==null){
            Log.d("onCreate: ", "nav not found");}

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        final SharedPreferences.Editor goal_recorder=getSharedPreferences("Goals",MODE_PRIVATE).edit();
        final SharedPreferences goals=getSharedPreferences("Goals",MODE_PRIVATE);//二者顺序,第一次运行相关，先创建才能获取到


    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            final Intent intentS = new Intent(Main.this, Settings.class);
            switch (item.getItemId()) {
                case R.id.navigation_challenge:
                    Log.d("listener", "onNavigationItemSelected:" + item.getItemId());
                    if (lastShowFragment != 1) {
                        Log.d("listener", "onNavigationItemSelected:" + lastShowFragment);
                        switchFragment(lastShowFragment, 1);
                        lastShowFragment = 1;
                    }
                    return true;
                case R.id.navigation_honor:
                    if (lastShowFragment != 0) {
                        switchFragment(lastShowFragment, 0);
                        lastShowFragment = 0;
                        initData();
                    }
                    return true;
               case R.id.navigation_settings:
                    //startActivity(intentS);
                    return true;
                case R.id.navigation_obstacle:
                    if(lastShowFragment!=2){
                        switchFragment(lastShowFragment,2);
                        lastShowFragment=2;
                    }
                    return true;//若没有true flag则无选中效果
            }
            return false;
        }
    };





    public void switchFragment(int lastIndex, int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastIndex]);
        if (!fragments[index].isAdded()) {
            Log.d("switch", "switchFragment:new frag added before switch");
            transaction.add(R.id.container, fragments[index]);}
        transaction.show(fragments[index]).commitNowAllowingStateLoss();//
    }

    private void initHonorFragments() {
        challengeFragment = new ChallengeFragment();
        honorFragment = new HonorFragment();
        obsFragment=new ObstacleFragment();

        fragments = new Fragment[]{ honorFragment,challengeFragment,obsFragment};
        lastShowFragment = 0;
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container,honorFragment)
                .show(honorFragment)
                .commitNowAllowingStateLoss();//调用 commit() 并不立即执行事务.恰恰相反, 它将事务安排排期, 一旦准备好, 就在activity的UI线程上运行(主线程).
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.show(fragments[0]).commitNowAllowingStateLoss();
        TextView total=(TextView)findViewById(R.id.total);
        if(total==null){
            Log.d("initHonorFragments: ","textview not found");
        }else
        Log.d("initHonorFragments: ","completed");
    }

    private void initData(){
        final SharedPreferences.Editor goal_recorder = getSharedPreferences("Goals", MODE_PRIVATE).edit();
        final SharedPreferences goals = getSharedPreferences("Goals", MODE_PRIVATE);//二者顺序
        int GoalID=goals.getInt("GoalID",-1);
        int CurrentCounts=goals.getInt("Counts",0);
        int FinishedCounts=goals.getInt("FinishedCounts",0);
        TextView total=(TextView)findViewById(R.id.total);
        TextView current=(TextView)findViewById(R.id.current);
        TextView finish=(TextView)findViewById(R.id.fin);
        TextView exp_value=(TextView)findViewById(R.id.exp);
        ProgressBar expbar=(ProgressBar)findViewById(R.id.expbar);
        int exp=FinishedCounts*100;
        if(expbar!=null) expbar.setProgress(exp%1000);
        int todo=CurrentCounts-FinishedCounts;
        if(exp_value!=null) exp_value.setText(exp+" exp.");
        Log.d("initData: "," "+CurrentCounts);
        if(total==null) Log.e("initData: ","not found" );
        else total.setText(""+CurrentCounts);
        if(current!=null)current.setText(""+todo);
        if(finish!=null)finish.setText(""+FinishedCounts);
    }
    }







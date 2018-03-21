package cw.now;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Focus extends AppCompatActivity {
    private Vibrator vibrator;
    int span;
    private  float percent;
    private float EffiencyRateNow=1.0F;
    private int FocusTimes=0;
    private int FocusTimesThis=0;
    private long TotalTime=0;
    private long TimeThis=0;
    private long ml=0;
    private int key;
    private boolean timing=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);
        vibrator=(Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);

        //接收键值
        Intent receive=getIntent();
        key=receive.getIntExtra("Key",0);
        String name=receive.getStringExtra("name");
        Log.d("getIntent",""+key+name);

        initSP();


        final SharedPreferences reader=getSharedPreferences("Goals",MODE_PRIVATE);
        final SharedPreferences.Editor editor=getSharedPreferences("Goals",MODE_PRIVATE).edit();

        span=reader.getInt("GID_span"+key,0)*60;// min to sec

        final CountDownTimer countDownTimer;
        final TextView timer=(TextView)findViewById(R.id.timer);
        final TextView per=(TextView)findViewById(R.id.percent);
        final Button start=(Button)findViewById(R.id.start);
        final ProgressBar progressBar=(ProgressBar)findViewById(R.id.progressBar2);
        final TextView SpentNow=(TextView)findViewById(R.id.spent_now);
        final TextView EffiencyNow=(TextView) findViewById(R.id.efficiency_rate_now);
        final TextView PredictTime=(TextView)findViewById(R.id.predict_time);
        final TextView ExpAdd=(TextView)findViewById(R.id.exp_value);

        timer.setText((span/60)+"分"+(span%60)+"秒");
        PredictTime.setText((span/60)+"分"+(span%60)+"秒");
        SpentNow.setText(TimeThis+"分");
        EffiencyNow.setText("0.0");
        ExpAdd.setText("0");

        progressBar.setIndeterminate(false);

        countDownTimer=new CountDownTimer(span*1000,1000) {

            @Override
            public void onTick(long l) {

                timing=true;//Flag正在计时
                ml=l;
                start.setText("我完成咯");

               //倒计时钟
                timer.setText(l/1000/60+"分"+l/1000%60+"秒");

                //进度条与百分比
                percent=(float)l/(span*1000);
                int pShow=(int)(percent*100);
                if(pShow<=50 && l%2==0){//实现闪烁提醒
                    per.setTextColor(getResources().getColor(R.color.colorAccent));}
                    else if(pShow<=50 && l%2!=0){
                    per.setTextColor(getResources().getColor(R.color.cardview_dark_background));
                }
                per.setText(pShow+"%剩余");
                progressBar.setMax(100);
                progressBar.setProgress((int)(percent*100));

                //我已花费
                SpentNow.setText((TimeThis+(span/60-l/1000/60))+"分");
                if( (TimeThis+(span/60-l/1000/60))>=(span/60) && l%2==0){//实现闪烁提醒
                    SpentNow.setTextColor(getResources().getColor(R.color.colorAccent));}
                else if((TimeThis+(span/60-l/1000/60))>=(span/60)   && l%2!=0){
                    SpentNow.setTextColor(getResources().getColor(R.color.cardview_dark_background));
                }

                //效率比
                if((TimeThis+(span/60-l/1000/60))!=0){
                EffiencyRateNow=(span/60)/(TimeThis+(span/60-(float)l/1000/60));}
                Log.d("effiency_rate_now",""+EffiencyRateNow);
                EffiencyNow.setText(""+10.0F);
                if(EffiencyRateNow<=10)
                {EffiencyNow.setText(String.format("%.2f",EffiencyRateNow));}




            }


            public void onPause(){
                timer.setText("已完成");
            }

            @Override
            public void onFinish() {

                timer.setText("已完成");
                NotificationManager notifyManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                //实例化NotificationCompat.Builde并设置相关属性
                NotificationCompat.Builder builder = new NotificationCompat.Builder(Focus.this)
                        //设置小图标
                        .setSmallIcon(R.drawable.head)
                        //设置通知标题
                        .setContentTitle("现在App")
                        //设置通知内容
                        .setContentText("计时器已完成")
                        //设置通知时间，默认为系统发出通知的时间，通常不用设置
                        .setWhen(System.currentTimeMillis());
                //通过builder.build()方法生成Notification对象,并发送通知,id=1
                notifyManager.notify(1, builder.build());
                vibrator.vibrate(new long[]{100,600,300,600},-1);

                //更新数据
                FocusTimes+=1;
                FocusTimesThis+=1;
                TotalTime+=(span/60-ml/1000/60); //unit is min
                TimeThis+=(span/60-ml/1000/60);;
                ;
                //EffiencyNow.setText(""+EffiencyRateNow);
                refreshSP();

                cancel();//停止本次计时


            }
        };

            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!timing) {
                        countDownTimer.start();
                    } else if (timing) {
                        countDownTimer.onFinish();
                        Log.d("onCreate: ", "finished");
                        AlertDialog.Builder finish = new AlertDialog.Builder(Focus.this);
                        finish.setTitle("专注已完成")
                                .setMessage("我用了" + TimeThis + "分")
                                .setMessage("专注第" + FocusTimesThis + "次")
                                .setPositiveButton("任务完成", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        countDownTimer.onFinish();
                                    }
                                })
                                .setNegativeButton("还没完", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        start.setText("再来一次");
                                        countDownTimer.start();
                                    }
                                })
                                .create()
                                .show();
                    }
                }
            });
        }





    private void initSP(){
        //接收SP的键值

        SharedPreferences.Editor writer=getSharedPreferences("FocusData",MODE_PRIVATE).edit();
       SharedPreferences reader=getSharedPreferences("FocusData",MODE_PRIVATE);

        FocusTimes=reader.getInt("FocusTimes",-1);//获取专注总数
        if(FocusTimes==-1){//如果第一次存储，则写入为0
            writer.putInt("FocusTimes",0);
        }

        FocusTimesThis=reader.getInt(key+" FocusTimesThis",-1);//获取该任务专注数
        if( FocusTimesThis==-1){//如果第一次存储，则写入为0
            writer.putInt(key+"FocusTimesThis",0);
        }

        TotalTime=reader.getLong("TotalTime",-1);//获取专注总时间
        if(TotalTime==-1){//如果第一次存储，则写入为0
            writer.putLong("TotalTime",0);
        }

        TimeThis=reader.getLong(key+"TimeThis",-1);//获取该任务专注时间
        if(TimeThis==-1){//如果第一次存储，则写入为0
            writer.putLong(key+"TimeThis",0);
        }
        writer.apply();
    }

    private void refreshSP(){
        SharedPreferences.Editor writer=getSharedPreferences("FocusData",MODE_PRIVATE).edit();
        SharedPreferences reader=getSharedPreferences("FocusData",MODE_PRIVATE);
        writer.putInt("FocusTimes",FocusTimes);
        writer.putInt(key+"FocusTimesThis",FocusTimesThis);
        writer.putLong("TotalTime",TotalTime);
        writer.putLong(key+"TimeThis",TimeThis);
        writer.apply();
    }
}

package cw.now;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.net.IDN;
import java.util.Calendar;

public class DdlSetting extends AppCompatActivity {

    private int hourSet=0;
    private int minSet=25;
    public int yn;
    public int mn;
    public int dn;
    boolean change=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ddl_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
              //EditText不会自动获取焦点并且不会弹出键盘，代码：
             //将其父控件设置f
        NumberPicker hour=(NumberPicker)findViewById(R.id.hourpicker);
        NumberPicker min=(NumberPicker)findViewById(R.id.minpicker);
        hour.setMinValue(0);
        hour.setMaxValue(100);
        hour.setValue(0);
        min.setMinValue(0);
        min.setMaxValue(59);
        min.setValue(25);
        hour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                 hourSet=i1;
            }
        });
        min.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
               minSet=i1;
            }
        });
        final Calendar c=Calendar.getInstance();//TODO:计算时间差
       yn=c.get(Calendar.YEAR);
       mn=c.get(Calendar.MONTH);
       dn=c.get(Calendar.DAY_OF_MONTH);


        final DatePicker ddl=(DatePicker) findViewById(R.id.datePicker);
        ddl.init(yn, mn, dn, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int y, int m, int d) {
                mn=m+1;
                dn=d;
            }
        });


        ImageButton save=(ImageButton) findViewById(R.id.finish);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int timespan=hourSet*60+minSet;//转换成分钟
                String deadline;
                if(mn!=c.get(Calendar.MONTH)&&dn!=c.get(Calendar.DAY_OF_MONTH)) deadline=mn+"月"+dn+"日";
                else deadline="今天";
                Log.d("ddl", "onClick:"+timespan+deadline);

                SharedPreferences.Editor timeEditor=getSharedPreferences("Goals",MODE_PRIVATE).edit();
                SharedPreferences rec=getSharedPreferences("Goals",MODE_PRIVATE);

                Intent intent_get=getIntent();
                int Key=intent_get.getIntExtra("key",-1);
                Log.d( "ddlsetting","get"+Key);//从上个活动中获取key

                if(Key==-1) Log.e("GID","lost" );
                timeEditor.putInt("GID_span"+Key,timespan);
                timeEditor.putString("GID_ddl"+Key,deadline);//截止日期
                timeEditor.apply();

                Snackbar.make(view,"期限已设定",Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                Intent intent_back=new Intent();
                intent_back.putExtra("ddl_return",timespan+","+deadline);
                intent_back.putExtra("span_return",timespan);
                setResult(RESULT_OK,intent_back);
                finish();
            }
        });

    }

}

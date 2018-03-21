package cw.now.List;

/**
 * Created by Thinkpad on 2018/2/12.
 */



public class ListItem {
    private String mName;
    private int mCat;
    private String mReward;
    private int mSpan_mi;
    private int mSpan_hh;
    private int mDdl_dd;
    private int mDdl_mm;
    private int mKey;
    private String mDdl;
    private boolean mFinish=false;

    public ListItem(String name,String reward,String ddl,int cat,int min,int hour,int day,int month,int key,boolean finish){
        this.mName=name;
        this.mCat=cat;
        this.mReward=reward;
        this.mSpan_mi=min;
        this.mSpan_hh=hour;
        this.mDdl_dd=day;
        this.mDdl_mm=month;
        this.mKey=key;
        this.mDdl=ddl;
        this.mFinish=finish;
    }

    public boolean getFinish(){
        return  mFinish;
    }

    public String getName(){
        return mName;
    }

    public String getReward(){
        return mReward;
    }
    public String getDdl(){
        return mDdl;
    }
    public int getDdlMin(){
        return mSpan_mi;
    }
    public int getDdlHour(){
        return mSpan_hh;
    }
    public int getDdlDay(){
        return mDdl_dd;
    }
    public int getDdlMon(){
        return mDdl_mm;
    }
    public int getKey(){
        return  mKey;
    }

    public int getCat(){
        return mCat;
    }

}


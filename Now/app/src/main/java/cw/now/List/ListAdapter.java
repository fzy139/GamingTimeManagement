package cw.now.List;

/**
 * Created by Thinkpad on 2018/2/12.
 */
/*适配器的作用是对接数据与view中的显示，viewholder在initItem之后调用，是一个持有者的类，他里面一般没有方法，只有属性，作用就是一个临时的储存器
* 就，把你getView方法中每次返回的View存起来，可以下次再用。
* 这样做的好处就是不必每次都到布局文件中去拿到你的View，提高了效率。
*/

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cw.now.Fragment.ChallengeFragment;
import cw.now.R;
import cw.now.ScrollingActivity;

import static android.content.Context.MODE_PRIVATE;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private List<ListItem> mListItemList;
    private List<ListItem> GoalList = new ArrayList<>();
    private Context context;
    String spanShow=new String();
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView Name;
        TextView Deadline;
        TextView Reward;
        CheckBox Finished;
        View itemView;

        public ViewHolder(View view) {
            super(view);
            itemView = view;
            Name = (TextView) view.findViewById(R.id.goalname);
            Deadline = (TextView) view.findViewById(R.id.deadline);
            Reward = (TextView) view.findViewById(R.id.reward);
            Finished = (CheckBox) view.findViewById(R.id.checkBox);
        }
    }

    public ListAdapter(List<ListItem> ListItemList) {
        mListItemList = ListItemList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goalitem, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        context = parent.getContext();
        Log.d( "onCreateViewHolder: ",""+context);
        final Intent intent = new Intent(context, ScrollingActivity.class);
        //为item中的checkbox注册监听器，目标finished
        holder.Finished.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {//如果已勾选
                    SharedPreferences.Editor wri = context.getSharedPreferences("Goals", MODE_PRIVATE).edit();
                    SharedPreferences rec = context.getSharedPreferences("Goals", MODE_PRIVATE);
                    int pos = holder.getAdapterPosition();
                    ListItem ListItem = mListItemList.get(pos);
                    Log.d("onCheckedChanged: ", "check" + pos);
                    int GID = ListItem.getKey();
                    boolean Finished = rec.getBoolean("GID_finished" + GID, false);
                    String name = rec.getString("GID_name" + GID, "null");
                    if (!Finished) {//未完成的任务勾选
                        int Counts = rec.getInt("Counts", -1);
                        int FCounts = rec.getInt("FinishedCounts", 0);
                        int FCounts_new = FCounts + 1;
                        Log.d("Finished", "" + FCounts_new);
                        wri.putInt("FinishedCounts", FCounts_new);
                        wri.putBoolean("GID_finished" + GID, true);
                        wri.apply();
                        holder.itemView.setAlpha(0.5f);
                    } else {//已完成的任务勾选
                        int FCounts = rec.getInt("FinishedCounts", 0);
                        int FCounts_new = FCounts - 1;
                        Log.d("Finished", "" + FCounts_new);
                        wri.putInt("FinishedCounts", FCounts_new);
                        wri.putBoolean("GID_finished" + GID, false);
                        wri.apply();
                        holder.itemView.setAlpha(1f);//恢复透明度
                    }
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final int pos = holder.getAdapterPosition();
                final ListItem ListItem = mListItemList.get(pos);

                final SharedPreferences.Editor delete = context.getSharedPreferences("Goals", MODE_PRIVATE).edit();
                final SharedPreferences rec = context.getSharedPreferences("Goals", MODE_PRIVATE);

                Snackbar.make(view, "确定要删除" + ListItem.getName() + "吗？", Snackbar.LENGTH_LONG)
                        .setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mListItemList.remove(pos);
                                notifyItemRemoved(pos);
                                notifyDataSetChanged();
                                int GoalID = rec.getInt("GoalID", -1);
                                int Counts = rec.getInt("Counts", -1);
                                int FCounts = rec.getInt("FinishedCounts", -1);
                                int Key = ListItem.getKey();
                                delete.putInt("Counts", Counts - 1);
                                if (rec.getBoolean("GID_finished" + Key, false))
                                    delete.putInt("FinishedCounts", FCounts - 1);
                                delete.remove("GID_name" + Key).apply();//editor操作最后一定要apply，否则无效
                                if (!rec.contains("GID_name" + Key)) {
                                    Log.d("Deleted", "GID_name" + Key);
                                    Log.d("Deleted", "now" + rec.getInt("Counts", -1));
                                    Toast.makeText(view.getContext(), "已删除", Toast.LENGTH_SHORT).show();
                                    initItem();
                                }
                            }
                        }).show();//和toast一样 没有show不显示

                return true;//即可解决长按事件跟点击事件同时响应的问题,表明该事件不必向下传递

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                ListItem ListItem = mListItemList.get(pos);
                intent.putExtra("GoalName", ListItem.getName());
                intent.putExtra("Key", ListItem.getKey());//向点击后事件发送该项键值
                Log.d("adapter", "onClick:" + ListItem.getName());
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        SharedPreferences reader = context.getSharedPreferences("Goals", MODE_PRIVATE);//SP必须在activity下使用，可通过自定义类所在context使用
        ListItem ListItem = mListItemList.get(pos);
        String name = ListItem.getName();
        int GID = ListItem.getKey();

        holder.Name.setText(name);
        if (!ListItem.getDdl().equals("预计 , 截止")) {//字符串比较！=无用

            holder.Deadline.setText(ListItem.getDdl());
        } else holder.Deadline.setText("快设置一个期限吧");
        holder.Reward.setText(ListItem.getReward());
        Boolean finished = reader.getBoolean("GID_finished" + GID, false);
        if (finished && name != "null") {
            holder.itemView.setAlpha(0.5f);
            // holder.Finished.setChecked(true); //可能会导致再次触发监听器 变成未完成
        }


    }

    public int getItemCount() {
        return mListItemList.size();
    }


    public void initItem() {
        int Span;
        Log.d("initItem:", "start"+context);
        final SharedPreferences goals = context.getSharedPreferences("Goals", MODE_PRIVATE);
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


package cw.now.SimpleList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cw.now.List.ListItem;
import cw.now.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Thinkpad on 2018/2/9.
 * Updated from ListAdapter
 */

public class SimpleAdapter extends RecyclerView.Adapter<cw.now.SimpleList.SimpleAdapter.ViewHolder> {
    private List<cw.now.SimpleList.Item> mObsItemList;
    private Context context;
    int OID=0;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView obsName;
        TextView obsDetail;
        View itemView;

        public ViewHolder(View view){
            super(view);
            itemView=view;
            obsName=(TextView)view.findViewById(R.id.obsName);
            obsDetail=(TextView)view.findViewById(R.id.obsDetail);
        }
    }
   //主构造器，无返回值
    public SimpleAdapter(List<cw.now.SimpleList.Item> ObsItemList){
        mObsItemList=ObsItemList; //传入建立的列表对象，便于适配器进行处理 //构造器内的变量会屏蔽类内声明的同名变量，因此只需声明一次

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.obsitem,parent,false);
        final ViewHolder holder=new ViewHolder(view);

        context=parent.getContext();
        //final Intent intent=new Intent(context,MainActivity2.class);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final int pos=holder.getAdapterPosition();
                final Item item=mObsItemList.get(pos);

                final SharedPreferences.Editor delete=context.getSharedPreferences("Obstacle",MODE_PRIVATE).edit();
                final SharedPreferences rec=context.getSharedPreferences("Obstacle",MODE_PRIVATE);

                Snackbar.make(view,"确定要删除"+item.getName()+"吗？",Snackbar.LENGTH_LONG)
                        .setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mObsItemList.remove(pos);
                                notifyItemRemoved(pos);
                                notifyDataSetChanged();
                                int OID=rec.getInt("OID",-1);
                                int Counts=rec.getInt("Counts",-1);

                                int Key=item.getKey();
                                delete.putInt("Counts",Counts-1);
                                delete.remove(""+Key).apply();//editor操作最后一定要apply，否则无效
                                if(!rec.contains(""+Key)) {
                                    Log.d("Deleted", "name"+Key);
                                    Log.d("Deleted", "now"+rec.getInt("Counts",-1));
                                    Toast.makeText(view.getContext(),"已删除",Toast.LENGTH_SHORT).show();
                                }
                                refreshItem();
                            }
                        }).show();//和toast一样 没有show不显示

                return true;//即可解决长按事件跟点击事件同时响应的问题,表明该事件不必向下传递

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos=holder.getAdapterPosition();
                Item obsItem=mObsItemList.get(pos);
              //  context.startActivity(intent);
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int pos){
        Item obsItem=mObsItemList.get(pos);
        holder.obsName.setText(obsItem.getName());
        holder.obsDetail.setText(obsItem.getDetail());

    }

    public  int getItemCount(){
        return mObsItemList.size();
    }

    public void addItem(int position, Item data) {
        mObsItemList.remove(position);
        notifyItemRemoved(position);//通知演示插入动画
        notifyItemRangeChanged(position,mObsItemList.size()-position);//通知数据与界面重新绑定
    }

    private void refreshItem()
    {
        final SharedPreferences.Editor editor=context.getSharedPreferences("Obstacle",Context.MODE_PRIVATE).edit();
        final SharedPreferences reader=context.getSharedPreferences("Obstacle",Context.MODE_PRIVATE);//以obstacle为文件名

        OID=reader.getInt("OID",0);
        Log.d( "refreshItem: ",OID+"now");

        if(OID!=0) {
            mObsItemList.removeAll(mObsItemList);
            for (int i = 0; i < OID; i++) {
                String name = reader.getString("" + i, "null");
                if(name!="null") {
                    Item obsitem = new Item(name, "专注", i);
                    mObsItemList.add(obsitem);
                    Log.d("refreshItem: ", name + "building");
                }else Log.d( "refreshItem: ","skip"+i);
            }

        }
    }
      /* listview的适配器
    @Override
    public View getView(int pos, View convView, ViewGroup parent){
        Item Item=getItem(pos); //对象变量引用类，创建实例
        View view= LayoutInflater.from(getContext()).inflate(resId,parent,false);

        TextView taskName=(TextView)view.findViewById(R.id.obsName);
        taskName.setText(Item.getName());
        return view;
    }*/
}

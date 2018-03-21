package cw.now.Fragment;

/**
 * Created by Thinkpad on 2018/2/14.
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import cw.now.R;

import static android.content.Context.MODE_PRIVATE;

public class HonorFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.honor_fragment, container, false);

        final SharedPreferences.Editor goal_recorder = getActivity().getSharedPreferences("Goals", getActivity().MODE_PRIVATE).edit();
        final SharedPreferences goals = getActivity().getSharedPreferences("Goals", MODE_PRIVATE);//二者顺序
        int GoalID=goals.getInt("GoalID",-1);
        int CurrentCounts=goals.getInt("Counts",0);
        int FinishedCounts=goals.getInt("FinishedCounts",0);
        TextView total=(TextView)view.findViewById(R.id.total);
        TextView current=(TextView)view.findViewById(R.id.current);
        TextView finish=(TextView)view.findViewById(R.id.fin);
        TextView exp_value=(TextView)view.findViewById(R.id.exp);
        ProgressBar expbar=(ProgressBar)view.findViewById(R.id.expbar);
        int exp=FinishedCounts*100;
        if(expbar!=null) expbar.setProgress(exp%1000);
        int todo=CurrentCounts-FinishedCounts;
        if(exp_value!=null) exp_value.setText(exp+" exp.");
        Log.d("initData: "," "+CurrentCounts);
        if(total==null) Log.e("initData: ","not found" );
        else total.setText(""+CurrentCounts);
        if(current!=null)current.setText(""+todo);
        if(finish!=null)finish.setText(""+FinishedCounts);


        return view;
    }

}


package com.mycomp.mrwang.snmpgetparamter.Fragement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycomp.mrwang.snmpgetparamter.Adapter.myViewAdapter;
import com.mycomp.mrwang.snmpgetparamter.utils.CompatUtils;
import com.mycomp.mrwang.snmpgetparamter.R;

import static com.mycomp.mrwang.snmpgetparamter.utils.CompatUtils.getInstance;


/**
 * View pager Fragment
 * Created by Mr Wang on 2016/9/13.
 */

public class ArrayListFragment extends ListFragment {
    int mNum;
    private CompatUtils helper;

    public static ArrayListFragment newInstance(int num){
        ArrayListFragment f=new ArrayListFragment();
        Bundle args=new Bundle();
        args.putInt("num",num);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum=getArguments() !=null? getArguments().getInt("num"):1;
        helper = getInstance(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_pager_list,container,false);
        View tv=v.findViewById(R.id.headtext);
        switch (mNum){
            case 0:
                ((TextView)tv).setText("DS3/E3模式选择");
                break;
            case 1:
                ((TextView)tv).setText("DS3/E3选择");
                break;
            case 2:
                ((TextView)tv).setText("协议选择");
                break;
        }
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        switch (mNum){
            case 0:
                myViewAdapter adapter1 = new myViewAdapter(getActivity(), helper.getList2(), R.layout.viewadapter_main2, helper.getTag1(), new int[]{R.id.checkboxfor3, R.id.checkboxfor4, R.id.viewadaptertext4, R.id.viewadaptertext5}, true);
                setListAdapter(adapter1);
                break;
            case 1:
                myViewAdapter adapter2 = new myViewAdapter(getActivity(), helper.getList1(), R.layout.viewadapter_main2, helper.getTag1(), new int[]{R.id.checkboxfor3, R.id.checkboxfor4, R.id.viewadaptertext4, R.id.viewadaptertext5}, true);
                setListAdapter(adapter2);
                break;
            case 2:
                Object type= helper.getData().get("autoDitect");
                int t=Integer.parseInt(String.valueOf(type));
                myViewAdapter adapter3;
                if (t==0){
                    adapter3 = new myViewAdapter(getActivity(), helper.getList3(),R.layout.viewadapter_main,helper.getTag(),new int[]{R.id.checkboxfor1,R.id.checkboxfor2,R.id.viewadaptertext1,R.id.viewadaptertext2,R.id.viewadaptertext3},true);
                }else {
                    adapter3 = new myViewAdapter(getActivity(), helper.getList3(),R.layout.viewadapter_main,helper.getTag(),new int[]{R.id.checkboxfor1,R.id.checkboxfor2,R.id.viewadaptertext1,R.id.viewadaptertext2,R.id.viewadaptertext3},false);
                }
                setListAdapter(adapter3);
                break;
        }

    }
    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

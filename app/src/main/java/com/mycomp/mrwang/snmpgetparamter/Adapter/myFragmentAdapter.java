package com.mycomp.mrwang.snmpgetparamter.Adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.mycomp.mrwang.snmpgetparamter.Fragement.ArrayListFragment;
import com.mycomp.mrwang.snmpgetparamter.utils.CompatUtils;

import java.util.List;

import static com.mycomp.mrwang.snmpgetparamter.utils.CompatUtils.getInstance;

/**
 * 用于适配参数设置中view pager的适配器
 * Created by Mr Wang on 2016/9/12.
 */

public class myFragmentAdapter extends FragmentPagerAdapter {
    private List<String> title;
    private CompatUtils helper;

    public myFragmentAdapter(FragmentActivity activity, List<String> title) {
        super(activity.getSupportFragmentManager());
        helper = getInstance(activity);
        this.title=title;
    }

    @Override
    public Fragment getItem(int position) {
        return ArrayListFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return helper.getNUM_ITEMS();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
}

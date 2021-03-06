package com.lsy.view;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lsy.view.model.ImgGroups;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by liusiyu.taloner on 2018/3/14.
 */

public class DbVPager extends RelativeLayout {
    ViewPager viewPager;
    TextView tv_index;
    TabLayout tab1;
    TabLayout tab2;
    private BarPositon mPositon = BarPositon.TOP;
    private List<ImgGroups> mDataSource;
    private int[] indexList;
    List<String> imgList;
    private List<String> textList;
    private int vpPosition = 0;
    private boolean isToLeft;
    private boolean tabScrolling;   //防止设置viewpager和TabLayout位置时触发回调
    private boolean vpScrolling;
    private DbCallbackListener mListener;

    public void setBarPosition(BarPositon position) {
        this.mPositon = position;
    }

    public void setSource(List<ImgGroups> imgLists) {
        this.mDataSource = imgLists;
    }

    public void show() {
        init();
    }

    public DbVPager(Context context) {
        super(context);
    }

    public DbVPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DbVPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        inflate(getContext(), R.layout.view_db_vpager, this);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tv_index = (TextView) findViewById(R.id.tv_index);
        tab1 = (TabLayout) findViewById(R.id.tab1);
        tab2 = (TabLayout) findViewById(R.id.tab2);
        ViewPagerAdapter adapter = new ViewPagerAdapter();
        intIndex();
        adapter.bind(getContext(), imgList);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if (vpPosition > positionOffset) {
//                    isToLeft = true;
//                } else if (vpPosition < positionOffset) {
//                    isToLeft = false;
//                }
                //vpPosition = positionOffset;

//                if (positionOffset != 0) {
//                    if (vpPosition >= positionOffsetPixels) {
//                        //右滑
//                        isToLeft = true;
//                    } else if (vpPosition < positionOffsetPixels) {
//                        //左滑
//                        isToLeft = false;
//                    }
//                }
//                vpPosition = positionOffsetPixels;
            }

            @Override
            public void onPageSelected(int position) {
                if (vpPosition > position) {
                    isToLeft = true;
                } else if(vpPosition<position) {
                    isToLeft = false;
                }
                vpPosition = position;

                if (!tabScrolling) {
                    if (isToLeft) {
                        int index = Arrays.binarySearch(indexList, position + 1);
                        if (index >= 0) {
                            TabLayout.Tab tab = getTab().getTabAt(index);
                            if (tab != null) {
                                if (mListener != null) {
                                    mListener.callback(tab.getPosition(), tab.getText().toString());
                                }
                                vpScrolling = true;
                                tab.select();
                                vpScrolling = false;
                            }
                        }
                    } else {
                        int index = Arrays.binarySearch(indexList, position);
                        if (index >= 0) {
                            TabLayout.Tab tab = getTab().getTabAt(index + 1);
                            if (tab != null) {
                                if (mListener != null) {
                                    mListener.callback(tab.getPosition(), tab.getText().toString());
                                }
                                vpScrolling = true;
                                tab.select();
                                vpScrolling = false;
                            }
                        }
                    }
                }
                vpScrolling = false;
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        getTab().addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (mListener != null) {
                    mListener.callback(tab.getPosition(), tab.getText().toString());
                }
                if (!vpScrolling) {
                    int index = tab.getPosition();
                    int pos;
                    if (index == 0) {
                        pos = 0;
                    } else {
                        //获取前面图片数量即目的position
                        pos = indexList[tab.getPosition() - 1];
                    }
                    tabScrolling = true;
                    viewPager.setCurrentItem(pos);
                    tabScrolling = false;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private TabLayout getTab() {
        if (mPositon == BarPositon.TOP) {
            tab1.setVisibility(VISIBLE);
            tab2.setVisibility(GONE);
        } else if (mPositon == BarPositon.BOTTOM) {
            tab2.setVisibility(VISIBLE);
            tab1.setVisibility(GONE);
        } else if (mPositon == BarPositon.EMPTY) {
            tab1.setVisibility(GONE);
            tab1.setVisibility(GONE);
        } else {
            tab2.setVisibility(VISIBLE);
            tab1.setVisibility(GONE);
        }
        return tab1.getVisibility() == VISIBLE ? tab1 : tab2;
    }

    private void intIndex() {
        indexList = new int[mDataSource.size()];
        imgList = new ArrayList<>();
        Iterator<ImgGroups> iterator = mDataSource.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            ImgGroups item = iterator.next();
            TabLayout.Tab tab = getTab().newTab();
            if (null != item && null != item.imgList && item.imgList.size() > 0) {
                if (i > 0) {
                    int temp = indexList[i - 1];
                    indexList[i++] = temp + item.imgList.size();
                } else {
                    indexList[i++] = item.imgList.size();
                }
                String text = TextUtils.isEmpty(item.groupName) ? "null" : item.groupName;

                tab.setText(text);
                getTab().addTab(tab);

                imgList.addAll(item.imgList);
            }
        }
    }

    public void addTabChangeListenr(DbCallbackListener listener) {
        this.mListener = listener;
    }

    public interface DbCallbackListener{
        void callback(int index, String text);
    }

    public enum BarPositon {
        TOP, BOTTOM, EMPTY;
    }
}

package cn.hellovega.zhimingdi.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hellovega.zhimingdi.R;
import cn.hellovega.zhimingdi.ui.activity.MainActivity;
import cn.hellovega.zhimingdi.ui.activity.MapActivity;
import cn.hellovega.zhimingdi.ui.adapter.FunctionFragmentAdapter;
import cn.hellovega.zhimingdi.ui.widget.GridSpacingItemDecoration;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by vega on 3/16/18.
 */

public class FunctionFragment extends Fragment implements
        CalendarView.OnDateSelectedListener,
        CalendarView.OnYearChangeListener {
    private static final String TAG = "FunctionFragment";
    @BindView(R.id.calendarView)
    CalendarView calendarView;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.tv_year_and_month)
    TextView tvYearAndMonth;
    @BindView(R.id.drawerLayoutButton)
    ImageView drawLayoutButton;
    private String date;

    //calender
    private int year;
    private int[] colorList ={0xFF40db25, 0xFFe69138, 0xFFdf1356, 0xFFedc56d, 0xFFedc56d, 0xFFaacc44, 0xFFbc13f0, 0xFF13acf0};
    private Random random =new Random();
    List<String> schemeDateList =new ArrayList<>();
    List<Calendar> schemeList =new ArrayList<>();

    //mention widget
    private FunctionFragmentAdapter functionFragmentAdapter =new FunctionFragmentAdapter();
    public static SharedPreferences mentionXml;

    //birth widget
    private View vBirth;
    private EditText etBirthName;
    private TimePickerView tpvCustom;
    private MaterialDialog birthmaterialDialog;
    private String birthDate;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mentionXml = getActivity().getSharedPreferences("mention", getActivity().MODE_PRIVATE);
        int month =calendarView.getCurMonth();
        int day =calendarView.getCurDay();
        date=String.valueOf(calendarView.getCurYear()); date+=(month<10)?"0"+month:month; date+=(day<10)?"0"+day:day;
        functionFragmentAdapter.init();
        functionFragmentAdapter.setContents(date);

        calendarView.setOnYearChangeListener(this);
        calendarView.setOnDateSelectedListener(this);
        setCalendarViewScheme();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_functions, container,false);
        ButterKnife.bind(this, view);

        initBirthWidget();
        year =calendarView.getCurYear();
        tvYearAndMonth.setText(year+"年"+calendarView.getCurMonth()+"月");

        recyclerView.setAdapter(functionFragmentAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 8,true));
        functionFragmentAdapter.setViewClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                TextView textView =view.findViewById(R.id.textView);
                saveMention(recyclerView.getChildAdapterPosition(view), textView.getText().toString());
            }
        });
        functionFragmentAdapter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View mentioniView) {
                final MaterialDialog materialDialog = new MaterialDialog(getContext())
                        .setMessage("是否确认删除?");
                materialDialog.setPositiveButton("是", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String mention =((TextView)mentioniView.findViewById(R.id.textView)).getText().toString();
                                if( mention.endsWith("生日")) {
                                    functionFragmentAdapter.deleteBirth(date, recyclerView.getChildAdapterPosition(mentioniView), mention);
                                    setCalendarViewScheme();
                                }else{
                                    if (recyclerView.getChildAdapterPosition(mentioniView) == 0)
                                        deleteCalendarViewScheme(date);
                                    functionFragmentAdapter.delete(date, recyclerView.getChildAdapterPosition(mentioniView));
                                }
                                materialDialog.dismiss();
                            }
                        }).setNegativeButton("否", new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {
                                materialDialog.dismiss();
                            }
                        }).show();
                return true;
            }
        });
        return view;
    }

    @OnClick(R.id.drawerLayoutButton)
    public void onDrawexrLayoutButtonClick() {
        ((MainActivity)getActivity()).showDrawer();
    }


    @OnClick(R.id.mapButton)
    public void onMapButtonClick() {
        Intent intent=new Intent(getActivity(), MapActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.birthButton)
    public void onBirthButtonClick() {
        etBirthName.setText("");
        birthmaterialDialog.show();
    }

    private void initBirthWidget() {
        vBirth =View.inflate(getContext(), R.layout.widget_birth, null);
        etBirthName = vBirth.findViewById(R.id.name);
        initLunarPicker();
        birthmaterialDialog =new MaterialDialog(getContext())
                .setTitle("设置生日提醒")
                .setContentView(vBirth);

        birthmaterialDialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tpvCustom.returnData();
                SharedPreferences.Editor editor =mentionXml.edit();
                if(date.substring(4).equals(birthDate))   {
                    for(int i1=calendarView.getCurYear();i1<2099;++i1) {
                        int i2=0;
                        while (mentionXml.getString(i1+birthDate + "_" + i2, null) != null) ++i2;
                        if(date.substring(0,4).equals(String.valueOf(i1))) {
                            functionFragmentAdapter.insert(i1+birthDate, i2, etBirthName.getText()+"生日");
                            continue;
                        }else
                            editor.putString(i1+birthDate + "_" + i2, etBirthName.getText()+"生日");
                        insertCalendarViewScheme(i1+birthDate);
                    }
                }else {
                    for(int i1=calendarView.getCurYear();i1<2099;++i1) {
                        int i2=0;
                        while (mentionXml.getString(i1+birthDate + "_" + i2, null) != null) ++i2;
                        editor.putString(i1+birthDate + "_" + i2, etBirthName.getText()+"生日");
                        insertCalendarViewScheme(i1+birthDate);
                    }
                }
                editor.commit();
                birthmaterialDialog.dismiss();
            }
        }).setNegativeButton("取消",new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                birthmaterialDialog.dismiss();
            }
        });
    }
    private void initLunarPicker() {
        java.util.Calendar selectedDate = java.util.Calendar.getInstance();//系统当前时间
        java.util.Calendar startDate = java.util.Calendar.getInstance();
        startDate.set(2000, 0, 1);
        java.util.Calendar endDate = java.util.Calendar.getInstance();
        endDate.set(2000, 11, 31);

        tpvCustom = new TimePickerView.Builder(getContext(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                DateTime dt =new DateTime(date);
                birthDate =dt.toString("MMdd");
            }
        })
                .setType(new boolean[]{false, true, true, false, false, false})
                .isCenterLabel(false)
                .setLayoutRes(R.layout.pickerview_custom_lunar, new CustomListener() {
                    @Override
                    public void customLayout(View v) {

                    }
                })
                .setDividerColor(Color.RED)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setDecorView((FrameLayout)vBirth.findViewById(R.id.birth_date))
                .build();
        tpvCustom.show();
    }


    @OnClick(R.id.noteButton)
    public void onNoteButton() {
        saveMention(-1, null);
    }

    private void saveMention(final int key,String mention) {
        View view = getLayoutInflater().inflate(R.layout.widget_share_note, null);
        final EditText editText = view.findViewById(R.id.text);
        if(mention!=null && !mention.isEmpty()) editText.setText(mention);
        final MaterialDialog materialDialog = new MaterialDialog(getActivity())
                .setTitle(date)
                .setContentView(view);
        materialDialog.setNegativeButton("取消", new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                materialDialog.dismiss();
            }
        }).setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i1 = 0;
                if(key==-1) while (mentionXml.getString(date + "_" + i1, null) != null) ++i1;
                else i1 = key;
                if(i1==key) functionFragmentAdapter.change(date , i1, editText.getText().toString());
                else { functionFragmentAdapter.insert(date , i1, editText.getText().toString()); insertCalendarViewScheme(date);}

                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }

    private void setCalendarViewScheme() {
        schemeDateList.clear();
        schemeList.clear();
        for(String str : mentionXml.getAll().keySet())
            if(!schemeDateList.contains(str.substring(0,8)))
                schemeDateList.add(str.substring(0,8));
        for(String date : schemeDateList)
            schemeList.add(getCalender(date));
        calendarView.setSchemeDate(schemeList);
    }
    public void insertCalendarViewScheme(String date) {
        if(!schemeDateList.contains(date)) {
            schemeDateList.add(date);
            schemeList.add(getCalender(date));
            calendarView.update();
        }
    }
    public void deleteCalendarViewScheme(String date) {
        int index =schemeDateList.indexOf(date);
        schemeList.remove(index);
        schemeDateList.remove(index);
        calendarView.update();
    }
    private Calendar getCalender(String date) {
        Calendar calendar =new Calendar();
        calendar.setYear(Integer.valueOf(date.substring(0,4)).intValue());
        calendar.setMonth(Integer.valueOf(date.substring(4,6)).intValue());
        calendar.setDay(Integer.valueOf(date.substring(6,8)).intValue());
        calendar.setSchemeColor(colorList[random.nextInt(colorList.length)]);

        calendar.setScheme("");

        return calendar;
    }




    @OnClick(R.id.tv_year_and_month)
    public void onYearAndMonthButtonClick() {
        tvYearAndMonth.setText(String.valueOf(year));
        calendarView.showYearSelectLayout(year);
    }

    @Override
    public void onDateSelected(Calendar calendar, boolean isClick) {
        int year =calendar.getYear(),month =calendar.getMonth(),day =calendar.getDay();
        tvYearAndMonth.setText(year+"年"+month+"月");
        date=String.valueOf(year); date+=(month<10)?"0"+month:month; date+=(day<10)?"0"+day:day;
        functionFragmentAdapter.setContents(date);
    }


    @Override
    public void onYearChange(int year) {
        tvYearAndMonth.setText(String.valueOf(year));
        this.year =year;
    }


    public void showDrawerLayoutButton(boolean ifShow) {

//        Log.e(TAG, "showDrawerLayoutButton: "+String.valueOf(getActivity().findViewById(R.id.tv_year_and_month)==null) );
//        Log.e(TAG, "showDrawerLayoutButton: "+String.valueOf(tvYearAndMonth==null) );
        if(ifShow) drawLayoutButton.setVisibility(View.VISIBLE);
        else drawLayoutButton.setVisibility(View.GONE);
    }
}

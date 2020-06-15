package com.teamsavezone.smartcart;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.sip.SipSession;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

public class CartList extends AppCompatActivity {

    private LinearLayout container;
    private String table;
    private Realm realm;
    private Realm exp_realm;
    private TextView textView;
    SimpleDateFormat idFormat;

    public static Context cart_context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);
        container = (LinearLayout) findViewById(R.id.cart_list);
        idFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        cart_context=this;

        RealmConfiguration expConfig = new RealmConfiguration.Builder().build();
        exp_realm = Realm.getInstance(expConfig);

        //최초 실행시
        if(!MyApplication.initExp){
            //ExpList 테이블 생성
            addFood("사과", 1, 21);
            addFood("바나나", 2, 21);
            addFood("오이", 1, 7);
            addFood("양파", 1, 4);
            addFood("무", 1, 7);
            addFood("딸기", 1, 3);

            MyApplication.initExp = true;
        }

        realm = Realm.getDefaultInstance();
        createTuple("양파");

        /* Inflater 이용 방식 -> 작동 안함
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_classify, null);

        Button add_btn = (Button) view.findViewById(R.id.add_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = convertName(MyApplication.name);
                createTuple(name);
            }
        });
        */
    }

    public void onDestroy(){
        super.onDestroy();
        exp_realm.close();
        realm.close();
    }

    public void addFood(final String name, final int storage, final int expire){

        //이미 있으면 생성하지 않음
        if(exp_realm.where(ExpList.class).equalTo("name", name).findAll().size() != 0)
            return;

        exp_realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ExpList expList = exp_realm.createObject(ExpList.class);
                expList.setName(name);
                expList.setStorage(storage);
                expList.setExpire(expire);
            }
        });
    }

    //tuple 생성
    public void createTuple(final String name){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                UserList userList = realm.createObject(UserList.class);

                /*******current 설정*******/
                userList.setCurrent(new Date());

                /*******name 설정*******/
                userList.setName(name);

                /*******id 설정*******/
                String id = idFormat.format(userList.getCurrent());
                userList.setId(id);

                //나머지 data는 expList table을 참고해서 설정함
                ExpList expList = exp_realm.where(ExpList.class).equalTo("name", name).findAll().first();

                /*******storage 설정*******/
                if(expList.getStorage() == 0)
                    userList.setStorage("상온");
                else if(expList.getStorage() == 1)
                    userList.setStorage("냉장");
                else if(expList.getStorage() == 2)
                    userList.setStorage("냉동");
                else
                    userList.setStorage("미정");

                /*******expire 설정*******/
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(userList.getCurrent());
                calendar.add(Calendar.DATE, expList.getExpire());
                userList.setExpire(calendar.getTime());

                showResult();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void showResult(){
        RealmResults<UserList> results = realm.where(UserList.class).findAll();
        for(UserList data : results){
            final Button button = new Button(this);
            button.setText(data.toString());
            button.setBackground(getResources().getDrawable(R.drawable.list_btn_background));
            container.addView(button);
            final String id = data.getId();

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //삭제하시겠습니까? 메세지
                    //예 아니오 --> 예 클릭 시 해당 데이터베이스 삭제
                    showMessage(id, button);
                    container.invalidate();
                }
            });
        }
    }

    private void showMessage(final String id, final Button button){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("품목 삭제");
        builder.setMessage("삭제하시겠습니까?");

        //예 클릭 시
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //해당 id를 갖는 tuple 삭제
                final RealmResults<UserList> results = realm.where(UserList.class).equalTo("id", id).findAll();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        results.deleteFirstFromRealm();
                        container.removeView(button);
                    }
                });
            }
        });

        //아니오 클릭 시
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



}
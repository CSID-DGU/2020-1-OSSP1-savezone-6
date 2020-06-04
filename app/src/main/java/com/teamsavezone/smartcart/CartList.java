package com.teamsavezone.smartcart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class CartList extends AppCompatActivity {

    private LinearLayout container;
    private String table;
    private Realm realm;
    private TextView textView;
    SimpleDateFormat idFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);
        container = (LinearLayout) findViewById(R.id.cart_list);
        textView = (TextView) findViewById(R.id.text_cart);

        //최초 실행시
        if(!MyApplication.initExp){
            //ExpList 테이블 생성
            Toast.makeText(this, idFormat.format(new Date()), Toast.LENGTH_LONG).show();
            //MyApplication.initExp = true;
        }

        realm = Realm.getDefaultInstance();
        //createTuple("양파");
    }

    public void onDestroy(){
        super.onDestroy();
        realm.close();
    }

    //tuple 생성
    public void createTuple(final String name){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                UserList userList = realm.createObject(UserList.class);
                //촬영 시점
                userList.setCurrent(new Date());
                //id를 현재 날짜로 하고싶은데 왜 오류가 날까 엉엉
                userList.setId(0);
                userList.setName(name);

                userList.setStorage(1);
                userList.setExpire(new Date());
                showResult();
            }
        });
    }

    /*
    public void createTextView(String table){
        TextView textView = new TextView(this);
        textView.setText(table);
        container.addView(textView);
    }*/

    private void showResult(){
        RealmResults<UserList> results = realm.where(UserList.class).findAll();
        textView.setText(results.toString());
    }
}
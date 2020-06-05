package com.teamsavezone.smartcart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);
        container = (LinearLayout) findViewById(R.id.cart_list);
        textView = (TextView) findViewById(R.id.text_cart);
        idFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        RealmConfiguration expConfig = new RealmConfiguration.Builder().build();
        exp_realm = Realm.getInstance(expConfig);

        //최초 실행시
        if(!MyApplication.initExp){
            //ExpList 테이블 생성
            addFood("양파", 1, 4);
            addFood("사과", 1, 21);
            Toast.makeText(this, idFormat.format(new Date()), Toast.LENGTH_LONG).show();
            //MyApplication.initExp = true;
        }

        realm = Realm.getDefaultInstance();
        createTuple("양파");
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

    private void showResult(){
        RealmResults<UserList> results = realm.where(UserList.class).findAll();
        textView.setText(results.toString());
    }

}
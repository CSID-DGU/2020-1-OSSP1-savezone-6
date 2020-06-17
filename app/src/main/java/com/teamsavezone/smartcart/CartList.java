package com.teamsavezone.smartcart;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class CartList extends AppCompatActivity {

    private LinearLayout container;
    private Realm realm;
    private Realm exp_realm;
    SimpleDateFormat idFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);
        container = (LinearLayout) findViewById(R.id.cart_list);
        idFormat = new SimpleDateFormat("yyyyMMddHHmmss");

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

        if(MyApplication.addCart){
            String name = convertName(MyApplication.name);
            Toast.makeText(this, name + "가 리스트에 담겼습니다.", Toast.LENGTH_SHORT).show();
            createTuple(name);
            MyApplication.addCart = false;
        }

        showResult();
    }

    public void onDestroy(){
        super.onDestroy();
        exp_realm.close();
        realm.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        super.onBackPressed();
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

    public String convertName(String name){
        String cName=null;

        if(name.equals("apple"))
            cName="사과";
        else if(name.equals("banana"))
            cName="바나나";
        else if(name.equals("cucumber"))
            cName="오이";
        else if(name.equals("milk"))
            cName="우유";
        else if(name.equals("onion"))
            cName="양파";
        else if(name.equals("radish"))
            cName="무";
        else if(name.equals("strawberry"))
            cName="딸기";

        return cName;
    }

}
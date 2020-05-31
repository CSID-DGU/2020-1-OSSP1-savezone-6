package com.teamsavezone.smartcart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class CartList extends AppCompatActivity {

    private LinearLayout container;
    private String table;
    private Realm realm;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);
        container = (LinearLayout) findViewById(R.id.cart_list);
        textView = (TextView) findViewById(R.id.text_cart);

        realm = Realm.getDefaultInstance();
        createTable("양파");
    }

    public void onDestroy(){
        super.onDestroy();
        realm.close();
    }


    public void createTable(final String name){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                UserList userList = realm.createObject(UserList.class);
                userList.setId(0);
                userList.setName(name);
                userList.setCurrent(new Date());
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
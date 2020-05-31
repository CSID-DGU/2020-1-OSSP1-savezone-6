package com.teamsavezone.smartcart;

import androidx.fragment.app.Fragment;

import android.os.Bundle;

import io.realm.Realm;

public class FragmentActivity extends Fragment {

    private Realm realm;

    @Override
    public void onStart(){
        super.onStart();
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onStop(){
        super.onStop();
        realm.close();
    }
}

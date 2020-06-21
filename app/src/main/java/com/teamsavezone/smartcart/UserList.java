package com.teamsavezone.smartcart;


import java.text.SimpleDateFormat;
import java.util.Date;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserList extends RealmObject {

    //id(촬영 날짜의 concat으로 설정)
    String id;
    //식품명
    String name;
    //구매 일자
    Date current;
    //보관 방법
    String storage;
    //유통기한 만료일 (current + ExpList.expire)
    Date expire;

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Date getCurrent(){
        return current;
    }

    public void setCurrent(Date current){
        this.current = current;
    }

    public String getStorage(){
        return storage;
    }

    public void setStorage(String storage){
        this.storage = storage;
    }

    public Date getExpire(){
        return expire;
    }

    public void setExpire(Date expire){
        this.expire = expire;
    }

    @Override
    public String toString() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return "품목명 : " + name +
                "\n보관 방법 : " + storage +
                "\n구매 일자 : " + dateFormat.format(current) +
                "\n유통 기한 : " + dateFormat.format(expire);
    }
}

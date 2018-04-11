package com.example.user.otherproject.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Ramon on 1/30/2018.
 */

public class Child implements Parcelable {

    private String childID;
    private ArrayList<String> childResponses;


    public Child(){


    }

    public String getChildID() {
        return childID;
    }

    public void setChildID(String childID) {
        this.childID = childID;
    }

    public ArrayList<String> getChildResponses() {
        return childResponses;
    }

    public void setChildResponses(ArrayList<String> childResponses) {
        this.childResponses = childResponses;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags){
        out.writeString(childID);
        out.writeList(childResponses);
    }


    public static final Parcelable.Creator<Child> CREATOR = new Parcelable.Creator<Child>(){

        @Override
        public Child createFromParcel(Parcel in){
            return new Child(in);
        }

        @Override
        public Child[] newArray(int size){
            return new Child[size];
        }
    };

    private Child(Parcel in){
        childID = in.readString();
        childResponses = in.readArrayList(null);
    }
}

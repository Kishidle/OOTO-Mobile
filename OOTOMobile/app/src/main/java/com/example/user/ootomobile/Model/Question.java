package com.example.user.ootomobile.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Ramon on 3/12/2018.
 */

public class Question implements Parcelable {

    private String questionText;
    private int questionNum;
    private String questionLabel;
    private ArrayList<String> featureGroup;
    private ArrayList<Integer> featureNum;
    private ArrayList<String> featureText;

    public Question(){
        featureGroup = new ArrayList<String>();
        featureNum = new ArrayList<Integer>();
        featureText = new ArrayList<String>();
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionLabel() {
        return questionLabel;
    }

    public void setQuestionLabel(String questionNum) {
        this.questionLabel = questionNum;
    }

    public int getQuestionNum(){
        return questionNum;
    }

    public void setQuestionNum(int questionNum){
        this.questionNum = questionNum;
    }

    public void addFeatureGroup(String feature){
        featureGroup.add(feature);
    }
    public void addFeatureNum(int featureNum){
        this.featureNum.add(featureNum);
    }
    public void addFeatureText(String featureText){
        this.featureText.add(featureText);
    }

    public ArrayList<String> getFeatureGroup() {
        return featureGroup;
    }

    public ArrayList<Integer> getFeatureNum() {
        return featureNum;
    }

    public ArrayList<String> getFeatureText() {
        return featureText;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags){
        out.writeString(questionText);
        out.writeString(questionLabel);
        //out.writeString(featureGroup);
        //out.writeInt(featureNum);
        //out.writeString(featureText);
    }

    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>(){

        @Override
        public Question createFromParcel(Parcel in){
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size){
            return new Question[size];
        }
    };

    private Question(Parcel in){
        questionText = in.readString();
        questionLabel = in.readString();
        //featureGroup = in.readString();
        //featureNum = in.readInt();
        //featureText = in.readString();
    }

}

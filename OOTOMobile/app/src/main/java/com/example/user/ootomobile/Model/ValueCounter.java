package com.example.user.ootomobile.Model;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Ramon on 1/30/2018.
 */

public class ValueCounter {

    private int[] valResponse;
    private int[] valGroup = {0, 0};
    private ArrayList<Child> childList;
    private int questionNum;
    private ArrayList<Question> questionList;


    public ValueCounter(ArrayList<Child> childList, int questionNum, ArrayList<Question> questionList){
        this.childList = childList;
        this.questionNum = questionNum;
        this.questionList = questionList;
        valResponse = new int[questionList.get(questionNum).getFeatureNum().size()];
        setResponse();
    }

    public void setResponse(){
        for(int i = 0; i < childList.size(); i++){
            Log.d("valuecounterchart", childList.get(i).getChildResponses().get(questionNum));
            for(int j = 0; j < questionList.get(questionNum).getFeatureNum().size(); j++){
                int childAnswer = Integer.parseInt(childList.get(i).getChildResponses().get(questionNum));
                if(childAnswer == questionList.get(questionNum).getFeatureNum().get(j)){
                    valResponse[j]++;
                    if(questionList.get(questionNum).getFeatureGroup().get(j).equals("a")){
                        valGroup[0]++;
                    }
                    else if(questionList.get(questionNum).getFeatureGroup().get(j).equals("b")){
                        valGroup[1]++;
                    }
                }

            }

        }
    }
    public int[] getGroup(){
        return valGroup;
    }
    public int[] getResponse(){
        return valResponse;
    }
}

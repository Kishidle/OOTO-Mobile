package com.example.user.ootomobile;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.ootomobile.Controller.DBHelper;
import com.example.user.ootomobile.Model.Child;
import com.example.user.ootomobile.Model.Question;
import com.example.user.ootomobile.Model.ValueCounter;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    private PieChart mPieLeft;
    private PieChart mPieRight;
    private ArrayList<Child> childListLeft, childListRight;
    private ArrayList<Child> filteredList;
    private ArrayList<PieEntry> pieEntries1;
    private ArrayList<PieEntry> pieEntries2;
    private ArrayList<Question> questionList;
    private RelativeLayout graphLayoutLeft, graphLayoutRight;
    private String xData, xDataRight, questionLabel, questionString, confInterval;
    private int[] yDataLeft, yDataRight;
    private DBHelper mDBHelper;
    private SQLiteDatabase db;
    private int leftFilterNum, rightFilterNum;
    private TextView resultText, questionDesc, leftDetails, rightDetails;
    private EditText questionCode;
    private View colorBar;
    private Question question;
    private int questionNum = 0;
    private int totalCount = 0;
    private ValueCounter vCountLeft, vCountRight;
    private int classValueLeft, classValueRight;
    private int[] answerCount;
    private int pieTotalLeft, pieTotalRight;
    private Spinner confSpinner;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        //TODO edit Child class to completely include the synthetic dataset
        //TODO next and previous buttons semi-done need to refresh/change the charts and stuff
        //TODO colored square box thing done
        //TODO back button to main menu done
        //TODO get database for sample and the features
        //TODO computations not started yet

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        String fileCSVLeft = getIntent().getStringExtra("listLeft");
        String fileCSVRight = getIntent().getStringExtra("listRight");
        String featureCSV = getIntent().getStringExtra("featureList");

        childListLeft = new ArrayList<Child>();
        childListRight = new ArrayList<Child>();
        questionList = new ArrayList<Question>();
        prepareDataset(fileCSVLeft, childListLeft);
        prepareDataset(fileCSVRight, childListRight);
        prepareFeatures(featureCSV, questionList);

        Log.d("intenttest", questionList.get(0).getFeatureText().get(0));

        //mPie1=(PieChart) findViewById(R.id.piechart1);
        //mPie2=(PieChart) findViewById(R.id.piechart2);

        leftDetails = (TextView) findViewById(R.id.leftDetails);
        rightDetails = (TextView) findViewById(R.id.rightDetails);

        questionLabel = questionList.get(0).getQuestionLabel();
        questionString = questionList.get(0).getQuestionText();
        //preliminary set question text
        questionDesc = (TextView) findViewById(R.id.question_desc);
        questionCode = (EditText) findViewById(R.id.question_code);

        questionCode.setText(questionLabel);
        questionDesc.setText(": "+ questionString);
        resultText = (TextView) findViewById(R.id.result_text);

        questionCode.setOnEditorActionListener(new TextView.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                String input;
                Log.d("oneditortest2", "test");
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    Log.d("oneditortest", v.getText().toString());
                    input = v.getText().toString();
                    questionCode.clearFocus();
                    hideKeyboard(v);
                    findQuestion(input);
                    return true;
                }
                return false;
            }

        });
        graphLayoutLeft = (RelativeLayout) findViewById(R.id.graph_container_left);
        graphLayoutRight = (RelativeLayout) findViewById(R.id.graph_container_right);

        confSpinner = (Spinner) findViewById(R.id.conf_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.conf_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        confSpinner.setAdapter(adapter);
        confInterval = "99%";

        confSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
           @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l){
               confInterval = confSpinner.getSelectedItem().toString();

           }
           @Override
            public void onNothingSelected(AdapterView<?> adapterView){

           }
        });

        pieEntries1 = new ArrayList<>();
        pieEntries2 = new ArrayList<>();
        //childList = new ArrayList<>();
        filteredList = new ArrayList<>();
        Log.d("hitest", "testing it started here");
        createCharts();

        graphLayoutLeft.addView(mPieLeft);
        graphLayoutRight.addView(mPieRight);

        ViewGroup.LayoutParams params = mPieLeft.getLayoutParams();
        params.height = 450;
        params.width = 450;

        ViewGroup.LayoutParams paramsRight = mPieRight.getLayoutParams();
        paramsRight.height = 450;
        paramsRight.width = 450;

        preparePieChartData2(mPieLeft, childListLeft, "left");
        preparePieChartData2(mPieRight, childListRight, "right");
        computeChiStat();

        //prepareChartData();

    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void createCharts(){
        mPieLeft = createPieChart();
        mPieRight = createPieChart();
    }


    public void prepareDataset(String fileCSV, ArrayList<Child> childList){
        /*
        Prepares data coming from the left and right dataset buttons, and adds them to the
        corresponding arraylist.
         */

        AssetManager manager = this.getAssets();
        InputStream inStream = null;
        try{
            inStream = manager.open(fileCSV);
        } catch(IOException e){
            e.printStackTrace();
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
        String line = "";
        boolean isHeader = true;
        try{
            while((line = buffer.readLine()) != null){
                if(isHeader){
                    isHeader = false;
                }
                else{
                    String[] col = line.split(",");
                    Child child = new Child();
                    child.setChildID(col[0].trim());

                    ArrayList<String> childResponses = new ArrayList<>();
                    for(int i = 1; i < col.length; i++){

                        String response = col[i].trim();
                        if(response.equals("#NULL!")){
                            response = "-1";
                        }

                        childResponses.add(response);
                    }

                    child.setChildResponses(childResponses);
                    childList.add(child);
                }

            }
        } catch(IOException e){
            e.printStackTrace();
        }

        //Toast.makeText(Mai.this, "Added dataset!", Toast.LENGTH_LONG).show();

    }

    public void prepareFeatures(String featureCSV, ArrayList<Question> questionList){
        /*
        Loads hard-coded file from the feature button. This function already prepares the Question
        arraylist.
         */
        //Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        //chooseFile.setType("*/*");
        //chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        //startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
        //TODO load files from cloud

        AssetManager manager = this.getAssets();
        InputStream inStream = null;
        int tempNum = 0;
        try{
            inStream = manager.open(featureCSV);
        } catch(IOException e){
            e.printStackTrace();
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
        String line = "";

        boolean isFirst = true;
        try{
            while((line = buffer.readLine()) != null){
                Log.d("lineread", line);
                String[] col = line.split(",");
                Log.d("numberofcolumns", Integer.toString(col.length));

                if(col[0].trim().equals("^")){
                    // new class
                    if(!isFirst){
                        //add to arraylist
                        questionList.add(question);
                        tempNum++;

                    }
                    isFirst = false;
                    question = new Question();
                    question.setQuestionNum(tempNum);
                    question.setQuestionLabel(col[1].trim());
                    question.setQuestionText(col[2].trim());
                    Log.d("questionNum", col[1].trim());

                }
                else{
                    //new features
                    question.addFeatureGroup(col[0].trim());
                    question.addFeatureNum(Integer.parseInt(col[1].trim()));
                    question.addFeatureText(col[2].trim());
                }

            }
            questionList.add(question);
        } catch(IOException e){
            e.printStackTrace();
        }

        //Toast.makeText(MainMenu.this, "Added feature dataset!", Toast.LENGTH_LONG).show();

    }

    private PieChart createPieChart(){

        PieChart pieChart = new PieChart(this);
        // configure pie chart
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(false);

        // enable hole and configure
        //pieChart.setDrawHoleEnabled(true);
        //pieChart.setHoleColor(Color.BLUE);
        //pieChart.setHoleRadius(45);
        //pieChart.setTransparentCircleRadius(100);

        // enable rotation of the chart by touch
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);

        pieChart.setTransparentCircleRadius(50f);

        // set a chart value selected listener
        //pieChart.setOnChartValueSelectedListener(getOnChartValueSelectedListener());

        // customize legends
        Legend l = pieChart.getLegend();
        pieChart.setEntryLabelColor(getResources().getColor(R.color.black));

        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        //l.setTextSize(16);
        l.setWordWrapEnabled(true);
        //l.setTextSize(R.dimen.context_text_size);
        l.setTextColor(Color.BLACK);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);

        return pieChart;
    }

    private void preparePieChartData2(PieChart pieChart, ArrayList<Child> childList, String test){

        List<PieEntry> pieEntries = new ArrayList<>();
        String[] pieLabels = questionList.get(questionNum).getFeatureText().toArray(new String[0]);

        ValueCounter valueCounter = new ValueCounter(childList, questionNum, questionList);



        //valueCounter.setResponse();
        int[] pieCount = valueCounter.getResponse();
        int pieTotal = 0;
        answerCount = valueCounter.getGroup();

        ArrayList<Float> pieValues = computeValue2(childList, test, pieCount, answerCount);

        Log.d("valuesize", Integer.toString(pieValues.size()));
        for(int i = 0; i < pieValues.size(); i++){
            Log.d("Pie Values", pieValues.get(i).toString());

            pieEntries.add(new PieEntry(pieValues.get(i), pieLabels[i] + "(" + pieCount[i] + ")"));
            pieTotal += pieCount[i];

        }

        if(test.equals("left")){
            leftDetails.setText("Female Dataset - N: " + childListLeft.size());
            pieTotalLeft = pieTotal;
        }
        else if(test.equals("right")){
            rightDetails.setText("Male Dataset - N: " + childListRight.size());
            pieTotalRight = pieTotal;
        }

        String label = questionList.get(questionNum).getQuestionLabel();
        ArrayList<Integer> colorList = new ArrayList<>();
        for(int i = 0; i < questionList.get(questionNum).getFeatureNum().size(); i++){
            if(questionList.get(questionNum).getFeatureGroup().get(i).equals("a")){
                Log.d("doesitgoherepls", "test");

                colorList.add(getResources().getColor(R.color.red));

            }
            else if(questionList.get(questionNum).getFeatureGroup().get(i).equals("b")){

                colorList.add(getResources().getColor(R.color.yellow));


            }
            else if(questionList.get(questionNum).getFeatureGroup().get(i).equals("c")){

                colorList.add(getResources().getColor(R.color.green));
            }
            else if(questionList.get(questionNum).getFeatureGroup().get(i).equals("x")){

                colorList.add(getResources().getColor(R.color.blue));
            }
        }
        PieDataSet set = new PieDataSet(pieEntries, "");

        set.setValueLinePart1OffsetPercentage(80.f);
        set.setValueLinePart1Length(0.2f);
        set.setValueLinePart2Length(0.4f);
        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        set.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);


        set.setSliceSpace(4f);
        set.setColors(colorList);

        PieData data = new PieData(set);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(18f);
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);

        Description description = new Description();
        description.setText("n: " + pieTotal);
        description.setTextSize(16.0f);
        description.setPosition(85f, 275f);

        pieChart.setDescription(description);
        pieChart.animateXY(1500, 1500);
        pieChart.invalidate();


    }
    private ArrayList<Float> computeValue2(ArrayList<Child> childList, String test, int[] valueCount, int[] answerCount){
        totalCount = 0;

        if(test.equals("left"))
            classValueLeft = answerCount[0];
        else if(test.equals("right")){
            classValueRight = answerCount[0];
        }
        //Log.d("z-test2", Integer.toString(valueCount[1]));

        ArrayList<Float> computedValues = new ArrayList<>();
        for(int i = 0; i < valueCount.length; i++){
            Log.d("valuecounttest", Integer.toString(valueCount[i]));
            totalCount += valueCount[i];
        }
        for(int i = 0; i < valueCount.length; i++){
            computedValues.add((float) valueCount[i] / totalCount * 10);
        }

        return computedValues;

    }

    private ArrayList computeValue(int[] valueCount){

        int totalCount = 0;

        //int sliceCount = valueCount.length;
        ArrayList<Float> computedValues = new ArrayList<>();
        //more efficient way of doing this?
        for(int i = 0; i < valueCount.length; i++){

            totalCount += valueCount[i];
        }

        for(int i = 0; i < valueCount.length; i++){
            computedValues.add((float)valueCount[i] / totalCount * 10);
        }

        return computedValues;


    }
    private void readFileName(){
        //for getting the filenames of the dataset to be displayed in the program
    }
    private void computeChiStat(){


        Log.d("z-value test", Integer.toString(classValueLeft));
        Log.d("z-valuetest2", Integer.toString(classValueRight));
        double childLeftSize = (double) pieTotalLeft;
        double childRightSize = (double) pieTotalRight;
        double leftDouble = (double) classValueLeft / childLeftSize;
        double rightDouble = (double) classValueRight / childRightSize;
        double confValue = 0.00;

        Log.d("cls", Double.toString(childLeftSize));
        Log.d("crs", Double.toString(childRightSize));
        Log.d("ldtest", Double.toString(leftDouble));
        Log.d("rdtest", Double.toString(rightDouble));

        Log.d("childListsize", "Left: " + Integer.toString(childListLeft.size()) + " and Right: " + Integer.toString(childListRight.size()));
        double pHat = ((childLeftSize * leftDouble) + (childRightSize * rightDouble)) / (childLeftSize + childRightSize);
        double sep = Math.sqrt((pHat * (1 - pHat)) * (1 / childLeftSize + 1 / childRightSize));
        Log.d("phat", Double.toString(pHat));
        Log.d("sep", Double.toString(sep));
        double z = Math.abs((leftDouble - rightDouble) / sep);
        Log.d("z-value", Double.toString(z));
        double z2 = Math.pow(z, 2);
        Log.d("pow", Double.toString(z2));

        //TODO change this to text "within normal bounds", and "out of the ordinary(ooto)" with confidence interval and the z-score at the beginning
        //TODO change color of pie chart
        //TODO place pie chart legends at the bottom and include the number of respondents
        //TODO get name of dataset
        //TODO searching of question
        //TODO filter in main menu
        double zRound = Math.round(z * 100.00) / 100.00;
        Log.d("zround", Double.toString(zRound));
        if(confInterval.equals("99%")){
            confValue = 2.58;
        }

        else if(confInterval.equals("95%")){
            confValue = 1.96;
        }
        //TODO spinner for confidence interval
        if(zRound <= confValue){ // 99% confidence interval
            resultText.setText("Z-score: " + Double.toString(zRound) + " Within Normal Bounds at " + confInterval + " confidence interval");
            resultText.setTextColor(getResources().getColor(R.color.black));
            resultText.setTypeface(null, Typeface.NORMAL);
        }
        else if(zRound > confValue){
            resultText.setText("Z-score: " + Double.toString(zRound) + " Out of the Ordinary(OOTO) at " + confInterval + " confidence interval");
            resultText.setTextColor(getResources().getColor(R.color.red));
            resultText.setTypeface(null, Typeface.BOLD);

        }

        //compute Chi statistic
        //display green/red box

    }
    public void setQuestionText(int questionNum){
        //set questiontext on launch, and use on prevView() and nextView()

    }

    public void findQuestion(String questionCode){
        //TODO fix variable naming
        boolean isFound = false;
        for(int i = 0; i < questionList.size(); i++){
            if(questionList.get(i).getQuestionLabel().equals(questionCode)){
                isFound = true;
                questionNum = i;
                questionDesc.setText(": "+ questionList.get(questionNum).getQuestionText());
                preparePieChartData2(mPieLeft, childListLeft, "left");
                preparePieChartData2(mPieRight, childListRight, "right");
                computeChiStat();
                break;
            }
        }
        if(!isFound){
            Toast.makeText(ChartActivity.this, "Question not found!", Toast.LENGTH_LONG).show();
        }

    }

    public void prevView(View view) {
        questionNum--;
        if(questionNum < 0){
            questionNum = questionList.size() - 1;
        }
        //questionText.setText("Question " + questionList.get(questionNum).getQuestionLabel() + ": " + questionList.get(questionNum).getQuestionText());
        questionCode.setText(questionList.get(questionNum).getQuestionLabel());
        questionDesc.setText(": "+ questionList.get(questionNum).getQuestionText());
         //green color

        preparePieChartData2(mPieLeft, childListLeft, "left");
        preparePieChartData2(mPieRight, childListRight, "right");
        computeChiStat();




    }

    public void nextView(View view){
        questionNum++;
        if(questionNum >= questionList.size()){
            questionNum = 0;
        }
        //questionText.setText("Question " + questionList.get(questionNum).getQuestionLabel() + ": " + questionList.get(questionNum).getQuestionText());
        questionCode.setText(questionList.get(questionNum).getQuestionLabel());
        questionDesc.setText(": "+ questionList.get(questionNum).getQuestionText());


        preparePieChartData2(mPieLeft, childListLeft, "left");
        preparePieChartData2(mPieRight, childListRight, "right");
        computeChiStat();


    }
}

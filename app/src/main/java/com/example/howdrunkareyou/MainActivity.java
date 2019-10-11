package com.example.howdrunkareyou;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import java.time.Duration;
import java.time.LocalTime;

public class MainActivity extends AppCompatActivity {

    private RadioButton pounds,centyliter;
    private Button calculatePerMil;
    private EditText amountAlc,percentage, weight, consumptionHour,consumptionMinute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUIViews();

        calculatePerMil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double howMuchWeight=0,howMuchAlc=0,alcPercentage=0;
                int hour=0,minute=0;
                boolean exception=false;
                try {
                    howMuchAlc = Double.parseDouble(amountAlc.getText().toString());
                    if (centyliter.isChecked()) howMuchAlc = clToMl(howMuchAlc);
                }catch (Exception e){
                    amountAlc.setText("");
                    amountAlc.setHint("Wrong data");
                    exception=true;
                }

                try{
                    howMuchWeight = Double.parseDouble(weight.getText().toString());
                    if (pounds.isChecked()) howMuchWeight = poundsToKg(howMuchWeight);
                }catch(Exception e){
                    weight.setText("");
                    weight.setHint("Wrong data");
                    exception=true;
                }
                try {
                    alcPercentage = Double.parseDouble(percentage.getText().toString());
                }catch(Exception e){
                    percentage.setText("");
                    percentage.setHint("Wrong data");
                    exception=true;
                }
                try {
                    hour = Integer.parseInt(consumptionHour.getText().toString());
                    minute = Integer.parseInt(consumptionMinute.getText().toString());
                }catch(Exception e){
                    consumptionMinute.setText("");
                    consumptionMinute.setHint("Wrong data");
                    consumptionHour.setText("");
                    consumptionHour.setHint("Wrong data");
                    exception=true;
                }

                LocalTime time = LocalTime.of(hour,minute);
                LocalTime timeNow = LocalTime.now();
                double timeBetween = Duration.between(timeNow,time).toMinutes();
                if(timeBetween>0) {
                    consumptionHour.setText("");
                    consumptionHour.setHint("Wrong data");
                    consumptionMinute.setText("");
                    consumptionMinute.setHint("Wrong data");
                    exception=true;
                }
                else timeBetween=Math.abs(timeBetween);

                double alc=perMilCalculator(howMuchWeight,howMuchAlc,alcPercentage,timeBetween);
                if(alc<0)alc=0;

                String alcohol=String.format( "%.2f", alc );
                LocalTime sober=LocalTime.of(23,59);
                if(!exception) {
                    try {
                        sober = soberUpTime(alc);
                        callDialog(alcohol, sober);
                    } catch (Exception e) {
                        callDialog(alcohol, sober, true);
                    }
                }
            }
        });

    }

    private LocalTime soberUpTime(double alcohol){
        double alc = alcohol/0.15;

        int hour =(int)alc;
        int min = (int) (alc * 60) % 60;

        LocalTime localTime=LocalTime.of(hour,min);
        return localTime;
    }

    private double perMilCalculator(double weight, double alc, double percentage, double time){

        double alcWeight= (percentage/100)*alc*0.79;

        double perMil= alcWeight/(0.65*weight);

        perMil=perMil-(time/60)*0.15;

        return perMil;
    }

    private void callDialog(String alcohol,LocalTime hourMin){
        new AlertDialog.Builder(this)
                .setTitle("This is how fucked up you are at the moment ")
                .setMessage("Per mil in your body is "+alcohol+"\n\nTo sober up you need "+ hourMin.getHour()+" h and "+hourMin.getMinute()+" min")
                .setNegativeButton("Ok",null).create().show();
    }
    private void callDialog(String alcohol,LocalTime hourMin,boolean check){
        new AlertDialog.Builder(this)
                .setTitle("This is how fucked up you are at the moment ")
                .setMessage("Per mil in your body is "+alcohol+"\n\nTo sober up you need over "+ hourMin.getHour()+" h and "+hourMin.getMinute()+" min so stop drinking!\n\n" +
                        "This is not a joke I wonder you are able to read this")
                .setNegativeButton("Fuck off I wanna have fun", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callDialog(true);
                    }
                })
                .setPositiveButton("Ok i will stop",null).create().show();

    }
    private void callDialog(boolean check) {
        new AlertDialog.Builder(this)
                .setTitle("This is not a joke dude ")
                .setMessage("Stop drinking or I will call an ambulance from your phone!")
                .setNegativeButton("No I will never stop!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callDialog(true);
                    }
                })
                .setPositiveButton("Ok I will stop!", null).create().show();

    }

    private double poundsToKg(double pounds){
       pounds*=0.45359237;
       return pounds;
    }
    private double clToMl(double cl){
        cl*= 10;
        return cl;
    }

    private void setupUIViews(){
        pounds = (RadioButton) findViewById(R.id.pounds);
        centyliter = (RadioButton) findViewById(R.id.centyliter);

        calculatePerMil = (Button) findViewById(R.id.calculatePerMil);

        amountAlc = (EditText) findViewById(R.id.amountAlc);
        percentage = (EditText) findViewById(R.id.percentage);
        weight = (EditText) findViewById(R.id.weight);
        consumptionHour = findViewById(R.id.consumptionHour);
        consumptionMinute = findViewById(R.id.consumptionMinute);

    }
}

package com.bvm.ui_example_4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Quiz extends AppCompatActivity {
    private static TextView textViewQuestion, textViewScore, textViewQuestiononCount, countdownTimerText;
    private static RadioButton rb1, rb2, rb3;
    private static int questionCounter = 0, questionCountTotal , score = 0, var = 0, i =1;
    private static boolean answered;
    private static CountDownTimer countDownTimer;
    private static RadioGroup rbGroup;
    private static Button buttonConfirmNext;
    private ColorStateList textColorDefaultRb;
    int noOfMinutes = 30;

   private static String[] Questions = new String[10];
    String[] option1 = new String[10];
    String[] option2 = new String[10] ;
    String[] option3 = new String[10];
    Integer[] correctanswer = new Integer[100];
    private List<Question> questionList;
    String j ;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        /******Initialization****/

        textViewQuestion = findViewById(R.id.text_view_question);

        textViewScore = findViewById(R.id.text_view_score);
        countdownTimerText = findViewById(R.id.text_view_countdonw);
        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);
        buttonConfirmNext = findViewById(R.id.button);
        textViewQuestiononCount = findViewById(R.id.text_view_question_count);
        textColorDefaultRb = rb1.getTextColors();


        getQuestion();

        buttonConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!answered){
                    if(rb1.isChecked() | rb2.isChecked() |rb3.isChecked()){
                        stopCountdown();
                        checkAnswer();

                    }else{
                        Toast.makeText(Quiz.this, "Select one option", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    var++;
                    ShowNextQuestion();
                    Toast.makeText(Quiz.this, "Showing next question", Toast.LENGTH_SHORT).show();
                }



            }

            private void ShowNextQuestion() {
                stopCountdown();
                noOfMinutes = 30 * 1000;
                startTimer(noOfMinutes);

                rb1.setTextColor(textColorDefaultRb);
                rb2.setTextColor(textColorDefaultRb);
                rb3.setTextColor(textColorDefaultRb);
                rbGroup.clearCheck();

                if (var < questionCountTotal) {

                    textViewQuestion.setText(Questions[var]);
                    rb1.setText(option1[var]);
                    rb2.setText(option2[var]);
                    rb3.setText(option3[var]);
                    textViewQuestiononCount.setText(var + " / " + questionCountTotal);
                    answered = false;
                }
                else{
                    Toast.makeText(Quiz.this, "You are done with the quiz", Toast.LENGTH_SHORT).show();

                }
            }


        });
    }

    private void getQuestion() {
        DatabaseReference myref  = FirebaseDatabase.getInstance().getReference();
        myref.addValueEventListener(new ValueEventListener() {
            int Answer = 0;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String Total = dataSnapshot.child("Subject").child("question").getValue().toString();
                int value = Integer.parseInt(Total);
                questionCountTotal = value;

                for(int k=0;k <value; k++) {
                    i = k+1;

                    j = ("Question " + Integer.toString(i));


                    String Question = dataSnapshot.child(j).child("Que").getValue().toString();
                    String option1s = dataSnapshot.child(j).child("option 1").getValue().toString();
                    String option2s = dataSnapshot.child(j).child("option 2").getValue().toString();
                    String option3s = dataSnapshot.child(j).child("option 3").getValue().toString();
                    String correctAnswer = dataSnapshot.child(j).child("ans").getValue().toString();
                    Answer = Integer.parseInt(correctAnswer);
                    Questions[k] = Question;
                    option1[k] = option1s;
                    option2[k] = option2s;
                    option3[k] = option3s;
                    correctanswer[k] = (Integer.parseInt(correctAnswer));
                }

                FirstQuestion();


            }

            private void FirstQuestion() {

                noOfMinutes = 30 * 1000;
                startTimer(noOfMinutes);

                textViewQuestion.setText(Questions[var]);
                rb1.setText(option1[var]);
                rb2.setText(option2[var]);
                rb3.setText(option3[var]);
                //ButtonFunc();
            }






            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void checkAnswer() {
        answered = true;
        RadioButton  rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        int answerNr = rbGroup.indexOfChild(rbSelected) + 1;

        if(answerNr == correctanswer[var]){
            score++;
            textViewScore.setText("Score: " + score);
            Toast.makeText(Quiz.this, "Your answer is correct", Toast.LENGTH_SHORT).show();
        }
        ShowSolution();

    }

    private void ShowSolution() {
        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);
        switch (correctanswer[var]){
            case 1:
                rb1.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 1 is correct");
                break;
            case 2:
                rb2.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 2 is correct");
                break;
            case 3:
                rb3.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 3 is correct");
                break;
        }
        if (questionCounter < questionCountTotal) {
            buttonConfirmNext.setText("NEXT");
        }else{
            buttonConfirmNext.setText("FINISH");
        }
    }

    private void stopCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    //Start Countodwn method
    private void startTimer(int noOfMinutes) {
        countDownTimer = new CountDownTimer(noOfMinutes, 1000) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                //Convert milliseconds into hour,minute and seconds
                String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                countdownTimerText.setText(hms);//set text
            }

            public void onFinish() {

                countdownTimerText.setText("TIME'S UP!!"); //On finish change timer text
                countDownTimer = null;//set CountDownTimer to null
                checkAnswer();
            }
        }.start();

    }


}

/*
String Question = dataSnapshot.child("Question " + Integer.toString(i + 1)).child("Que").getValue().toString();
                   String option1s = dataSnapshot.child("Question "+ Integer.toString(i + 1)).child("option 1").getValue().toString();
                   String option2s = dataSnapshot.child("Question "+ Integer.toString(i + 1)).child("option 2").getValue().toString();
                   String option3s = dataSnapshot.child("Question "+ Integer.toString(i + 1)).child("option 3").getValue().toString();
                   String correctAnswer = dataSnapshot.child("Question " + Integer.toString(i + 1)).child("ans").getValue().toString();
                   int Answer = Integer.parseInt(correctAnswer);
                   Questions[i] = Question;
                   option1[i] = option1s;
                   option2[i] = option2s;
                   option3[i] =option3s;
                   correctanswer[i] = Answer;
 */

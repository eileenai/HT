package com.example.ht;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ansActivity extends AppCompatActivity {

    //(Function) 抓出要回覆的問題id
    String proid = "1";
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference proRef = FirebaseDatabase.getInstance().getReference("problem");
    private DatabaseReference proDeRef = proRef.child(proid);
    private DatabaseReference repRef =FirebaseDatabase.getInstance().getReference("reply");
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user");

    public static final String REPID_KEY ="id_reply";
    public static final String PROID_KEY ="id_problem";
    public static final String CONTENT_KEY ="content_reply";
    public static final String TEAID_KEY ="id_tea";
    public static final String TIME_KEY ="time_reply";
    public static final String FROM_KEY ="from";
    public static final String REPORT_KEY ="been_reported_reply";
    public static final String INAPPRO_KEY ="inappropriate_content_reply";
    public static final String TAG ="ReplyingQuestion";
    String userId = "";




    TextView hottea,questitle,quescontent;

    ImageButton backimagebutton,sendimagebutton,menubutton;

    LinearLayout linearLayout;
    EditText anstext;
    //髒話列表(要更新兩邊都要更新) ----->未來用Function做同步
    ArrayList<String> a = new ArrayList(Arrays.asList("幹","靠","機掰","你娘","屎","乳頭","雞雞","雞掰","雞巴","雞八","王八","哭邀","哭腰","怪胎","腦殘","白癡","北七","媽的","低能","智障","屁眼","陰道","陰莖","去死","腦殘","喜憨"));

    //String[] name = new String[50];
    int ranPick = 0;
    String proKey = "";

    Button reject;

    //int num = 50;
    int num;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ans);

        hottea = (TextView)findViewById(R.id.hottea);
        questitle = (TextView)findViewById(R.id.questitle);
        quescontent = (TextView)findViewById(R.id.quescontent);

        menubutton =  findViewById(R.id.menubutton);

        backimagebutton = (ImageButton)findViewById(R.id.backimageButton);
        sendimagebutton = (ImageButton)findViewById(R.id.sendimageButton);
        anstext = findViewById(R.id.anstext);
        reject = findViewById(R.id.reject);
        reject.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                userRef.child(userId).child("waitreply").child(proKey).setValue("-1");

                new AlertDialog.Builder(ansActivity.this)
                        .setIcon(R.drawable.logo)
                        .setTitle("我們收到了！謝謝你撥空閱讀這則提問。")
                        .setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendgotohome();
                            }
                        }).create()
                        .show();
            }
        });

        new AlertDialog.Builder(ansActivity.this)
                .setIcon(R.drawable.logo)
                .setTitle("貼心小提醒")
                .setMessage("在大家熱心回答前~要請大家注意一下用詞內容!\n" +
                        "像是避免使用\n" +
                        "·罵人自找 等等明顯攻擊性話語\n" +
                        "·激問句帶來的貶低和否定等等帶有隱含意思的語句\n" +
                        "·強人哲學：你就要努力等等 （批評你不夠努力⋯⋯)\n" +
                        "·歧視同志、跨性別等等觀點\n" +
                        "真誠地回應才能溫暖並幫助需要的人!!\n")
                .setNegativeButton("我知道了",null).create()
                .show();

        Intent it = getIntent();
        userId = it.getStringExtra("UserId");
        //Toast.makeText(this, "Here is userId:"+userId, Toast.LENGTH_SHORT).show();
        System.out.println("Here is userID(ansActivity):"+userId);


        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                      int i = 0;


                      //抓problem總數
                      /*
                      num = Integer.parseInt(String.valueOf(dataSnapshot.child("count_problem").getValue()));
                      System.out.println("This is num:" + dataSnapshot.child("count_problem").getValue());
                      */
                      num = Integer.parseInt(String.valueOf(dataSnapshot.child("user").child(userId).child("waitreply").getChildrenCount()) );
                      System.out.println("this user has "+num+" problem waitreply");
                      String[] name = new String[num];

                      //把所有problem Key抓進來
                     /*
                      for (DataSnapshot d : dataSnapshot.child("problem").getChildren()) {
                          name[i] = d.getKey();
                          i++;
                      }
                      */
                     if(num != 0) {
                         for (DataSnapshot d : dataSnapshot.child("user").child(userId).child("waitreply").getChildren()) {
                             if(d.getValue().toString().equals("-1")){
                                 System.out.println("no get -1");
                                 num--;
                             }
                             else{
                                 name[i] = d.getKey();
                                 i++;
                             }

                         }
                         //Test
                        /*for(int j=0;j<num;j++) {
                            System.out.println("This is all key" + name[j]);
                        }*/


                         //10可以改成抓線上problem的數量count
                         ranPick = (int) (Math.random() * num);


                         // ranPick = (int)(Math.random()* Double.parseDouble(userCount));

                         //System.out.println("This is ranPick:"+ranPick);
                         proKey = name[ranPick];

                         System.out.println(dataSnapshot.child("problem").child(proKey).child("from").getValue());

                         //System.out.println("This is content:"+content);


                         String title = dataSnapshot.child("problem").child(proKey).child("title_problem").getValue(String.class);
                         String content = dataSnapshot.child("problem").child(proKey).child("content_problem").getValue(String.class);

                         questitle.setText(title);
                         quescontent.setText(content);
                     }
                     else{
                         questitle.setText("");
                         quescontent.setText("暫無問題");
                         quescontent.setTextSize(32);
                     }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void gotohome(View v) {
        //Intent it = new Intent(this, mainPageActivity.class );
        //it.putExtra("UserId", userId);
        //startActivity(it);
        finish();
    }
    public void sendgotohome(){finish();}
    /*public void gotonotice(View v) {
        Intent it = new Intent(this, noticeActivity.class);
        it.putExtra("UserId", userId);

        startActivity(it);
    }*/

    public void gotomenu(View v) {
        Intent it = new Intent(this, menuActivity.class);
        it.putExtra("UserId", userId);
        startActivity(it);
    }

    public void returnAns(View v){

        //Time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date sendTime = new Date(System.currentTimeMillis());
        String repTime = formatter.format(sendTime);

        //Been Reported (Function)
        Boolean reported = false;

        //Inappropriate Content (Function)
        Boolean inappro = false;

        // TeaID (Function)
        String teaid = "001";

        //隨機產生id_reply
        repRef.push();


        String repid = repRef.push().getKey();

        //ansText
        String  ans = "";
        ans = anstext.getText().toString();


        //是否有含髒話
        boolean contain = false;
        //是否有欄位為空
        if (ans.isEmpty() ){
            Toast.makeText(this, "標題或內容有缺漏，請再檢查一次", Toast.LENGTH_SHORT).show();
        }
        for (int i=0;i<a.size();i++) {
            //-1是沒有髒話
            if (ans.indexOf(a.get(i)) != -1 ) {
                Toast.makeText(this, "標題或內容有髒話，請再檢查一次", Toast.LENGTH_SHORT).show();
                contain = true;
                System.out.println("Here is the bad:"+ans);
            }
        }

        //都符合，才寫入db
        if(contain == false) {
            Map<String, Object> dataToSave = new HashMap<String, Object>();

            dataToSave.put(CONTENT_KEY, ans);
            // dataToSave.put(PROID_KEY, proid);
            dataToSave.put(TIME_KEY, ServerValue.TIMESTAMP);
            dataToSave.put(FROM_KEY, userId);
            dataToSave.put(REPORT_KEY, reported);
            dataToSave.put(INAPPRO_KEY, inappro);
            dataToSave.put(TEAID_KEY, teaid);
            dataToSave.put(PROID_KEY,proKey);

            repRef.child(repid).setValue(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Document has been saved!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Document was not saved!", e);
                }
            });
            if(userId.isEmpty() == false) {
                userRef.child(userId).child("reply").child(repid).setValue(ans);

                proRef.child(proKey).child("id_reply").child(repid).setValue("1");

            }
            anstext.setText("");
            //testing
            System.out.println(ans);

            sendgotohome();


        }
    }
}
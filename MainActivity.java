package cha.com.autodetectsms;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> language=new ArrayList<>();
    TextView textView;
    EditText editText;
    Button dot,dash;
    Handler handler;
    int reqCode=0;
    ArrayList<String> contactNames; //Arraylist to save all the contact Names
    ArrayList<ContactDTO> contacts=new ArrayList<>(); //ArrayList To hold all contacts from the phone.
    private  String[] alpha;
    private String[] dottie;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ){ //Back button
            String text=textView.getText().toString();
            String finaltext=null;
            if(text.length()>0)
                finaltext=text.substring(0,text.length()-1);
            textView.setText(finaltext);
            editText.setText("");
            return true;
        }
        else if( keyCode == KeyEvent.KEYCODE_VOLUME_UP) { //add Space
            String text=textView.getText().toString();
            textView.setText(text+" ");
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
    public  void getContactsReady(){
        contacts=GetContactList.getNumber(MainActivity.this.getContentResolver()); //Fetch the contacts from Contact List
        contactNames=new ArrayList<>();

        /*Iterate the contact list and save all the names to a arraylist which will be used to search the contact name entered by the user */
        for(ContactDTO contactDTO:contacts){
            if(contactDTO.name!=null)
                contactNames.add(contactDTO.name.toLowerCase());
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ValueDTO valueDTO=new ValueDTO();
        new DBHelper(this).initialiseValues();
        valueDTO=new DBHelper(this).getValuesFromDataBase();

        ArrayList<String> alphaList=new ArrayList<>();
        alphaList = new Gson().fromJson(valueDTO.alpha, new TypeToken<ArrayList<String>>() {
        }.getType());
        alpha=new String[alphaList.size()];
        alphaList.toArray(alpha);


        ArrayList<String> dottieList=new ArrayList<>();
        dottieList = new Gson().fromJson(valueDTO.dottie, new TypeToken<ArrayList<String>>() {
        }.getType());
        dottie=new String[dottieList.size()];
        dottieList.toArray(dottie);



        Log.i("Obj",valueDTO.toString());
        //Load The Views
        startService(new Intent(this, ShakeDetectingService.class));
        editText=(EditText)findViewById(R.id.enteredVal);
        textView=(TextView)findViewById(R.id.completeVal);
        language= new ArrayList<String>(Arrays.asList(dottie));
        checkPermissions(editText);
        // getContactsReady();


        handler =new Handler();

        //When DOT button is pressed ad "." to the editText
        ((Button)findViewById(R.id.dot)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String initialText=editText.getText().toString();

                if(initialText.length()>0){
                    initialText =initialText+".";
                    editText.setText(initialText);
                }else{editText.setText(".");}

            }
        });

//When DASH button is pressed ad "." to the editText

        ((Button)findViewById(R.id.dash)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String initialText=editText.getText().toString();

                if(initialText.length()>0){
                    initialText =initialText+"-";
                    editText.setText(initialText);
                }else{editText.setText("-");}

            }
        });




        //Add Space
        ((Button)findViewById(R.id.space)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=textView.getText().toString();
                textView.setText(text+" ");

            }
        });

//Remove last char
        ((Button)findViewById(R.id.back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=textView.getText().toString();
                String finaltext=null;
                if(text.length()>0)
                    finaltext=text.substring(0,text.length()-1);
                textView.setText(finaltext);
                editText.setText("");




            }
        });


        // maps the morse input to aphabets/numbers
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String contactName;
                String textVal=textView.getText().toString();
                textVal=textVal.toLowerCase();
                if((textVal.startsWith("call "))&&(textVal.length()>5)){
                    contactName=textVal.substring(5);
                    Log.i("ContactName",contactName);
                    Log.i("Check Existence",contactNames.contains(contactName)+"");
                    if(contactNames.contains(contactName)){

                        String numberTOBeCalled= contacts.get(contactNames.indexOf(contactName)).number;
                        Log.i("NUmber to be called",numberTOBeCalled);
                        try {
                            Intent my_callIntent = new Intent(Intent.ACTION_CALL);
                            my_callIntent.setData(Uri.parse("tel:" + numberTOBeCalled));
                            MainActivity.this.startActivity(my_callIntent);

                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Error in your phone call" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                }
            }
        });



        //The Alpha char .Listens for CALL XYZ.When receives call xyz, Proceeds to the calling xyz
        editText.addTextChangedListener(new TextWatcher() {
            String beforeText,afterText;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeText=editText.getText().toString();
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                //  if(handler!=null){
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("Before Text",beforeText);
                        final String val=editText.getText().toString();
                        Log.i("After Text",val);
                        Handler handler1=new Handler();
                        handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                afterText =editText.getText().toString();
                                if(afterText.equals(val)){
                                    boolean isContained=language.contains(val);
                                    if(isContained) {
                                        String charIs = alpha[language.indexOf(val)];
                                        String textViewText = textView.getText().toString();
                                        if (textViewText.length() > 0) {
                                            textViewText = textViewText + charIs;
                                            textView.setText(textViewText);
                                        } else {
                                            textView.setText(charIs);
                                        }
                                        editText.setText("");
                                    }
                                }else{
                                    return;
                                }

                            }
                        },500);


                    }
                },200);
                //   }


            }
        });

    }

    //To make it marshmallow compatible.
    public void checkPermissions(View view) {
        // Check if the  permission is already available.
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) &&(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED)&&(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED)){

            requestPermission();

        }else{
            getContactsReady();
        }
    }


    public void requestPermission(){
        {
            Snackbar.make(findViewById(R.id.dot), "Grant Permission To use App",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.WRITE_CONTACTS,
                                            Manifest.permission.CALL_PHONE,
                                            Manifest.permission.READ_CONTACTS},
                                    reqCode);
                        }
                    })
                    .show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        Log.i("ReqCode",String.valueOf(requestCode));
        if (requestCode == reqCode
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getContactsReady();
        }else{
            Snackbar.make(findViewById(R.id.dot), "Grant Permission To use App",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.WRITE_CONTACTS,
                                            Manifest.permission.CALL_PHONE,
                                            Manifest.permission.READ_CONTACTS},
                                    reqCode);
                        }
                    })
                    .show();
        }
    }
}

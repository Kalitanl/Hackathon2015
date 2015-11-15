package com.example.kalit_000.practicetexttwist;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    String[] gameWords={"tray","carrot","knife"}; //different sets of letters
    ArrayList<String> possibleWords = new ArrayList(); //Holds all of the possible words to make from letters
    ArrayList<String> listOfLetters = new ArrayList();//Holds the letters for the round
    ArrayList<String> correctGuesses = new ArrayList();//Holds guesses that were correct. Currently not used in this version
    ArrayList<String> words;
    private Context myContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        try {
            initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }



        final Button scrambleButton = (Button)findViewById(R.id.scrambleButton);
        final Button enterButton = (Button)findViewById(R.id.enterButton);
        final TextView displayForLetters = (TextView)findViewById(R.id.displayForLetters);
        displayForLetters.setText(printableArray(listOfLetters));
        final EditText userGuess = (EditText)findViewById(R.id.editText);
        final TextView endGameScreen = (TextView)findViewById(R.id.textView2);
        final TextView wordsLeft = (TextView)findViewById(R.id.wordsLeft);
        wordsLeft.setText("There are "+possibleWords.toArray().length+" words left!");

        //Rearranges given displayed letters when clicked
        scrambleButton.setOnClickListener(new Button.OnClickListener(){
               public void onClick(View v) {
                   Collections.shuffle(listOfLetters);
                   displayForLetters.setText(printableArray(listOfLetters));
               }
        });

        //Checks if user guess is correct and gives feedback accordingly
        enterButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                String inputedGuess = userGuess.getText().toString();
                guess(inputedGuess);
                userGuess.setText(null);
                possibleWords.remove(inputedGuess);
                wordsLeft.setText("There are " + possibleWords.toArray().length + " words left!");
                if(possibleWords.isEmpty())
                    endGameScreen.setVisibility(View.VISIBLE);
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void guess(String userGuess){
        final TextView correctOrIncorrectMessage =
                (TextView)findViewById(R.id.correctOrIncorrectView);
        if(possibleWords.contains(userGuess)){
            correctGuesses.add(userGuess);
            possibleWords.remove(userGuess);
            correctOrIncorrectMessage.setText("Correct guess of "+userGuess);
        }
        else{
            correctOrIncorrectMessage.setText("Incorrect guess of "+userGuess);
        }
    }

    public void initialize() throws IOException {
        //Selects one of the words from array gameWords to be our set of letters for the rounds
        int randomIndex= (int)(Math.random()*gameWords.length);
        for(int i=0; i<gameWords[randomIndex].length();i++){
            listOfLetters.add(gameWords[randomIndex].charAt(i)+"");
        }
        Collections.shuffle(listOfLetters);//Shuffle immediately so not to give an answer

        //Encountered extensive problems taking Strings from a text file in Android
        //Only solution I could find.
        words = new ArrayList<String>();
        myContext=this;
        AssetManager am = myContext.getAssets();
        try{
            InputStream is = am.open("wordlist.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            int count = 69903;//.txt file length
            while(count>0){
                words.add(reader.readLine());
                count--;
            }

            is.close();
            reader.close();
        } catch(Exception e){
            e.printStackTrace();
        }

        possibleWords = findAllRealWords(listOfLetters, words);
    }
    //Finds words from .txt file that can be made with the given set of letters
    public ArrayList<String> findAllRealWords(ArrayList<String> letters, ArrayList<String> words){
        ArrayList<String> realWords = new ArrayList<String>();
        for(int i=0; i<words.toArray().length;i++){
            if(doesContain(letters, words.get(i))&&words.get(i).length()>2){
                realWords.add(words.get(i));
            }
        }
        return realWords;
    }
    //Helper method for findAllRealWords that checks one word from the .txt list to
    //see if it can be made with the given letters for the round.
    public boolean doesContain(ArrayList<String> letters,String word){
        ArrayList<String> tempLetters = new ArrayList<String>(letters);
        for(int j=0; j<word.length();j++){
            if(tempLetters.contains(word.substring(j,j+1))){//Cannot use charAt because we need to remove string
                tempLetters.remove(word.substring(j,j+1));
            }
            else{

                return false;
            }
        }
        return true;
    }
    //Method that prints out an ArrayList of Strings without "[]"
    public static String printableArray(ArrayList<String> arrayList){
        String result="";
        for(int i=0; i<arrayList.toArray().length;i++){
            result+=arrayList.get(i)+" ";
        }
        return result;
    }
}

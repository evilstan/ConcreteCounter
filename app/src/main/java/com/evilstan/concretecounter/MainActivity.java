package com.evilstan.concretecounter;

import android.widget.AdapterView.OnItemClickListener;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
        final ArrayList<String> constructionArray = new ArrayList<>();
    final ArrayList<Double> volumeList = new ArrayList<>();

    EditText lengthEdit, heightEdit, widthEdit, numberEdit;
    TextView resultText;
    ListView constructionListView;
    ArrayAdapter<String> adapter;

    Toast toast;
    DecimalFormat df = new DecimalFormat("####.###");

    double totalVolume = 0;
    double length, width, height;
    int numberOfConstructions;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Розрахунок бетону");

/*        Toast toast = Toast.makeText(MainActivity.this, "Введіть кількість конструкцій, та розміри в міліметрах", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();*/

        init();
    }


    public void count() {
        getNumbers();

        double volume = length * width * height * numberOfConstructions;
        volumeList.add(0, volume);

        //@SuppressLint("DefaultLocale") String constructionText = String.format("1. Конструкція %sx%sx%s м, %dшт, V=%s м\u00b3", df.format(length), df.format(width), df.format(height), numberOfConstructions, df.format(volume));
        @SuppressLint("DefaultLocale") String constructionText = String.format("1. %s x %s x %s мм, %dшт, V=%s м\u00b3", df.format(length*1000), df.format(width*1000), df.format(height*1000), numberOfConstructions, df.format(volume));
        constructionArray.add(0, constructionText);
        rearrangeArray();
        viewResult();
        if (volume == 100000000) easterEgg();
    }

    public void getNumbers() throws NumberFormatException {
        length = Double.parseDouble(lengthEdit.getText().toString())/1000;
        width = Double.parseDouble(widthEdit.getText().toString())/1000;
        height = Double.parseDouble(heightEdit.getText().toString())/1000;
        numberOfConstructions = Integer.parseInt(numberEdit.getText().toString());
    }


    public void viewResult() {
        String buf = "Загальний об'єм " + df.format(totalVolume) + "м\u00b3";
        resultText.setText(buf);
    }


    public void rearrangeArray() { //rearrange listView items indexes
        double temp = 0;
        for (Double volume : volumeList) {
            temp += volume;
        }
        totalVolume = temp;

        for (int i = 0; i < constructionArray.size(); i++) {
            String buf = constructionArray.get(i).substring(3);
            int index = constructionArray.size() - i;
            constructionArray.set(i, index + ". " + buf);
            adapter.notifyDataSetChanged();
        }
    }


    public void init() {

        resultText = findViewById(R.id.result_Text);

        lengthEdit = findViewById(R.id.length_edit);
        widthEdit = findViewById(R.id.width_edit);
        heightEdit = findViewById(R.id.height_edit);
        numberEdit = findViewById(R.id.number_edit);
        lengthEdit.requestFocus();

        lengthEdit.setFilters(new InputFilter[]{new InputFilterMinMax(0.0, 100000.0)});
        widthEdit.setFilters(new InputFilter[]{new InputFilterMinMax(0.0, 100000.0)});
        heightEdit.setFilters(new InputFilter[]{new InputFilterMinMax(0.0, 100000.0)});
        numberEdit.setFilters(new InputFilter[]{new InputFilterMinMax(0.0, 100.0)});

        constructionListView = findViewById(R.id.construction_list);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, constructionArray);
        constructionListView.setAdapter(adapter);

        resultText.setText("Введіть кількість конструкцій, та розміри в міліметрах");

        initListeners();
    }


    public void initListeners() {

        constructionListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showMessage("Довге натиснення для видалення");
            }
        });

        resultText.setOnLongClickListener(new View.OnLongClickListener() { //clear all on TextView  Long Click
            @Override
            public boolean onLongClick(View v) {
                constructionArray.clear();
                adapter.notifyDataSetChanged();
                resultText.setText("");
                return false;
            }
        });

        numberEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() { //set value 1 when gets focus
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    numberEdit.setText("1");
                    numberEdit.selectAll();
                }
            }
        });

        constructionListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { //remove item from ListView on Long Click
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                constructionArray.remove(position);
                adapter.notifyDataSetChanged();
                volumeList.remove(position);

                rearrangeArray();
                if (totalVolume == 0) resultText.setText("");
                else viewResult();
                return false;
            }
        });

        numberEdit.setOnEditorActionListener(new EditText.OnEditorActionListener() { //check numbers and run calculation on Enter press
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!lengthEdit.getText().toString().matches("") && !widthEdit.getText().toString().matches("") && !heightEdit.getText().toString().matches("") && !numberEdit.getText().toString().matches("")) {
                        count();
                        lengthEdit.requestFocus();
                    } else if (lengthEdit.getText().toString().matches("")) {
                        lengthEdit.requestFocus();
                        Toast.makeText(MainActivity.this, "Введіть довжину конструкції", Toast.LENGTH_SHORT).show();
                    } else if (widthEdit.getText().toString().matches("")) {
                        widthEdit.requestFocus();
                        Toast.makeText(MainActivity.this, "Введіть ширину конструкції", Toast.LENGTH_SHORT).show();
                    } else if (heightEdit.getText().toString().matches("")) {
                        heightEdit.requestFocus();
                        Toast.makeText(MainActivity.this, "Введіть висоту конструкції", Toast.LENGTH_SHORT).show();
                    } else if (numberEdit.getText().toString().matches("")) {
                        numberEdit.requestFocus();
                        Toast.makeText(MainActivity.this, "Введіть кількість конструкцій", Toast.LENGTH_SHORT).show();
                    }

                    return true;
                }
                return false;
            }
        });
    }

    public void easterEgg() {
        resultText.setText("Даний об'єм перевищує потужності всіх заводів групи Ковальська");
        setTitle("Happy Easter Day");

        Toast toast = Toast.makeText(MainActivity.this, "Ви зробили помилку. Служба безпеки вже виїхала за вами", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        constructionArray.clear();
        constructionArray.add("");
        constructionArray.add("");
        constructionArray.add("");
        constructionArray.add("");
        constructionArray.add("");
        constructionArray.add("");
        constructionArray.add("");
        constructionArray.add("Author: Oleksandr Putsenko");
        constructionArray.add("Crasy Man Interactive");
        constructionArray.add("2021");
        constructionArray.add("All rights reserved");
        adapter.notifyDataSetChanged();

        InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(MainActivity.this.INPUT_METHOD_SERVICE);
        View view = MainActivity.this.getCurrentFocus();
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        heightEdit.setVisibility(View.INVISIBLE);
        lengthEdit.setVisibility(View.INVISIBLE);
        widthEdit.setVisibility(View.INVISIBLE);
        numberEdit.setVisibility(View.INVISIBLE);
    }

    private void showMessage(String message) {

        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

}
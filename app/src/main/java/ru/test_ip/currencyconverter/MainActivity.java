package ru.test_ip.currencyconverter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.util.TypedValue;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;



class ValutesAdapter extends BaseAdapter implements SpinnerAdapter {
    Context context;

    ValutesAdapter(Context context) {
        this.context = context;
        elements = new ArrayList<Valute_element>();
    }

    static class Valute_element{
        public long numCode;
        public String charCode;
        public String name;

        Valute_element(Valute val){
            this.numCode = val.numCode;
            this.charCode = val.charCode;
            this.name = val.name;
        }
        public String toString(){
            return charCode + " - " + name;
        }
    }
    private ArrayList<Valute_element> elements;

    public void fillData(List<Valute> valutes){
        elements.clear();
        for (Valute v : valutes) {
            elements.add( new Valute_element(v));
        }
    }

    @Override
    public int getCount() {
        return elements.size();
    }

    @Override
    public String getItem(int pos) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public long getItemId(int pos) {
        return elements.get(pos).numCode;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = new TextView(context);
            ((TextView)view).setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        }
        ((TextView) view).setText(elements.get(i).charCode);
        return view;
    }

    @Override
    public View getDropDownView(int i, View view,
                                ViewGroup parent) {
        if(view == null){
            view = new TextView(context);
            ((TextView)view).setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        }
        ((TextView) view).setText(elements.get(i).toString());
        return view;
    }
}

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Converter valuteConverter;

    Spinner in_valute,out_valute;
    EditText in_sum;
    TextView not_actual,out_sum;
    Button but_convert;


    class UpdateExchangeRateReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            MainActivity.this.fillData();
        }
    }
    UpdateExchangeRateReceiver uerr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        valuteConverter = Converter.getInstance(this.getApplication());

        in_valute = (Spinner) findViewById(R.id.spinner_inValute);
        out_valute = (Spinner) findViewById(R.id.spinner_outValute);
        in_sum = (EditText) findViewById(R.id.editText_In);
        not_actual = (TextView) findViewById(R.id.textView_notActual);
        out_sum = (TextView) findViewById(R.id.textView_out);

        but_convert = (Button) findViewById(R.id.buttonOk);
        but_convert.setOnClickListener(this);

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        uerr = new UpdateExchangeRateReceiver();
        lbm.registerReceiver(uerr,new IntentFilter(getApplicationContext().getString(R.string.actionUpdateExchangeRate)));

        if( valuteConverter.isExistsExchangeRate()) {
            fillData();
        }
        else {
            Toast.makeText(this,R.string.errorExistExchangeRate,Toast.LENGTH_SHORT).show();
        }

    }

    private void fillData(){
        ValutesAdapter adapter1 = new ValutesAdapter(this),
                       adapter2 = new ValutesAdapter(this);
        adapter1.fillData(valuteConverter.getListValutes());
        adapter2.fillData(valuteConverter.getListValutes());

        int prev_pos;
        prev_pos = in_valute.getSelectedItemPosition();
        in_valute.setAdapter(adapter1);
        if(prev_pos != AdapterView.INVALID_POSITION && in_valute.getCount() > prev_pos) in_valute.setSelection(prev_pos);

        prev_pos = out_valute.getSelectedItemPosition();
        out_valute.setAdapter(adapter2);
        if(prev_pos != AdapterView.INVALID_POSITION && out_valute.getCount() > prev_pos) out_valute.setSelection(prev_pos);

        but_convert.setEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        valuteConverter.updateExchangeRate();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(uerr);
        super.onDestroy();
    }


    @Override
    public void onClick(View view) {
        float sum;
        try{
            sum = Float.valueOf(in_sum.getText().toString());
        }catch (NumberFormatException e){
            Toast.makeText(this,R.string.errorInput,Toast.LENGTH_SHORT).show();
            return;
        }
        float res = valuteConverter.convert(sum, in_valute.getSelectedItemId(),out_valute.getSelectedItemId());
        if(res < 0) {
            Toast.makeText(this,R.string.errorConvert,Toast.LENGTH_SHORT).show();
            out_sum.setText("Err");
            return;
        }
        out_sum.setText(String.format("%.2f",res));
        if( valuteConverter.isActualExchangeRate())
            not_actual.setVisibility(View.INVISIBLE);
        else
            not_actual.setVisibility(View.VISIBLE);
    }
}

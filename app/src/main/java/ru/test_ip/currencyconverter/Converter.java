package ru.test_ip.currencyconverter;

/**
 * Created by Илья on 29.01.2018.
 */
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/*
 * Класс Converter отвечает за данные (контролирует подгрузку из сети, выполняет вычисления, отдает сформированный перечень валют)
 *
 */
public class Converter {
    static private Converter instance;
    static public synchronized Converter getInstance(Context context){
        if(instance == null){
            instance = new Converter(context);
        }
        return instance;
    }

    Converter(Context context){
        this.context = context;
        readExchangeRate();
    }
    Context context;


    private ValCurs valuteCurs = null;

    public boolean isActualExchangeRate(){
        final SimpleDateFormat dateCurseFormat = new SimpleDateFormat("dd.MM.yyyy");
        String today = dateCurseFormat.format(Calendar.getInstance().getTime());
        if(valuteCurs != null && valuteCurs.date != null){
            return today.equals(valuteCurs.date);
        }
        return false;
    }

    public boolean isExistsExchangeRate(){
        if(valuteCurs == null) return false;
        return true;
    }


    class DownloadNotifer implements Downloader.iMessageDownloader{
        @Override
        public void onCompleted() {
            Converter.this.readExchangeRate();
            Intent intent = new Intent(context.getApplicationContext().getString(R.string.actionUpdateExchangeRate));
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }
    public void updateExchangeRate(){
        new Downloader(context, new DownloadNotifer()).execute();
    }

    public synchronized boolean readExchangeRate(){
        Serializer serializer = new Persister();
        try {
            valuteCurs = serializer.read(ValCurs.class, context.openFileInput("cacheCurs"));
        }catch(Exception e){
            valuteCurs = null;
            return false;
        }
        valuteCurs.prepareData();
        return true;
    }

    public List<Valute> getListValutes(){
        if(valuteCurs == null) return null;
        return Collections.unmodifiableList(valuteCurs.valutes);
    }

    // Основная функция конвертации из одной валюты в другую
    public float convert(float sum,long fromValute,long toValute){
        Valute vfrom = valuteCurs.mvalutes.get(fromValute),
               vto   = valuteCurs.mvalutes.get(toValute);
        if (vfrom == null || vto == null) return -1;
        if( vfrom.value == 0 || vto.nominal == 0) return -1;
        return sum * vfrom.value / vfrom.nominal / vto.value * vto.nominal;

    }



}

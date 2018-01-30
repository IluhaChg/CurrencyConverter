package ru.test_ip.currencyconverter;

/**
 * Created by Илья on 29.01.2018.
 */
import android.content.Context;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


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
    }
    Context context;


    private ValCurs valuteCurs = null;

    Converter(){
        readExchangeRate();
    }

    public boolean isActualExchangeRate(){
        final SimpleDateFormat dateCurseFormat = new SimpleDateFormat("dd.MM.yyyy");
        String today = dateCurseFormat.format(Calendar.getInstance().getTime());
        if(valuteCurs != null && valuteCurs.date != null){
            return today.equals(valuteCurs.date);
        }
        return false;
    }


    class DownloadNotifer implements Downloader.iMessageDownloader{
        @Override
        public void onCompleted() {
            Converter.this.readExchangeRate();
        }
    }
    public void updateExchangeRate(){
        new Downloader(context, new DownloadNotifer()).execute();
    }

    public synchronized boolean readExchangeRate(){
        Serializer serializer = new Persister();
        try {
            valuteCurs = serializer.read(ValCurs.class, new File(context.getFilesDir(), "cacheCurs"));
        }catch(Exception e){
            e.printStackTrace();
            valuteCurs = null;
            return false;
        }
        valuteCurs.prepareData();
        return true;
    }

    public List<Valute> getListValutes(){
        valuteCurs = new ValCurs();
        valuteCurs.prepareData();
        return Collections.unmodifiableList(valuteCurs.valutes);
    }

    public float convert(float sum,long fromValute,long toValute){
        Valute vfrom = valuteCurs.mvalutes.get(fromValute),
               vto   = valuteCurs.mvalutes.get(toValute);
        if (vfrom == null || vto == null) return -1;
        if( vfrom.value == 0 || vto.nominal == 0) return -1;
        return sum * vfrom.nominal / vfrom.value * vto.value / vto.nominal ;

    }



}

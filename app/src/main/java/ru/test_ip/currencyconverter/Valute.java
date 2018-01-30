package ru.test_ip.currencyconverter;

/**
 * Created by Илья on 29.01.2018.
 */

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Root
class ValCurs {

     @ElementList(inline=true)
     public List<Valute> valutes = new ArrayList<Valute>();
     public HashMap<Long,Valute> mvalutes;

     @Attribute
     public String date;


     public void prepareData(){
        if(valutes != null){
            Valute rub = new Valute();
            mvalutes = new HashMap<Long,Valute>();
            rub.numCode = 0;
            rub.charCode = "RUB";
            rub.nominal = 1;
            rub.nameValute = "Российский рубль";
            rub.value = 1;
            valutes.add(rub);

            for (Valute v: valutes) {
                mvalutes.put(v.numCode,v);
            }
        }

     }

}

public class Valute {
    public String valuteId;
    public long numCode;
    public String charCode;
    public int nominal;
    public String nameValute;
    public float value;

}

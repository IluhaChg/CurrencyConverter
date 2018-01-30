package ru.test_ip.currencyconverter;

/**
 * Created by Илья on 29.01.2018.
 */

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.HashMap;
import java.util.List;

@Root(name="ValCurs")
class ValCurs {

     @ElementList(entry="Valute",inline=true)
     public List<Valute> valutes;
     public HashMap<Long,Valute> mvalutes;

     @Attribute(name="Date")
     public String date;
     @Attribute(name="name")
     private String nxml;


     public void prepareData(){
        if(valutes != null){
            Valute rub = new Valute();
            mvalutes = new HashMap<Long,Valute>();
            rub.numCode = 0;
            rub.charCode = "RUB";
            rub.nominal = 1;
            rub.name = "Российский рубль";
            rub.valueString = "1";
            valutes.add(rub);

            for (Valute v: valutes) {
                v.value = Float.valueOf(v.valueString.replace(',','.'));
                mvalutes.put(v.numCode,v);
            }
        }

     }

}
@Root(name="Valute")
public class Valute {
    @Attribute(name="ID")
    public String valuteId;
    @Element(name="NumCode")
    public long numCode;
    @Element(name="CharCode")
    public String charCode;
    @Element(name="Nominal")
    public int nominal;
    @Element(name="Name")
    public String name;
    @Element(name="Value")
    public String valueString;
    public float value;

}

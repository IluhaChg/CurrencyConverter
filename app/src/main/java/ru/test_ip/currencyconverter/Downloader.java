package ru.test_ip.currencyconverter;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Илья on 29.01.2018.
 */



public class Downloader extends AsyncTask<Void,Void,Boolean> {

    public interface iMessageDownloader {
        void onCompleted();
    }

    private Context context;
    private iMessageDownloader message;

    Downloader(Context context, iMessageDownloader message){
        this.context = context;
        this.message = message;
    }

    private synchronized boolean writeCacheCurs(ByteArrayOutputStream ba){
        FileOutputStream out;
        try{
            out = context.openFileOutput("cacheCurs",Context.MODE_PRIVATE);
            out.write(ba.toByteArray());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        HttpURLConnection con = null;
        try {
            URL url = new URL(context.getString(R.string.urlCBR));
            con = (HttpURLConnection) url.openConnection();

            InputStream inp = new BufferedInputStream(con.getInputStream());
            ByteArrayOutputStream ba_out = new ByteArrayOutputStream();
            byte [] b = new byte[1024];
            int size = inp.read(b);
            while (size > 0) {
                ba_out.write(b,0, size);
                size = inp.read(b);
            }
            if( ! writeCacheCurs(ba_out)) return false;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if(con != null)
                con.disconnect();
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result){
        if(result)
            message.onCompleted();
    }
}

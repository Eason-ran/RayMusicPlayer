package com.raymondqk.raymusicplayer.utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by 陈其康 raymondchan on 2016/8/9 0009.
 */
public class MyFileUtil {
    private Context mContext;
    private File mFile;

    public MyFileUtil(Context context) {
        mContext = context;
    }

    public static String readInternalFile(Context context, String filename) {
        BufferedReader reader = null;
        FileInputStream in = null;
        StringBuilder builder = new StringBuilder();
        String line = "";
        try {
            in = context.openFileInput(filename);
            reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return builder.toString();
    }

    public static void wirteInternalFile(Context context, String filename, int mode, String data) {
        FileOutputStream out;
        OutputStreamWriter streamWriter;
        BufferedWriter writer = null;
        try {
            out = context.openFileOutput(filename, mode);
            streamWriter = new OutputStreamWriter(out);
            writer = new BufferedWriter(streamWriter);
            writer.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static void writeExternalFile(String filename, String data) {
        File file = new File(Environment.getExternalStorageDirectory(), filename);
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(data);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }


}

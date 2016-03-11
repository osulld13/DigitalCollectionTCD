package com.example.osulld13.digitalcollections;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Donal on 11/03/2016.
 */
public class ResponseJSONParser {

    public List parseMetadata(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMessage(reader);
        } finally {
            reader.close();
        }
    }

    public List<String> readMessage(JsonReader reader) throws IOException {
        // title, origin_place, publisher, date, language, abstract, access_condition
        String title = "";
        String originPlace = "";
        String publisher = "";
        String date = "";
        String language = "";
        String docAbstract = "";
        String accessCondition = "";

        List<String> returnList = new ArrayList<String>();

        // Get values for items

        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            if (name.equals("mods")){
                reader.beginObject();
                while(reader.hasNext()){
                    name = reader.nextName();
                    if(name.equals("titleInfo")){
                        title = readTitleInfo(reader, title);
                    }
                    else if (name.equals("originInfo")){

                    }
                    else{
                        reader.skipValue();
                    }
                }
            }
            else{
                reader.skipValue();
            }
        }

        returnList.add(title);
        returnList.add(originPlace);
        returnList.add(publisher);
        returnList.add(date);
        returnList.add(language);
        returnList.add(docAbstract);
        returnList.add(accessCondition);

        return returnList;
    }

    private String readTitleInfo(JsonReader reader, String title) throws IOException {
        String name;
        boolean titleFound = false;
        try{
            reader.beginArray();
            while(reader.hasNext() && titleFound == true){
                reader.beginObject();
                while(reader.hasNext() && titleFound == true){
                    name = reader.nextName();
                    if (name.equals("title")){
                        title = reader.nextString();
                        titleFound = true;
                    }
                    else{
                        reader.skipValue();
                    }
                }
                reader.endObject();
            }
            reader.endArray();
        } catch(Exception e){

            reader.beginObject();
            while(reader.hasNext()){
                name = reader.nextName();
                if (name.equals("title")){
                    title = reader.nextString();
                }
                else{
                    reader.skipValue();
                }
            }

        }
        return title;
    }

}

package com.example.osulld13.digitalcollections;

import android.util.JsonReader;
import android.util.JsonToken;

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
            if (name.equals(JSONParsingConstants.mods)){
                reader.beginObject();
                while(reader.hasNext()){
                    name = reader.nextName();
                    if(name.equals(JSONParsingConstants.titleInfo)){
                        title = readTitleInfo(reader, title);
                    }
                    else if (name.equals(JSONParsingConstants.originInfo)){
                        reader.beginObject();
                        while(reader.hasNext()){
                            name = reader.nextName();
                            if (name.equals(JSONParsingConstants.place)){
                                reader.beginObject();
                                while(reader.hasNext()) {
                                    name = reader.nextName();
                                    if(name.equals(JSONParsingConstants.placeTerm)){
                                        reader.beginObject();
                                        while(reader.hasNext()) {
                                            name = reader.nextName();
                                            if (name.equals(JSONParsingConstants.dollar)){
                                                originPlace = reader.nextString();
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

        if (reader.peek() == JsonToken.BEGIN_ARRAY){
            reader.beginArray();
            while(reader.hasNext()){
                reader.beginObject();
                while(reader.hasNext()){
                    name = reader.nextName();
                    if(titleFound == true){
                        reader.skipValue();
                    }
                    else if (name.equals(JSONParsingConstants.title)){
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
        }

        else {
            reader.beginObject();
            while(reader.hasNext()){
                name = reader.nextName();
                if (name.equals(JSONParsingConstants.title)){
                    title = reader.nextString();
                }
                else{
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
        return title;
    }

}

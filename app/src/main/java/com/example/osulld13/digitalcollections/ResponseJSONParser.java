package com.example.osulld13.digitalcollections;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Donal on 11/03/2016.
 */
public class ResponseJSONParser {

    private final String TAG = ResponseJSONParser.class.getSimpleName();

    public ArrayList parsePopularData(InputStream in )throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readPopularMessage(reader);
        } finally {
            reader.close();
        }
    }

    private ArrayList readPopularMessage(JsonReader reader) throws IOException {
        ArrayList<String []> popularObjects = new ArrayList<String []>();

        reader.beginObject();
        int objectCount = 0;
        while(reader.hasNext()){
            String name = reader.nextName();
            if(name.equals(JSONParsingConstants.objects)){
                reader.beginArray();
                while(reader.hasNext()){
                    if(objectCount < AppConstants.popularItemCount) {
                        String[] object = readObject(reader);
                        popularObjects.add(object);
                        objectCount++;
                    }
                    else {
                        reader.skipValue();
                    }
                }
                reader.endArray();
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();


        return popularObjects;
    }

    private String [] readObject(JsonReader reader) throws IOException {
        String pid = "";
        String folderId = "";

        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            if(name.equals(JSONParsingConstants.pid)){
                pid = reader.nextString();
            }
            else if (name.equals(JSONParsingConstants.folderId)){
                folderId = reader.nextString();
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();

        String [] object = {pid, folderId};

        return object;
    }

    public String[] getPopularItemMetadata(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readPopularMetadata(reader);
        } finally {
            reader.close();
        }
    }

    private String[] readPopularMetadata(JsonReader reader) throws IOException{
        String [] metadata = new String[2];

        String genre = "";
        String title = "";

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
                    else if (name.equals(JSONParsingConstants.genre)){
                        genre = readGenre(reader);
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

        metadata[0] = genre;
        metadata[1] = title;

        return metadata;
    }

    private String readGenre(JsonReader reader) throws IOException {
        String genre ="";

        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            if(name.equals(JSONParsingConstants.dollar)){
                genre = reader.nextString();
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();;

        return genre;
    }

    public ArrayList parseMetadata(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMessage(reader);
        } finally {
            reader.close();
        }
    }

    public ArrayList<String> readMessage(JsonReader reader) throws IOException {
        // title, origin_place, publisher, date, language, abstract, access_condition
        String title = "";
        String originPlace = "";
        String publisher = "";
        String date = "";
        String language = "";
        String docAbstract = "";
        String accessCondition = "";

        ArrayList<String> returnList = new ArrayList<String>();

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
                                originPlace = readPlace(reader, originPlace);
                            }
                            else if(name.equals(JSONParsingConstants.publisher)){
                                publisher = readPublisher(reader, publisher);
                            }
                            else if(name.equals(JSONParsingConstants.dateOther)){
                                date = readDate(reader);
                            }
                            else{
                                reader.skipValue();
                            }
                        }
                        reader.endObject();
                    }
                    else if (name.equals(JSONParsingConstants.language)){
                        language = readLanguage(reader, language);
                    }
                    else if (name.equals(JSONParsingConstants.abstractVal)){
                        docAbstract = readAbstract(reader, docAbstract);
                    }
                    else if (name.equals(JSONParsingConstants.accessCondition)){
                        accessCondition = readAccessCondition(reader, accessCondition);
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

    private String readAccessCondition(JsonReader reader, String accessCondition) throws IOException {
        String name;
        reader.beginObject();
        while(reader.hasNext()){
            name = reader.nextName();
            if(name.equals(JSONParsingConstants.dollar)){
                accessCondition = reader.nextString();
            }
            else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return accessCondition;
    }

    private String readAbstract(JsonReader reader, String docAbstract) throws IOException {
        String name;
        reader.beginObject();
        while (reader.hasNext()){
            name = reader.nextName();
            if(name.equals(JSONParsingConstants.dollar)){
                docAbstract = reader.nextString();
            }
            else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return docAbstract;
    }

    private String readLanguage(JsonReader reader, String language) throws IOException {
        String name;

        if (reader.peek() == JsonToken.BEGIN_ARRAY){
            reader.beginArray();
            while(reader.hasNext()){
                if (language.equals("")) {
                    language += readLanguage(reader, language);
                }
                else {
                    language += ", " + readLanguage(reader, language);
                }
            }
            reader.endArray();
        }

        else {
            reader.beginObject();
            while (reader.hasNext()) {
                name = reader.nextName();
                if (name.equals(JSONParsingConstants.languageTerm)) {
                    if (reader.peek().equals(JsonToken.BEGIN_ARRAY)) {
                        reader.beginArray();
                        while (reader.hasNext()) {
                            reader.beginObject();
                            while (reader.hasNext()) {
                                name = reader.nextName();
                                if (name.equals(JSONParsingConstants.type)) {
                                    if (reader.nextString().equals(JSONParsingConstants.text)) {
                                        while (reader.hasNext()) {
                                            name = reader.nextName();
                                            if (name.equals(JSONParsingConstants.dollar)) {
                                                language = reader.nextString();
                                            } else {
                                                reader.skipValue();
                                            }
                                        }
                                    }
                                } else {
                                    reader.skipValue();
                                }
                            }
                            reader.endObject();
                        }
                        reader.endArray();
                    }
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
        return language;
    }

    private String readDate(JsonReader reader) throws IOException {
        String date;
        if (reader.peek() == JsonToken.BEGIN_ARRAY) {
            reader.beginArray();
            date = reader.nextString();
            while (reader.hasNext()){
                reader.skipValue();
            }
            reader.endArray();
        }
        else {
            date = reader.nextString();
        }
        return date;
    }

    private String readPublisher(JsonReader reader, String publisher) throws IOException {
        if(reader.peek() != JsonToken.NULL) {
            publisher = reader.nextString();
        }
        else {
            reader.skipValue();
        }
        return publisher;
    }

    private String readPlace(JsonReader reader, String originPlace) throws IOException {
        String name;
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
                reader.endObject();
            }
            else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return originPlace;
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

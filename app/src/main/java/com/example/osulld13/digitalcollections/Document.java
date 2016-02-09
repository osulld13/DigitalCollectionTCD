package com.example.osulld13.digitalcollections;

/**
 * Created by osulld13 on 07/02/16.
 */
public class Document{

    public String mPid; // PID
    public String mDrisFolderNumber; // identifier_DRIS_FOLDER"
    public String mGenre; // genre
    public String mLang; // language
    public String mTypeOfResource; // typeOfResource
    public String mText; // allText

    public Document(String pid, String drisFolderNumber, String genre){
        this.mPid = pid;
        this.mDrisFolderNumber = drisFolderNumber;
        this.mGenre = genre;
    }
    /*
        Getter Methods
     */

    public String getPid() {
        return mPid;
    }

    public String getDrisFolderNumber() {
        return mDrisFolderNumber;
    }

    public String getGenre() {
        return mGenre;
    }

    public String getLang() {
        return mLang;
    }

    public String getTypeOfResource() {
        return mTypeOfResource;
    }

    public String getText() {
        return mText;
    }

    /*
        Setter Methods
     */

    public void setPid(String mPid) {
        this.mPid = mPid;
    }

    public void setDrisFolderNumber(String mDrisFolderNumber) {
        this.mDrisFolderNumber = mDrisFolderNumber;
    }

    public void setGenre(String mGenre) {
        this.mGenre = mGenre;
    }

    public void setLang(String mLang) {
        this.mLang = mLang;
    }

    public void setTypeOfResource(String mTypeOfResource) {
        this.mTypeOfResource = mTypeOfResource;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public String toString(){
        return "Document: " + this.mPid + " " + this.mDrisFolderNumber + " " + this.mGenre + "\n";
    }
}
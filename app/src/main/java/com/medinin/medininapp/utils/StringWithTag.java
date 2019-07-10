package com.medinin.medininapp.utils;

public class StringWithTag {
    public String string;
    public Object tag;

    public StringWithTag(String stringPart, Object tagPart) {
        string = stringPart;
        tag = tagPart;
    }

    @Override
    public String toString() {
        return string;
    }

    public Object getTag() {
        return tag;
    }

    public String getString() {
        return string;
    }
}
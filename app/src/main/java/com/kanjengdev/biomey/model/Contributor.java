package com.kanjengdev.biomey.model;

public class Contributor {
    String name;
    String occupation;
    int image;

    public Contributor(String name, String occupation, int image) {
        this.name = name;
        this.occupation = occupation;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}

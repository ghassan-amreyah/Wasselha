package com.cs.wasselha.CollectionPointProvider;

import android.widget.Button;

public class CollectionPoints {

    int id,image;
    String collectionPointName;
    Button manageBtn;
    public CollectionPoints(int id,int image, String collectionPointName, Button manageBtn)
    {
        this.id = id;
        this.image = image;
        this.collectionPointName = collectionPointName;
        this.manageBtn = manageBtn;
    }
    public CollectionPoints(int image, String collectionPointName, Button manageBtn)
    {
        this.image = image;
        this.collectionPointName = collectionPointName;
        this.manageBtn = manageBtn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getCollectionPointName() {
        return collectionPointName;
    }

    public void setCollectionPointName(String collectionPointName) {
        this.collectionPointName = collectionPointName;
    }

    public Button getManageBtn() {
        return manageBtn;
    }

    public void setManageBtn(Button manageBtn) {
        this.manageBtn = manageBtn;
    }
}

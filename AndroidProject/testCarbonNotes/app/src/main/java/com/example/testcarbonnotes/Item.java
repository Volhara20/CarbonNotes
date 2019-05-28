package com.example.testcarbonnotes;

class Item {
    private String Name;
    private String Text;
    public Item(String name,String text)
    {
        Name=name;
        Text =text;
    }
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }
}

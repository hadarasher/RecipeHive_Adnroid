package com.example.recipehive.data;

public class IngDataModel {
    private String name;
    private String amount;

    public IngDataModel(String name, String amount) {
        this.name = name;
        this.amount = amount;
    }
    public IngDataModel(IngDataModel ingDataModel){
        this.name=ingDataModel.getName();
        this.amount=ingDataModel.getAmount();
    }
    public IngDataModel(){
        this.name="";
        this.amount="";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}

package com.medinin.medininapp.data;

public class MedicineItem {
    private String id;
    private String name;
    private String medicine_id;
    private String form;
    private String status;
    private String trade_name;
    private String strength_value;
    private String unit_of_strength;
    private String search_str;

    public MedicineItem() {
    }

    public MedicineItem(String id, String name, String form, String status, String medicine_id, String trade_name, String strength_value, String unit_of_strength, String search_str) {
        super();
        this.id = id;
        this.name = name;
        this.form = form;
        this.status = status;
        this.medicine_id = medicine_id;
        this.trade_name = trade_name;
        this.unit_of_strength = unit_of_strength;
        this.strength_value = strength_value;
        this.search_str = search_str;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSearch_str() {
        return search_str;
    }

    public void setSearch_str(String search_str) {
        this.search_str = search_str;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMedicine_id() {
        return medicine_id;
    }

    public void setMedicine_id(String medicine_id) {
        this.medicine_id = medicine_id;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTradeName() {
        return trade_name;
    }

    public void setTradeName(String trade_name) {
        this.trade_name = trade_name;
    }

    public String getStrengthValue() {
        return strength_value;
    }

    public void setStrengthValue(String strength_value) {
        this.strength_value = strength_value;
    }

    public String getUnitOfStrength() {
        return unit_of_strength;
    }

    public void setUnitOfStrength(String unit_of_strength) {
        this.unit_of_strength = unit_of_strength;
    }
}

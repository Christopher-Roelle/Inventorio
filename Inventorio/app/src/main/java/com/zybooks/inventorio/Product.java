package com.zybooks.inventorio;

public class Product {
    private int itemID;
    private String productName;
    private String brand;
    private String location;
    private int quantity;

    private float price = 0.00f;

    public Product(int itemID, String productName, String location, int quantity)
    {
        this.itemID = itemID;
        this.productName = productName;
        this.location = location;
        this.quantity = quantity;
    }

    //Get/Sets
    public int GetItemID() {
        return itemID;
    }

    public void SetItemID(int itemID)
    {
        this.itemID = itemID;
    }

    public String GetProductName() {
        return CapitalizeWords(productName);
    }

    public void SetProductName(String productName)
    {
        this.productName = CapitalizeWords(productName);
    }

    public String GetBrand()
    {
        return CapitalizeWords(brand);
    }

    public String GetBrandShort()
    {

        String[] words = brand.split("\\s");

        if(words.length == 1)
        {
            return brand.substring(0, Math.min(3, brand.length())).toUpperCase();
        }
        else {
            StringBuilder shortBrand = new StringBuilder();

            for (String word : words) {
                shortBrand.append(word.substring(0, 1));
            }

            return shortBrand.toString();
        }
    }

    public void SetBrand(String brand)
    {
        this.brand = CapitalizeWords(brand);
    }

    public String GetLocation() {
        return CapitalizeWords(location);
    }

    public void SetLocation(String location)
    {
        this.location = CapitalizeWords(location);
    }

    public int GetQuantity() {
        return quantity;
    }

    public void SetQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public float GetPrice(){ return price;}
    public void SetPrice(float price) { this.price = price; }

    //Functions
    public int IncrementQuantity()
    {
        quantity++;
        return quantity;
    }

    public int DecremenetQuantity()
    {
        quantity--;
        return quantity;
    }

    //HELPERS
    //====================

    public String CapitalizeWords(String input)
    {
        String[] words = input.split("\\s");

        StringBuilder builder = new StringBuilder();

        for (String word : words)
        {
            if(!word.isEmpty())
            {
                builder.append(word.substring(0,1).toUpperCase())
                        .append(word.substring(1).toLowerCase())
                        .append(' ');
            }
        }

        return builder.toString();
    }
}

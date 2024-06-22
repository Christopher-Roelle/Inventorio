package com.zybooks.inventorio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InventoryDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Inventory.db";
    private static final int DB_VERSION = 4;
    private static final String TABLE_NAME = "inventory";

    private static final String COL_ID = "itemID";
    private static final String COL_PRODUCT_NAME = "productName";
    private static final String COL_BRAND = "brand";
    private static final String COL_PRICE = "price";
    private static final String COL_LOCATION = "locationName";
    private static final String COL_QUANTITY = "quantity";

    public InventoryDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PRODUCT_NAME + " TEXT NOT NULL, " +
                COL_BRAND + " TEXT, " +
                COL_PRICE + " DECIMAL NOT NULL," +
                COL_LOCATION + " TEXT, " +
                COL_QUANTITY + " INTEGER " +
                ")";

        db.execSQL(sql);
    }

    //TODO: Add logic to backup old table to transfer to new Version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //Adds the user to the DB. Returns false if error or user exists already.
    //TODO: Handle errors with numerical code to differentiate reason for failure.
    public boolean AddProduct(String productName, String brand, float price, String location, int quantity)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        //Check if the product already exists
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_PRODUCT_NAME + " = ?", new String[]{productName});
        if(cursor.getCount() > 0)
        {
            //Product exists
            cursor.close();
            return false;
        }

        //Begin insert transaction
        ContentValues cv = new ContentValues();
        cv.put(COL_PRODUCT_NAME, productName);
        cv.put(COL_BRAND, brand);
        cv.put(COL_PRICE, price);
        cv.put(COL_LOCATION, location);
        cv.put(COL_QUANTITY, quantity);

        long result = db.insert(TABLE_NAME, null, cv);

        //Check the insert worked
        return true;
    }

    public boolean UpdateProduct(Product product, String newName, String newBrand, float newPrice, String newLocation, int newQuantity)
    {
        boolean changesMade = false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        String whereClause = COL_ID + " = ?";

        //Only put values in if they differ
        if(!Objects.equals(product.GetProductName(), newName)) { cv.put(COL_PRODUCT_NAME, newName); changesMade = true; }
        if(!Objects.equals(product.GetBrand(), newBrand)) { cv.put(COL_BRAND, newBrand); changesMade = true; }
        if(!Objects.equals(product.GetPrice(), newPrice)) { cv.put(COL_PRICE, newPrice); changesMade = true; }
        if(!Objects.equals(product.GetLocation(), newLocation)) { cv.put(COL_LOCATION, newLocation); changesMade = true; }
        if(product.GetQuantity() != newQuantity) { cv.put(COL_QUANTITY, newQuantity); changesMade = true;}

        //If changes made is set, then we will attempt the update, otherwise, don't bother
        if(changesMade)
        {
            int rowsAffected = db.update(TABLE_NAME, cv, whereClause, new String[]{String.valueOf(product.GetItemID())} );
            db.close();

            return rowsAffected > 0;
        }
        else {
            return false;
        }
    }

    //Deletes a product based on its ID
    public boolean DeleteProduct(Product product)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        //Where clause
        String whereClause = COL_ID + " = ?";
        String[] whereArgs = {String.valueOf(product.GetItemID())};

        //Begin delete transaction
        int rowsDeleted = db.delete(TABLE_NAME, whereClause, whereArgs);

        return rowsDeleted > 0;
    }

    public List<Product> RetrieveInventory()
    {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        //Retrieve all items
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        //Check if results, if so, populate the list of products
        if(cursor.moveToFirst())
        {
            do {
                //Retrieve the current cursor
                int itemID = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String productName = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_NAME));
                String brand = cursor.getString(cursor.getColumnIndexOrThrow(COL_BRAND));
                float price = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_PRICE));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY));

                //Create a product based on the data
                Product product = new Product(itemID, productName, location, quantity);
                product.SetPrice(price);
                product.SetBrand(brand);

                //Add the product to the list
                productList.add(product);
            }
            while(cursor.moveToNext());
        }

        cursor.close();

        return productList;
    }

    public List<Product> RetrieveInventory(int underQuantity)
    {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        //Retrieve all items under quantity
        String whereClause = COL_QUANTITY + " <= ?";
        String[] selectionArgs = {String.valueOf(underQuantity)};
        Cursor cursor = db.query(TABLE_NAME, null, whereClause, selectionArgs, null, null, null);

        //Check if results, if so, populate the list of products
        if(cursor.moveToFirst())
        {
            do {
                //Retrieve the current cursor
                int itemID = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String productName = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_NAME));
                String brand = cursor.getString(cursor.getColumnIndexOrThrow(COL_BRAND));
                float price = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_PRICE));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY));

                //Create a product based on the data
                Product product = new Product(itemID, productName, location, quantity);
                product.SetPrice(price);

                //Add the product to the list
                productList.add(product);
            }
            while(cursor.moveToNext());
        }

        cursor.close();

        return productList;
    }

    public boolean UpdateQuantity(int itemID, int newQuantity)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_QUANTITY, newQuantity);

        //Make the update
        int rowsAffected = db.update(TABLE_NAME, cv, COL_ID + " = ?", new String[]{String.valueOf(itemID)});

        return rowsAffected > 0;
    }
}

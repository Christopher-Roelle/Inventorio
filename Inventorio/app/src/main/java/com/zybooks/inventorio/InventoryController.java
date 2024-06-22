package com.zybooks.inventorio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Locale;

public class InventoryController {

    private Context mContext;
    private TableLayout mTableLayout;
    private InventoryDatabase mDB;

    public InventoryController(Context context, TableLayout tableLayout, InventoryDatabase db)
    {
        mContext = context;
        mTableLayout = tableLayout;
        mDB = db;
    }

    public void PopulateTable(List<Product> productList)
    {
        mTableLayout.removeAllViews();

        //Check device orientation to hide columns
        boolean isPortrait = mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        int maxProductNameInPortrait = 8;
        int maxLocationCodeInPortrait = 4;

        //Get the secondary color
        TypedValue typedValue = new TypedValue();
        mContext.getTheme().resolveAttribute(android.R.attr.colorSecondary, typedValue, true);
        int colorSecondary = typedValue.data;

        //Add the headers
        TableRow header = new TableRow(mContext);
        header.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        header.setBackgroundColor(colorSecondary);

        //Header labels
        TextView hNumTextView = new TextView(mContext);
        hNumTextView.setText("#");
        hNumTextView.setTextColor(Color.WHITE);
        hNumTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

        TextView hNameTextView = new TextView(mContext);
        hNameTextView.setText(mContext.getString(R.string.product));
        hNameTextView.setTextColor(Color.WHITE);
        hNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

        TextView hPriceTextView = new TextView(mContext);
        hPriceTextView.setText(mContext.getString(R.string.price));
        hPriceTextView.setTextColor(Color.WHITE);
        hPriceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

        TextView hLocationTextView = new TextView(mContext);
        if(isPortrait) { hLocationTextView.setText(mContext.getString(R.string.location_shorthand)); }
        else { hLocationTextView.setText(mContext.getString(R.string.location)); }
        hLocationTextView.setTextColor(Color.WHITE);
        hLocationTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

        TextView hQuantityTextView = new TextView(mContext);
        hQuantityTextView.setText(mContext.getString(R.string.quantity));
        hQuantityTextView.setTextColor(Color.WHITE);
        hQuantityTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

        TextView hControlsPosTextView = new TextView(mContext);
        hControlsPosTextView.setText("+");
        hControlsPosTextView.setTextColor(Color.WHITE);
        hControlsPosTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        hControlsPosTextView.setGravity(Gravity.CENTER);

        TextView hControlsNegTextView = new TextView(mContext);
        hControlsNegTextView.setText("-");
        hControlsNegTextView.setTextColor(Color.WHITE);
        hControlsNegTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        hControlsNegTextView.setGravity(Gravity.CENTER);

        if(!isPortrait) { header.addView(hNumTextView); }
        header.addView(hNameTextView);
        header.addView(hPriceTextView);
        header.addView(hLocationTextView);
        header.addView(hQuantityTextView);
        header.addView(hControlsPosTextView);
        header.addView(hControlsNegTextView);


        mTableLayout.addView(header);

        int rowNum = 1;

        //Iterate the products and add them to the table
        for(Product product : productList)
        {
            int shade = 0;

            //Determine if row should be shaded
            if(rowNum % 2 == 0)
            {
                mContext.getTheme().resolveAttribute(android.R.attr.colorControlHighlight, typedValue, true);
                shade = typedValue.data;
            }
            else {
                mContext.getTheme().resolveAttribute(android.R.attr.colorBackground, typedValue, true);
                shade = typedValue.data;
            }

            TableRow row = new TableRow(mContext);

            //Add an onClick listener to the row for opening the modal
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    ShowUpdateModal(product);
                }
            });

            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(layoutParams);
            row.setBackgroundColor(shade);

            //Row Number
            TextView rowNumTextView = new TextView(mContext);
            rowNumTextView.setText(String.valueOf(rowNum));
            rowNumTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

            //Product Name
            TextView productNameTextView = new TextView(mContext);
            String tProductBrand = product.GetBrandShort();
            String tProductName = product.GetProductName();

            //Shorten the name if longer than max character count and in portrait
            if(isPortrait && tProductName.length() > maxProductNameInPortrait)
            {
                //Split the name to words
                String[] words = tProductName.split("\\s");

                //Single word, substring it
                if(words.length == 1) { tProductName = tProductName.substring(0, maxProductNameInPortrait); }
                else {
                    //Remove whitespace and vowels
                    tProductName =  tProductName
                            .replaceAll("(?i)[aeiou]", "")
                            .replaceAll("\\s+", "")
                            .replaceAll("(?i)([a-z])\\1+", "$1");

                    //If still too long, then substring
                    if(tProductName.length() > maxProductNameInPortrait)
                    {
                        tProductName = tProductName.substring(0,Math.min(tProductName.length(), maxProductNameInPortrait));
                    }
                }
            }

            if(!tProductBrand.isEmpty())
            {
                tProductName = tProductBrand + "-" + tProductName;
            }

            productNameTextView.setText(tProductName);
            productNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

            //Product Price
            TextView productPriceTextView = new TextView(mContext);
            String tPrice = String.format(Locale.US, "$%.2f", product.GetPrice());
            productPriceTextView.setText(tPrice);
            productPriceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

            //Product Location
            TextView locationTextView = new TextView(mContext);
            String tLocation = product.GetLocation().trim();
            if(tLocation.isEmpty())
            {
                tLocation = "No Location";
            }
            //If portrait and if the text is over 4 character, shorten the location
            if(isPortrait && tLocation.length() > maxLocationCodeInPortrait)
            {
                //First split it on whitespace
                String[] locationWords = tLocation.split("\\s");

                //Then check how many words, if 1, we will substring the location
                if(locationWords.length == 1) { tLocation = locationWords[0].substring(0, maxLocationCodeInPortrait).toUpperCase(); }
                else
                {
                    //Multiple words, we will take the first letter of each word (up to 4 words)
                    //And use that as a location code
                    StringBuilder locationCode = new StringBuilder();
                    for(int i = 0; i < Math.min(locationWords.length, maxLocationCodeInPortrait); i++)
                    {
                        if(!locationWords[i].isEmpty())
                        {
                            locationCode.append(locationWords[i].substring(0,1).toUpperCase());
                        }
                    }
                    tLocation = locationCode.toString();
                }
            }

            locationTextView.setText(tLocation);
            locationTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

            //Quantity
            TextView quantityTextView = new TextView(mContext);
            quantityTextView.setText(String.valueOf(product.GetQuantity()));
            quantityTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

            //Pixels to dp
            float pixelsWidth = 40;
            float dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixelsWidth,
                    mContext.getResources().getDisplayMetrics());

            Button incrementButton = new Button(mContext);
            incrementButton.setText("+");
            incrementButton.setTextColor(Color.WHITE);
            incrementButton.setBackgroundColor(mContext.getColor(R.color.green));
            incrementButton.setLayoutParams(new TableRow.LayoutParams((int)dp,
                    TableRow.LayoutParams.WRAP_CONTENT));
            incrementButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            incrementButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    //Increment the table view and update the DB
                    UpdateQuantity(product, 1, quantityTextView);
                }
            });

            Button decrementButton = new Button(mContext);
            decrementButton.setText("-");
            decrementButton.setTextColor(Color.WHITE);
            decrementButton.setBackgroundColor(mContext.getColor(R.color.red));
            decrementButton.setLayoutParams(new TableRow.LayoutParams((int)dp,
                    TableRow.LayoutParams.WRAP_CONTENT));
            decrementButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            decrementButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    //Decrement the table view and update the DB
                    UpdateQuantity(product, -1, quantityTextView);
                }
            });

            //Add the views
            if(!isPortrait) { row.addView(rowNumTextView); }
            row.addView(productNameTextView);
            row.addView(productPriceTextView);
            row.addView(locationTextView);
            row.addView(quantityTextView);
            row.addView(incrementButton);
            row.addView(decrementButton);

            //Add the row to the table and increment the row
            mTableLayout.addView(row);
            rowNum++;
        }
    }

    public void UpdateQuantity(Product product, int amount, TextView quantityTextView)
    {
        //TODO: Handle if the quantity was updated since the last time the table loaded on this device
        //Update the GUI
        int currentQuantity = Integer.parseInt(quantityTextView.getText().toString());
        int newQuantity = currentQuantity + amount;

        //Prevent negative inventory
        if(newQuantity < 0)
        {
            newQuantity = 0;
        }
        
        //Update the DB
        boolean updated = mDB.UpdateQuantity(product.GetItemID(), newQuantity);
        if(updated)
        {
            quantityTextView.setText(String.valueOf(newQuantity));
        }
        else {
            Toast.makeText(mContext, mContext.getString(R.string.quantity_change_fail), Toast.LENGTH_SHORT).show();
        }
    }

    //Displays a modal for updating the product or deleting it.
    private void ShowUpdateModal(Product product)
    {
        //Create a modal
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View dialogView = inflater.inflate(R.layout.dialog_update_product, null);
        builder.setView(dialogView);
        builder.setTitle("Product Overview");

        EditText productNameEditText = dialogView.findViewById(R.id.editProductName);
        EditText productBrandEditText = dialogView.findViewById(R.id.editProductBrand);
        EditText productPriceEditText = dialogView.findViewById(R.id.editProductPrice);
        EditText productLocationEditText = dialogView.findViewById(R.id.editProductLocation);
        EditText productQuantityEditText = dialogView.findViewById(R.id.editProductQuantity);

        productNameEditText.setText(product.GetProductName());
        productBrandEditText.setText(product.GetBrand());
        productPriceEditText.setText(String.valueOf(product.GetPrice()));
        productLocationEditText.setText(product.GetLocation());
        productQuantityEditText.setText(String.valueOf(product.GetQuantity()));

        Button deleteButton = dialogView.findViewById(R.id.deleteProductButton);
        Button updateButton = dialogView.findViewById(R.id.updateProductButton);

        AlertDialog alertDialog = builder.create();

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = mDB.DeleteProduct(product);

                if(!result)
                {
                    Toast.makeText(mContext, mContext.getString(R.string.unable_to_delete), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(mContext, mContext.getString(R.string.success_delete), Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    UpdateTable();
                }
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = productNameEditText.getText().toString().trim();
                String productBrand = productBrandEditText.getText().toString().trim();
                String productPrice = productPriceEditText.getText().toString().trim();
                String productLocation = productLocationEditText.getText().toString().trim();
                String productQuantity = productQuantityEditText.getText().toString().trim();

                float price = 0.0f;

                //Default price if not exists
                if(!productPrice.isEmpty())
                {
                    price = Float.parseFloat(productPrice);
                }

                if(!productName.isEmpty())
                {
                    if(!productQuantity.isEmpty()) {
                        //Parse the quantity
                        int quantity = Integer.parseInt(productQuantity);

                        //Check quantity is non-negative
                        if (quantity < 0) {
                            Toast.makeText(mContext, mContext.getString(R.string.negative_quantity), Toast.LENGTH_SHORT).show();
                        } else {
                            //Add the product
                            if (mDB.UpdateProduct(product, productName, productBrand, price, productLocation, quantity)) {
                                //Success! Notify, update the table, then dismiss the dialog
                                Toast.makeText(mContext, mContext.getString(R.string.product_updated), Toast.LENGTH_SHORT).show();
                                UpdateTable();
                                alertDialog.dismiss();
                            } else {
                                //Fail, notify
                                Toast.makeText(mContext, mContext.getString(R.string.product_not_updated), Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        }
                    }
                    else {
                        Toast.makeText(mContext, "Missing quantity!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(mContext, "Missing product name!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.show();
    }

    //Updates the table after product DB changes
    private void UpdateTable()
    {
        List<Product> productList = mDB.RetrieveInventory();
        PopulateTable(productList);
    }

}



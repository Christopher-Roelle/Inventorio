package com.zybooks.inventorio;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    private static final String TAG = "INVENTORY_ACTIVITY";
    private TableLayout mTableLayout;
    private InventoryController mInventoryController;
    private FloatingActionButton mAddProductFAB;
    private FloatingActionButton mAddSMSNotificationFAB;
    private InventoryDatabase db;

    private String mPhoneNumber = "";
    private final String PHONE_NUMBER_BUNDLE = "PHONE";
    private int mThresholdQuantity = 10;
    private final String THRESHOLD_QUANTITY_BUNDLE = "THRESHOLD";

    private static final int REQUEST_SMS_PERMISSION = 1;
    private long notifyInterval = 10 * 60 * 1000;
    private Handler mHandler;
    private Runnable mCheckInventoryRunnable;

    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_activity);

        //Get references
        mTableLayout = findViewById(R.id.inventoryTable);
        mAddProductFAB = findViewById(R.id.addProductFAB);
        mAddSMSNotificationFAB = findViewById(R.id.notifyFAB);

        //Get the DB
        db = new InventoryDatabase(this);

        //Instantiate the Controller
        mInventoryController = new InventoryController(this, mTableLayout, db);

        //Add Callbacks
        mAddProductFAB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                ShowAddProductModal();
            }
        });

        mAddSMSNotificationFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPermissionRequestDialog();
            }
        });

        //Populate the table
        UpdateTable();

    }

    //Handle saving and loading the bundle
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putString(PHONE_NUMBER_BUNDLE, mPhoneNumber);
        outState.putInt(THRESHOLD_QUANTITY_BUNDLE, mThresholdQuantity);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        mPhoneNumber = savedInstanceState.getString(PHONE_NUMBER_BUNDLE, "");
        mThresholdQuantity = savedInstanceState.getInt(THRESHOLD_QUANTITY_BUNDLE, 10);
    }

    //Displays the modal to add a product
    private void ShowAddProductModal()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_add_product, null);
        builder.setView(dialogView);
        builder.setTitle(getString(R.string.add_product));
        //builder.setCancelable(false);

        EditText newProductName = dialogView.findViewById(R.id.addProdName);
        EditText newProductBrand = dialogView.findViewById(R.id.addProdBrand);
        EditText newProductPrice = dialogView.findViewById(R.id.addProdPrice);
        EditText newProductLocation = dialogView.findViewById(R.id.addProdLoc);
        EditText newProductQuantity = dialogView.findViewById(R.id.addProdQty);

        //Create and show the alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button positiveButton = dialogView.findViewById(R.id.addProductButton);
        positiveButton.setOnClickListener(new View.OnClickListener()
        {

            //Handles adding a product if valid
            @Override
            public void onClick(View v) {
                //Verify the important columns are populated
                String productName = newProductName.getText().toString().trim();
                String productBrand = newProductBrand.getText().toString().trim();
                String productPrice = newProductPrice.getText().toString().trim();
                String productLocation = newProductLocation.getText().toString().trim();
                String productQuantity = newProductQuantity.getText().toString().trim();

                float price = 0.0f;

                //Parse price if set
                if (!productPrice.isEmpty()) {
                    price = Float.parseFloat(productPrice);
                }

                if(!productName.isEmpty())
                {
                    if(!productQuantity.isEmpty()) {
                        //Parse the quantity
                        int quantity = Integer.parseInt(productQuantity);

                        //Check quantity is non-negative
                        if (quantity < 0) {
                            Toast.makeText(InventoryActivity.this, getString(R.string.negative_quantity), Toast.LENGTH_SHORT).show();
                        } else {
                            //Add the product
                            if (InsertProduct(productName, productBrand, price, productLocation, quantity)) {
                                //Success! Notify, update the table, then dismiss the dialog
                                Toast.makeText(InventoryActivity.this, getString(R.string.add_product_success), Toast.LENGTH_SHORT).show();
                                UpdateTable();
                                alertDialog.dismiss();
                            } else {
                                //Fail, notify
                                Toast.makeText(InventoryActivity.this, getString(R.string.add_product_fail), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else {
                        Toast.makeText(InventoryActivity.this, "Missing quantity!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(InventoryActivity.this, "Missing product name!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Request SMS Dialog
    private void ShowPermissionRequestDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_send_sms, null);
        builder.setView(dialogView);
        builder.setTitle(getString(R.string.permissions_sms_title));

        TextView requestText = dialogView.findViewById(R.id.requestText);
        EditText phoneNumber = dialogView.findViewById(R.id.addNotifyPhone);
        EditText thresholdQuantity = dialogView.findViewById(R.id.notifyQuantity);

        //Set defaults
        if(!mPhoneNumber.isEmpty())
        {
            phoneNumber.setText(mPhoneNumber);
        }

        thresholdQuantity.setText(String.valueOf(mThresholdQuantity));

        requestText.setText(getString(R.string.permissions_sms_desc));

        //Buttons for permission
        builder.setPositiveButton(getString(R.string.grant_permission), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!phoneNumber.getText().toString().isEmpty()) {
                    mPhoneNumber = phoneNumber.getText().toString();
                    mThresholdQuantity = Integer.parseInt(thresholdQuantity.getText().toString());
                    RequestSMSPermission();
                }
            }
        });

        builder.setNegativeButton(getString(R.string.deny_permission), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(InventoryActivity.this, getString(R.string.denied_message), Toast.LENGTH_SHORT).show();
            }
        });

        //Display Modal
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Calls the DB to update the table
    private void UpdateTable()
    {
        //Retrieve the products
        try {
            productList = db.RetrieveInventory();
        } catch (IllegalArgumentException e)
        {
            Toast.makeText(this, "Issue reading database!", Toast.LENGTH_LONG).show();
            Log.e(TAG, "onCreate: InventoryDatabase couldn't retrieve products!", e);
        }

        //Check the table has data
        if(!productList.isEmpty()) {
            //Populate the table
            mInventoryController.PopulateTable(productList);
        }
        else {
            //No items
            TextView noItemsTextView = new TextView(this);
            noItemsTextView.setText(getString(R.string.no_items));
            mTableLayout.addView(noItemsTextView);
        }
    }

    public boolean InsertProduct(String productName, String productBrand, float productPrice, String productLocation, int productQuantity)
    {
        return db.AddProduct(productName, productBrand, productPrice, productLocation, productQuantity);
    }

    private void RequestSMSPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS},
                REQUEST_SMS_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_SMS_PERMISSION)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                StartPeriodicChecking();
                Toast.makeText(InventoryActivity.this, getString(R.string.alerts_started),
                        Toast.LENGTH_SHORT).show();
            }
            else {
                //Denied
                StopPeriodicChecking();
                Toast.makeText(InventoryActivity.this, getString(R.string.denied_message),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Starts the callback to check the inventory and notify of low stock
    private void StartPeriodicChecking()
    {
        mHandler = new Handler();
        mCheckInventoryRunnable = new Runnable() {
            @Override
            public void run() {
                CheckInventoryAndSendSMS();
                mHandler.postDelayed(this, notifyInterval);
            }
        };

        mHandler.postDelayed(mCheckInventoryRunnable, notifyInterval);
    }

    //Stops the callbacks for the notification service
    private void StopPeriodicChecking()
    {
        if(mHandler != null && mCheckInventoryRunnable != null)
        {
            mHandler.removeCallbacks(mCheckInventoryRunnable);
        }
    }

    private void CheckInventoryAndSendSMS()
    {
        List<Product> lowInventoryProducts = db.RetrieveInventory(mThresholdQuantity);

        StringBuilder message = new StringBuilder("The following products are at or under " + mThresholdQuantity + ":");

        if(!lowInventoryProducts.isEmpty())
        {
            //Append the items to the text
            for(Product product : lowInventoryProducts)
            {
                message.append("\n").append(product.GetProductName());
            }

            //Send SMS
            SMSManager.SendSMSMessage(mPhoneNumber, message.toString());
            Toast.makeText(InventoryActivity.this, getString(R.string.sms_sent), Toast.LENGTH_SHORT).show();
        }
    }
}


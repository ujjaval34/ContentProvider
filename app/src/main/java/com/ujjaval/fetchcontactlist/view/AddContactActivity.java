package com.ujjaval.fetchcontactlist.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ujjaval.fetchcontactlist.R;

import java.util.ArrayList;

public class AddContactActivity extends AppCompatActivity {

    private Toolbar reg_tool;
    private EditText et_number;
    private EditText et_name;
    private Button bt_save;
    public static final int PERMISSIONS_REQUEST_WRITE_CONTACTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        reg_tool=(Toolbar) findViewById(R.id.reg_tool);
        setSupportActionBar(reg_tool);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        init();

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void init() {
        et_name=(EditText) findViewById(R.id.et_name);
        et_number=(EditText) findViewById(R.id.et_number);
        bt_save=(Button) findViewById(R.id.bt_save);


        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(AddContactActivity.this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(AddContactActivity.this,
                                android.Manifest.permission.WRITE_CONTACTS)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddContactActivity.this);
                            builder.setTitle("Read contacts access needed");
                            builder.setPositiveButton(android.R.string.ok, null);
                            builder.setMessage("Please enable access to contacts.");
                            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @TargetApi(Build.VERSION_CODES.M)
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    requestPermissions(
                                            new String[]
                                                    {android.Manifest.permission.WRITE_CONTACTS}
                                            , PERMISSIONS_REQUEST_WRITE_CONTACTS);
                                }
                            });
                            builder.show();
                        } else {
                            ActivityCompat.requestPermissions(AddContactActivity.this,
                                    new String[]{android.Manifest.permission.WRITE_CONTACTS},
                                    PERMISSIONS_REQUEST_WRITE_CONTACTS);
                        }
                    } else {
                        writeNumber();
                    }
                } else {
                    writeNumber();
                }

            }
        });



    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSIONS_REQUEST_WRITE_CONTACTS :{

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    writeNumber();
                }else {
                    Toast.makeText(this, "You have disabled a contacts permission", Toast.LENGTH_SHORT).show();
                }

                return;
            }
        }
    }


    private void writeNumber() {

        String name=et_name.getText().toString();
        String number=et_number.getText().toString();
        if (name.length()<=3){
            et_name.setError("Please enter valid name");
            et_name.requestFocus();
            return;
        }

        if (number.length()!=10){
            et_number.setError("Please enter valid name");
            et_number.requestFocus();
            return;
        }
        ArrayList<ContentProviderOperation> contactProviders=new ArrayList<>();

        contactProviders.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());


        // first and last names
        contactProviders.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, "")
                .build());

        // Contact No Mobile
        contactProviders.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());


        try {
            ContentProviderResult[] results = getContentResolver().applyBatch(ContactsContract.AUTHORITY, contactProviders);
            Toast.makeText(AddContactActivity.this, "hello"+results.toString(), Toast.LENGTH_SHORT).show();
            Intent in =new Intent();
            setResult(RESULT_OK,in);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
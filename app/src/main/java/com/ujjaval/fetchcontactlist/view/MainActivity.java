package com.ujjaval.fetchcontactlist.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ujjaval.fetchcontactlist.ContactList;
import com.ujjaval.fetchcontactlist.R;
import com.ujjaval.fetchcontactlist.adapter.Adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    List<ContactList>contactLists=new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Adapter adapter;
    private LinearLayout layout;
    FloatingActionButton fl_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {

        recyclerView = findViewById(R.id.rv_userList);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        layout=(LinearLayout) findViewById(R.id.layout);
        fl_button=(FloatingActionButton) findViewById(R.id.fl_button);
        fl_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in =new Intent(MainActivity.this,AddContactActivity.class);
                startActivityForResult(in,3);
            }
        });

        checkpermission();
    }

    private void checkpermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Read contacts access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to contacts.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_CONTACTS}
                                    , PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                fetchContactList();
            }
        } else {
            fetchContactList();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==3){
            if (resultCode==RESULT_OK){
                fetchContactList();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSIONS_REQUEST_READ_CONTACTS :{

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchContactList();
                }else {
                    Toast.makeText(this, "You have disabled a contacts permission", Toast.LENGTH_SHORT).show();
                }

                return;
            }
        }
    }

    public void fetchContactList() {
        ContentResolver contentResolver=getContentResolver();       //cursor reading contact list
        Cursor cursor=contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);

        if (cursor !=null && cursor.getCount()>0) {

            while (cursor != null && cursor.moveToNext()) {

                String id = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                ContactList contactList=new ContactList();
                contactList.setContactName(name);
                contactList.setId(id);


                if (cursor.getInt(cursor.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        contactList.setContactPhone(phoneNo);


                    }
                    pCur.close();
                    contactLists.add(contactList);   //Add all data in arraylist

                }

            }

            if (cursor != null) {
                cursor.close();
            }
            Collections.sort(contactLists, new Comparator<ContactList>() {   //sorting contact array list
                @Override
                public int compare(ContactList contactList, ContactList t1) {
                    return contactList.getContactName().compareTo(t1.getContactName());
                }
            });
            layout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            adapter = new Adapter(contactLists, MainActivity.this);  //Recycler view adapter
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }else {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }



       // Toast.makeText(this, "Hello " + contactLists.get(0).getContactName() + "/////" + contactLists.get(0).getContactPhone(), Toast.LENGTH_SHORT).show();
    }



}
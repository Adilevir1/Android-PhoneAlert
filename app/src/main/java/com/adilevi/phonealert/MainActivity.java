package com.adilevi.phonealert;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_CONTACTS = 111;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 222;

    private ContactsPref pref;
    private ContactsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pref = new ContactsPref(getApplicationContext());

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<String> contactNames = new ArrayList<>(pref.getContactNames());
        ArrayList<String> contactPhones = new ArrayList<>(pref.getContactPhones());
        adapter = new ContactsAdapter(contactNames, contactPhones, new ContactsAdapter.ContactsAdapterCallback() {
            @Override
            public void onItemRemoved(String name, String phone) {
                pref.removeContact(name, phone);
            }
        });
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean hasContactPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
                boolean hasSmsPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
                if (!hasContactPermission || !hasSmsPermission) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                } else {
                    Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                    startActivityForResult(contactPickerIntent, REQUEST_CODE_PICK_CONTACTS);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_PICK_CONTACTS: {
                if (resultCode == Activity.RESULT_OK) {
                    handlePickContact(this, data);
                    break;
                }
            }
        }
    }

    private void handlePickContact(Activity activity, Intent data) {
        boolean hasContactPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        boolean hasSmsPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
        if (!hasContactPermission || !hasSmsPermission) {
            Toast.makeText(activity, "Error: missing permission", Toast.LENGTH_LONG).show();
            return;
        } else if (data == null || data.getData() == null) {
            Toast.makeText(activity, "Error: no contacts", Toast.LENGTH_LONG).show();
            return;
        }

        Cursor cursor = activity.getContentResolver().query(data.getData(), null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));
                String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                pref.addContact(contactName, contactNumber);
                adapter.addNewContact(contactName, contactNumber);
            }
            cursor.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                    startActivityForResult(contactPickerIntent, REQUEST_CODE_PICK_CONTACTS);
                } else {
                    Toast.makeText(this, "Mast have contacts permission", Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
    }
}

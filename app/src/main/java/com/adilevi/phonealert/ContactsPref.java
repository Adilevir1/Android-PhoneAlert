package com.adilevi.phonealert;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class ContactsPref {

    private static final String PREF_FILE_NAME = "DeviceManagerPref";

    private static final String KEY_CONTACTS_NAME = "CONTACTS_NAME";
    private static final String KEY_CONTACTS_PHONE = "CONTACTS_PHONE";

    private final SharedPreferences sharedPreferences;

    public ContactsPref(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public synchronized void addContact(String contactName, String contactNumber) {
        HashSet<String> newNames = new HashSet<>();
        HashSet<String> newPhones = new HashSet<>();

        Set<String> names = sharedPreferences.getStringSet(KEY_CONTACTS_NAME, new HashSet<String>());
        Set<String> phones = sharedPreferences.getStringSet(KEY_CONTACTS_NAME, new HashSet<String>());

        newNames.addAll(names);
        newNames.add(contactName);

        newPhones.addAll(phones);
        newPhones.add(contactNumber.substring(contactNumber.length() > 9 ? contactNumber.length() - 9 : 0));

        sharedPreferences.edit().putStringSet(KEY_CONTACTS_NAME, newNames).apply();
        sharedPreferences.edit().putStringSet(KEY_CONTACTS_PHONE, newPhones).apply();
    }

    public synchronized void removeContact(String name, String phone) {
        HashSet<String> newNames = new HashSet<>();
        HashSet<String> newPhones = new HashSet<>();

        Set<String> names = sharedPreferences.getStringSet(KEY_CONTACTS_NAME, new HashSet<String>());
        names.remove(name);

        Set<String> phones = sharedPreferences.getStringSet(KEY_CONTACTS_NAME, new HashSet<String>());
        phones.remove(phone);

        newNames.addAll(names);
        newPhones.addAll(phones);

        sharedPreferences.edit().putStringSet(KEY_CONTACTS_NAME, newNames).apply();
        sharedPreferences.edit().putStringSet(KEY_CONTACTS_PHONE, newPhones).apply();
    }

    public synchronized Set<String> getContactNames() {
        return sharedPreferences.getStringSet(KEY_CONTACTS_NAME, new HashSet<String>());
    }

    public synchronized Set<String> getContactPhones() {
        return sharedPreferences.getStringSet(KEY_CONTACTS_PHONE, new HashSet<String>());
    }
}

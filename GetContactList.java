package cha.com.autodetectsms;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;



import java.util.ArrayList;


public class GetContactList {


/* This method is used to fetch the contact List.Each contact is saved in a arraylist */

    public static ArrayList<ContactDTO> getNumber(ContentResolver cr) {
        ArrayList<String> contactNumbers = new ArrayList<>();
        ArrayList<ContactDTO> contacts=new ArrayList<>();
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,  "UPPER(" + ContactsContract.Contacts.DISPLAY_NAME + ") ASC");

        while (phones.moveToNext()) {

            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));//Contact Name
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));//Contact Number


            if((phoneNumber!=null)&&(name!=null)){ //Check if name and number is not null.

                if(!contactNumbers.contains(phoneNumber)){
                    contactNumbers.add(name);
                    contacts.add(new ContactDTO(name,phoneNumber));// Create a ContactDTO object and add it to arrayList
                    Log.i("Detailed",name+" : "+phoneNumber);
                }

            }


        }
        return contacts;
    }
  

}

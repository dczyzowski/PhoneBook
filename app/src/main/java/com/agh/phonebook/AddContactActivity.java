package com.agh.phonebook;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddContactActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mAuth = FirebaseAuth.getInstance();
        //deklaruje baze danych
        database = FirebaseDatabase.getInstance();
        //referencja do mojej bazy danych - odnosnik gdzie znajduja sie moje dane
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null)
            myRef = database.getReference("kontakty").child(user.getUid());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AutoCompleteTextView contactName = (AutoCompleteTextView)
                        findViewById(R.id.new_contact_name);

                AutoCompleteTextView contactPhoneNumber = (AutoCompleteTextView)
                        findViewById(R.id.new_phone_number);

                EditText contactInfo = (EditText) findViewById(R.id.new_some_info);

                String getContactName = contactName.getText().toString();
                String getContactPhone = contactPhoneNumber.getText().toString();
                String getContactInfo = contactInfo.getText().toString();

                boolean error = false;

                if (getContactName.length() < 4) {
                    contactName.setError("Nazwa jest zbyt krótka");
                    error = true;
                }
                if (getContactPhone.length() < 9) {
                    contactPhoneNumber.setError("Wpisz poprawny numer telefonu");
                    error = true;
                }
                if (getContactInfo.isEmpty()) {
                    getContactInfo = "brak";
                }
                if (!error) {
                    Snackbar.make(view, "Zapisuję nowy kontakt...", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    Contact addContact = new Contact(getContactName, getContactPhone, getContactInfo);
                    myRef.child(getContactName).setValue(addContact);

                    finish();
                }
            }
        });
        setStatusBarTranslucent(true);
    }

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}

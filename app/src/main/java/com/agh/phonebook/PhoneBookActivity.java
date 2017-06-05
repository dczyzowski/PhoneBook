package com.agh.phonebook;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.Manifest.permission.CALL_PHONE;

public class PhoneBookActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseDatabase database;

    private static final int REQUEST_CALL_PHONE = 0;

    String TAG = "State changed";

    String userDisplayName;
    String userDisplayEmail;

    boolean loggedIn;

    TextView userName;
    TextView userEmail;

    ArrayList<Contact> contacts;
    PhoneBookListAdapter listAdapter;

    NavigationView navigationView;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_book);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    loggedIn = true;
                    if (user.getDisplayName() == null)
                        userName.setText("Ustaw swoje imie");
                    else
                        userDisplayName = user.getDisplayName();

                    userDisplayEmail = user.getEmail();
                    userName.setText(userDisplayName);
                    userEmail.setText(userDisplayEmail);

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    loggedIn = false;
                    userName.setText("Zaloguj się");
                    userEmail.setText(" ");
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!loggedIn) {
                    Snackbar.make(view, "Musisz być zalogowany", Snackbar.LENGTH_LONG)
                            .setAction("Zaloguj się!", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(getBaseContext(), LoginActivity.class);
                                    startActivity(i);
                                }
                            }).show();
                } else {
                    Snackbar.make(view, "Dodaj nowy kontakt", Snackbar.LENGTH_LONG)
                            .setAction("Dodaj", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(getBaseContext(), AddContactActivity.class);
                                    startActivity(i);
                                }
                            }).show();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        userName = (TextView) headerView.findViewById(R.id.user_name);
        userEmail = (TextView) headerView.findViewById(R.id.user_email);


        listView = (ListView) findViewById(R.id.list);
        contacts = new ArrayList<>();
        listAdapter = new PhoneBookListAdapter(getBaseContext(),
                R.layout.contact_row, contacts);

        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PhoneBookActivity.this,
                        android.R.style.Theme_Material_Dialog_Presentation);
                builder.setPositiveButton("POŁĄCZ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                                contacts.get(position).getPhoneNumber()));
                        if (ActivityCompat.checkSelfPermission(PhoneBookActivity.this,
                                android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            mayRequestCallPhone();
                            return;
                        }
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setMessage("Numer telefonu: " + contacts.get(position).getPhoneNumber() +
                "\n\nInformacje dodatkowe: " + contacts.get(position).getContactInfo());
                builder.setTitle(contacts.get(position).getContactName());

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private boolean mayRequestCallPhone() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(CALL_PHONE)) {
            Snackbar.make(listView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{CALL_PHONE}, REQUEST_CALL_PHONE);
                        }
                    });
        } else {
            requestPermissions(new String[]{CALL_PHONE}, REQUEST_CALL_PHONE);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL_PHONE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(myRef != null) {

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    contacts.clear();

                    // pobieramy wszystkie dane od nowa gdy zostanie wprowadzona zmiana
                    for (DataSnapshot productsSnapshot : dataSnapshot.getChildren()) {
                        Contact myContact = productsSnapshot.getValue(Contact.class);
                        contacts.add(myContact);
                    }

                    // ponownie generuje listView
                    listAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null)
            myRef = database.getReference("kontakty").child(user.getUid());
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.phone_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            if(loggedIn) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PhoneBookActivity.this, android.R.style.Theme_Material_Dialog_Presentation);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        contacts.clear();
                        listAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setMessage("Nastąpi wylogowanie");
                builder.setTitle("Wyloguj");

                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else{
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

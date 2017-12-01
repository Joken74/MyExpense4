package com.example.jochen.myexpense;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jochen.myexpense.db.MyExpenseOpenHandler;
import com.example.jochen.myexpense.dialogs.DatePickerFragment;
import com.example.jochen.myexpense.model.Expense;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;
import java.util.Locale;

public class CreateNewExpenseActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, DatePickerDialog.OnDateSetListener {

    public Button currentPosition;
    public GoogleMap map;
    private Marker marker;
    private LocationManager locationManager;

    private Expense expense;

    private TextView dueDate;
    private EditText category;
    private EditText amount;
    private EditText description;
    private CheckBox important;
    private Button save;
    private ImageButton removeDate;
    private ImageButton removePosition;

    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_expense);

        this.currentPosition = (Button) findViewById(R.id.currentPosition);

        this.expense = new Expense();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.category = (EditText) findViewById(R.id.category);
        this.amount = (EditText) findViewById(R.id.amount);
        this.description = (EditText) findViewById(R.id.description);
        this.important = (CheckBox) findViewById(R.id.important);

        this.save = (Button) findViewById(R.id.save);
        this.removeDate = (ImageButton) findViewById(R.id.deleteDueDate);
        this.removePosition = (ImageButton) findViewById(R.id.deletePosition);

        this.spinner = (Spinner) findViewById(R.id.spinner);
        String[] items = new String[]{"1", "2", "three"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter1);


        this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE); // einfach abfragen

        if (this.currentPosition != null) {
            this.currentPosition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchPosition();
                }
            });
        }

        this.dueDate = (TextView) findViewById(R.id.dueDateText);
        this.dueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "datePicker");
            }
        });

        // Standard Dialog:
     /*   AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // der Kontext ist this, also die momentan aktive Activity in der wir uns befinden
        builder.setTitle("Sehr Wichtig!!!");
        builder.setMessage("Eine neue Ausgabe wird erstellt.");
        builder.setNeutralButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, final int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show(); */

     // Standard Text-Watcher:
        this.category.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {
                expense.setCategory(editable.toString().length() == 0 ? null : editable.toString());
            }
        });

        this.amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {
                expense.setAmount(editable.toString().length() == 0 ? null : editable.toString());
            }
        });

        this.description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {
                expense.setDescription(editable.toString().length() == 0 ? null : editable.toString());
            }
        });

        this.important.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton compoundButton, final boolean b) {
                expense.setImportant(b);
            }
        });

        this.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if(expense.getCategory() == null || expense.getAmount() == null) { // Amount and Category must be set
                    Toast.makeText(CreateNewExpenseActivity.this, "Fehler beim speichern. Kategorie und Betrag müssen sein!", Toast.LENGTH_SHORT).show();
                    return;
                }

                MyExpenseOpenHandler.getInstance(CreateNewExpenseActivity.this).createExpense(expense);
                finish(); // Activity will be closed when saving was successful.
            }
        });

        this.removePosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeLocation();
            }
        });

        this.removeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeDate();
            }
        });
    }
// Abfrage des ob der Nutzer mit der Positionsweitergabe einverstanden ist --> Manifest.xml permissions
    private void searchPosition() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            this.requestPermission(5);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50, this);
        //von wem wollen wir das update? , wie viel milisekunden bis zum nächsten Update für weniger Batterie , nur bei Bewegung in Metern, listener = location die wir bekommen
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

        expense.setLocation(position);

        if (this.map != null) {

            if(this.marker != null) {
                this.marker.remove();
            }

            this.marker = map.addMarker(new MarkerOptions().position(position));
            this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
        }

        removeListener(); // er soll, sobald Position gefunden, aufhören zu suchen (Energie)


    }

    private void removeListener() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermission(4);
            return;
        }
        this.locationManager.removeUpdates(this); // müssten wieder nach der Permission fragen
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void requestPermission(final int resultCode) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, resultCode); // resultCode = Zahl am Ende ist ein Dialogfeld ob ja oder nein
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {

        switch (requestCode) {
            case 5:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    searchPosition();
                }
                break;
            case 4:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    removeListener();
                }
                break;

        }
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDateSet(final DatePicker datePicker, final int i, final int i1, final int i2) {
        this.dueDate.setText(String.format(Locale.GERMANY, "%02d.%02d.%d", i2, i1+1, i));

        Calendar c = Calendar.getInstance();
        c.set(i, i1+1, i2);

        expense.setDate(c);
    }

    private void removeLocation() {
        if(this.marker != null) {
            this.marker.remove();
        }
        expense.setLocation(null);
    }

    private void removeDate() {
        this.dueDate.setText("");
        this.expense.setDate(null);
    }
}

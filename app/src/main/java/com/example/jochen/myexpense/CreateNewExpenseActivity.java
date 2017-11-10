package com.example.jochen.myexpense;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.jochen.myexpense.dialogs.DatePickerFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CreateNewExpenseActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, DatePickerDialog.OnDateSetListener {

    public Button currentPosition;
    public GoogleMap map;
    private Marker marker;
    private LocationManager locationManager;

    private TextView dueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_expense);

        this.currentPosition = (Button) findViewById(R.id.currentPosition);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
        this.dueDate.setText(String.format("%02d.%02d.%d", i2, i1+1, i));
    }
}

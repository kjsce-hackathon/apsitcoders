package com.apsitcoders.bus.fragment;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.apsitcoders.bus.R;
import com.apsitcoders.bus.TicketActivity;
import com.apsitcoders.bus.model.Direction;
import com.apsitcoders.bus.model.MinMaxLatLong;
import com.apsitcoders.bus.network.DirectionService;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by adityathanekar on 06/10/17.
 */

public class NavigationFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnPolylineClickListener {

    private static final int PLACE_AUTOCOMPLETE_SOURCE_REQUEST_CODE = 1;
    private static final int PLACE_AUTOCOMPLETE_DESTINATION_REQUEST_CODE = 2;

    private GoogleMap mMap;

    @BindView(R.id.source)
    Button source;
    @BindView(R.id.destination)
    Button destination;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.book_ticket)
    Button bookTicket;

    private DirectionService directionService;
    private Place src, dest;
    private Unbinder unbinder;

    private Map<String, MinMaxLatLong> rectangles;
    private List<GeoLocation> busStopList;

    private int stopCount = 0;
    private int fare = 0;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;

    GeoFire geoFire;

    private Polyline previousPolyline;

    private Observer<List<Direction>> directionObserver = new Observer<List<Direction>>() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(List<Direction> value) {
            long minDistance = 0;
            for (Direction direction : value) {
                PolylineOptions option = direction.getPolylineOption();
                Polyline polyline = mMap.addPolyline(option);
                if (minDistance == 0) {
                    minDistance = direction.getDistance();
                    previousPolyline = polyline;
                }
                if (minDistance > direction.getDistance()) {
                    minDistance = direction.getDistance();
                    previousPolyline = polyline;
                }
                rectangles.put(polyline.getId(), direction.getMinMaxLatLong());
            }
            if (previousPolyline != null) {
                previousPolyline.setColor(ContextCompat.getColor(getActivity(), R.color.mapBlue));
                previousPolyline.setZIndex(1);
            }
//            presenter.getTolls(rectangles.get(previousPolyline.getId()));


//            for(Direction direction: value) {
//                mMap.addPolyline(direction.getPolylineOption());
//            }
        }

        @Override
        public void onError(Throwable e) {
            Log.d("Aditya", e.getMessage());
        }

        @Override
        public void onComplete() {

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        MapFragment mapFragment = (MapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        directionService = new DirectionService(getActivity());
        rectangles = new HashMap<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference("stops");
        busStopList = new ArrayList<>();
        geoFire = new GeoFire(ref);
        initView();
    }

    private void initView() {
        source.setOnClickListener(this);
        destination.setOnClickListener(this);
        fab.setOnClickListener(this);
        bookTicket.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude())));
        mMap.setOnPolylineClickListener(this);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void launchPlaceSelectActivity(int requestCode) {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(getActivity());
            startActivityForResult(intent, requestCode);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.source:
                launchPlaceSelectActivity(PLACE_AUTOCOMPLETE_SOURCE_REQUEST_CODE);
//                startActivity(new Intent(getActivity(), BusStopPickerActivity.class));
                break;
            case R.id.destination:
                launchPlaceSelectActivity(PLACE_AUTOCOMPLETE_DESTINATION_REQUEST_CODE);
                break;
            case R.id.fab:
                if(src != null && dest != null) {
                    LatLng sourceLatLng = src.getLatLng(), destLatLng = dest.getLatLng();
                    Log.d("Aditya", sourceLatLng.latitude + "," + sourceLatLng.longitude);
                    directionService
                            .getDirectionObservable(sourceLatLng.latitude + "," + sourceLatLng.longitude, destLatLng.latitude + "," + destLatLng.longitude)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(directionObserver);
                }
                else {
                    Log.d("Aditya", "is null " + (src == null) + " " + (dest == null));
                }
                break;
            case R.id.book_ticket:
                Intent intent = new Intent(getActivity(), TicketActivity.class);
                intent.putExtra(TicketActivity.EXTRA_FARE, fare);
                intent.putExtra(TicketActivity.EXTRA_SOURCE, src.getAddress());
                intent.putExtra(TicketActivity.EXTRA_DESTINATION, dest.getAddress());
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == RESULT_OK) {
                if(requestCode == PLACE_AUTOCOMPLETE_SOURCE_REQUEST_CODE)
                    src = PlaceAutocomplete.getPlace(getActivity(), data);
                else if(requestCode == PLACE_AUTOCOMPLETE_DESTINATION_REQUEST_CODE)
                    dest = PlaceAutocomplete.getPlace(getActivity(), data);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
//                Status status = PlaceAutocomplete.getStatus(this, data);
//                // TODO: Handle the error.
//                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        polyline.setColor(ContextCompat.getColor(getActivity(), R.color.mapBlue));
        polyline.setZIndex(1);
        if (previousPolyline != null) {
            previousPolyline.setColor(ContextCompat.getColor(getActivity(), R.color.mapGray));
            previousPolyline.setZIndex(0);
        }
        previousPolyline = polyline;
        getBusStops(rectangles.get(previousPolyline.getId()));
//        presenter.getTolls(rectangles.get(previousPolyline.getId()));
    }

    private void calculateTicket() {

    }

    private void getBusStops(MinMaxLatLong minMaxLatLong) {
        bookTicket.setVisibility(View.VISIBLE);

        double minLat = minMaxLatLong.getMinLat(), maxLat = minMaxLatLong.getMaxLat();
        double minLong = minMaxLatLong.getMinLong(), maxLong = minMaxLatLong.getMaxLong();

        double midLat = 0.5 * (maxLat - minLat);
        double midLong = 0.5 * (maxLong - minLong);

        double centerLat = minLat + midLat, centerLong = minLong + midLong;

        double radius = Math.sqrt((midLat * midLat) + (midLong * midLong)) + 5;

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(centerLat, centerLong), radius * 5);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                busStopList.add(location);

                LatLng pos = new LatLng(location.latitude, location.longitude);
                if (PolyUtil.isLocationOnPath(pos, previousPolyline.getPoints(), true, 100.0)) {
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).position(new LatLng(location.latitude, location.longitude)));
                    stopCount++;
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(stopCount > 0 && stopCount <= 3)
                    fare = 10;
                else if(stopCount > 3 && stopCount <= 6)
                    fare = 20;
                else if(stopCount > 6 && stopCount <= 9)
                    fare = 30;
                else if(stopCount > 9)
                    fare = 40;
                else
                    fare = 0;
                Toast.makeText(getActivity(), "Fare is: " + fare , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
}

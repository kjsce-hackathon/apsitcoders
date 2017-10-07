package com.apsitcoders.bus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apsitcoders.bus.model.Ticket;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.security.SecureRandom;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TicketActivity extends AppCompatActivity {

    public static final String EXTRA_SOURCE = "com.apsitcoders.bus.SOURCE";
    public static final String EXTRA_DESTINATION = "com.apsitcoders.bus.DESTINATION";
    public static final String EXTRA_FARE = "com.apsitcoders.bus.FARE";

    final Random randomGenerator = new SecureRandom();
    final char[] character = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghigklmnopqrstuvwxyz0123456789".toCharArray();
    final char[] result = new char[16];


    String text2Qr;

    @BindView(R.id.qrCode)
    ImageView qrCode;
    @BindView(R.id.text)
    TextView textView;

    private Unbinder unbinder;

    private String source, destination;
    private int fare;

    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("tickets");

        unbinder = ButterKnife.bind(this);

        Intent intent = getIntent();
        if(intent != null) {
            source = intent.getStringExtra(EXTRA_SOURCE);
            destination = intent.getStringExtra(EXTRA_DESTINATION);
            fare = intent.getIntExtra(EXTRA_FARE, 0);
        }

        for (int i = 0; i < result.length; i++) {
            // picks a random index out of character set > random character
            int randomCharIndex = randomGenerator.nextInt(character.length);
            result[i] = character[randomCharIndex];
        }
        String converttext = String.valueOf(result);
        text2Qr = converttext.trim();
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(text2Qr, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrCode.setImageBitmap(bitmap);
        }
        catch (WriterException e){
            e.printStackTrace();
        }

        reference.child(text2Qr).setValue(new com.apsitcoders.bus.model.Ticket(fare, source, destination, false))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

        reference.child(text2Qr).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Ticket ticket = dataSnapshot.getValue(Ticket.class);
                    if(ticket.isVerified()) {
                       textView.setText("Enjoy your journey");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}

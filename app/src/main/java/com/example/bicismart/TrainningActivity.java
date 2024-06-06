package com.example.bicismart;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TrainningActivity extends AppCompatActivity {
    TextView tvDuracion, tvIntensidad, tvBuzzer, tvSensores, tvMusDin, tvAddress;
    int duracion;
    String intensidad, address;
    boolean enableBuzzer, enableSensor, enableMusDin;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trainning);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvDuracion = findViewById(R.id.tv_duracion);
        tvIntensidad = findViewById(R.id.tv_intensidad);
        tvBuzzer = findViewById(R.id.tv_buzzer);
        tvSensores = findViewById(R.id.tv_Sensores);
        tvMusDin = findViewById(R.id.tv_MusicaDin);
        tvAddress = findViewById(R.id.tvAddress);

        Bundle bundle =getIntent().getExtras();
        address = bundle.getString("Direccion_Bluethoot");
        duracion = bundle.getInt("Duracion");
        intensidad = bundle.getString("Intensidad");
        enableBuzzer = bundle.getBoolean("Buzzer");
        enableSensor = bundle.getBoolean("Sensores");
        enableMusDin = bundle.getBoolean("Musica Dinamica");

        tvDuracion.setText("Duracion: " + duracion);
        tvIntensidad.setText("Intensidad: " + intensidad);
        tvBuzzer.setText("Buzzer: " + enableBuzzer);
        tvSensores.setText("Sensores: " + enableSensor);
        tvMusDin.setText("Musica Dinamica: " + enableMusDin);
        tvAddress.setText("Address: " + address);

    }
}
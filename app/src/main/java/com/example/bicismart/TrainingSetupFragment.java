package com.example.bicismart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class TrainingSetupFragment extends Fragment
{

    RadioButton btnTime, btnMeters;
    private TextView tvTrainningParameter;
    private EditText etTrainningParameter;
    private Spinner spItensity;
    Button btnStart;

    private final String[] intensidades = new String[]
            {
                    "Baja",
                    "Media",
                    "Alta",
            };

    private static String address = null;

    private boolean enableBuzzer = true;
    private boolean enableSensor = true;
    private boolean enableDinMusic = true;

    public TrainingSetupFragment()
    {
        // Required empty public constructor
    }

    public static TrainingSetupFragment newInstance(String address)
    {
        TrainingSetupFragment fragment = new TrainingSetupFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Direccion_Bluetooth", address);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            address = getArguments().getString("Direccion_Bluetooth");
        }

        getParentFragmentManager().setFragmentResultListener("datos", this, new FragmentResultListener()
        {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result)
            {
                enableBuzzer = result.getBoolean("Buzzer");
                enableSensor = result.getBoolean("Control_Sensors");
                enableDinMusic = result.getBoolean("Musica_Dinamica");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tr_fragment_training_setup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        btnTime = view.findViewById(R.id.btn_tiempo);
        btnMeters = view.findViewById(R.id.btn_metros);
        tvTrainningParameter = view.findViewById(R.id.tvEntrenamiento);
        etTrainningParameter = view.findViewById(R.id.et_parametro_Entrenamiento);
        btnStart = view.findViewById(R.id.btn_start);
        spItensity = view.findViewById(R.id.spinner_Intensidad);

        ArrayAdapter<String> adaptador = new ArrayAdapter<>(requireActivity().getApplicationContext(), android.R.layout.simple_spinner_item, intensidades);
        spItensity.setAdapter(adaptador);

        Bundle args = getArguments();
        address = args.getString("Direccion_Bluetooth", address);
        showToast("Adress: " + address);

        btnTime.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v)
            {
                tvTrainningParameter.setText("Ingresar Tiempo (en Minutos)");
            }
        });

        btnMeters.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                tvTrainningParameter.setText("Ingresar Metros");
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String str = etTrainningParameter.getText().toString();
                if(str.isEmpty())
                    showToast("Ingresar Parametros");
                else
                {
                    showToast("Parametros: Duracion(Metros o Minutos): " + str +
                            "\nIntensidad: " + spItensity.getSelectedItem().toString() +
                            "\nBuzzer: " + enableBuzzer +
                            "\nSensores: " + enableSensor +
                            "\nMusica Dinamica: " + enableDinMusic +
                            "\nPor Tiempo: " + btnTime.isChecked());

                    Intent i = new Intent(getActivity(), TrainningActivity.class);
                    i.putExtra("Direccion_Bluethoot", address);
                    i.putExtra("Duracion", Integer.parseInt(str));
                    i.putExtra("Por Tiempo", btnTime.isChecked());
                    i.putExtra("Intensidad", spItensity.getSelectedItem().toString());
                    i.putExtra("Buzzer", enableBuzzer);
                    i.putExtra("Sensores", enableSensor);
                    i.putExtra("Musica Dinamica", enableDinMusic);
                    startActivity(i);
                }
            }
        });
    }

    private void showToast(String message)
    {
        Toast.makeText(requireActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
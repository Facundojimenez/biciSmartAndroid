package com.example.bicismart;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrainingSetupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrainingSetupFragment extends Fragment {

    private RadioButton btnTime, btnMeters;
    private TextView tvTrainningParameter, tvItensity;
    private EditText etTrainningParameter;
    private Spinner spItensity;
    private Button btnStart;
    private String[] intensidades = new String[]
            {
                    "Baja",
                    "Media",
                    "Alta",
            };

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TrainingSetupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrainingSetupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrainingSetupFragment newInstance(String param1, String param2) {
        TrainingSetupFragment fragment = new TrainingSetupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_training_setup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnTime = view.findViewById(R.id.btn_tiempo);
        btnMeters = view.findViewById(R.id.btn_metros);
        tvTrainningParameter = view.findViewById(R.id.tvEntrenamiento);
        tvItensity = view.findViewById(R.id.tvIntensidad);
        etTrainningParameter = view.findViewById(R.id.et_parametro_Entrenamiento);
        btnStart = view.findViewById(R.id.btn_start);
        spItensity = view.findViewById(R.id.spinner_Intensidad);

        ArrayAdapter<String> adaptador = new ArrayAdapter<>(requireActivity().getApplicationContext(), android.R.layout.simple_spinner_item, intensidades);
        spItensity.setAdapter(adaptador);
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvTrainningParameter.setText("Ingresar Tiempo (en Minutos)");
            }
        });

        btnMeters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvTrainningParameter.setText("Ingresar Metros");
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = etTrainningParameter.getText().toString();
                if(str.isEmpty())
                    showToast("Ingresar Parametros");
                else
                    showToast("Parametros: Duracion(Metros o Minutos): " +
                        str + "\nIntensidad: " +
                        spItensity.getSelectedItem().toString());
            }
        });

    }
    private void showToast(String message) {
        Toast.makeText(requireActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
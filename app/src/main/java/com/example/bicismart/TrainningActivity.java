package com.example.bicismart;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TrainningActivity extends AppCompatActivity
{
    TextView tvDuracion, tvIntensidad, tvBuzzer, tvSensores, tvMusDin, tvAddress, tvTipoEntrenamiento;
    static TextView tvEstado;
    int duracion;
    String intensidad;
    boolean enableBuzzer, enableSensor, enableMusDin, forTime;
    static Button btnRestart;
    static boolean TRAINING_FINISHED;
    Handler bluetoothIn;
    final int handlerState = 0; //used to identify handler message
    private SingletonSocket mSocket;

    private final StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // String for MAC address del Hc05
    private static String address = null;

    @SuppressLint({"SetTextI18n", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trainning);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TRAINING_FINISHED = false;
        tvDuracion = findViewById(R.id.tv_duracion);
        tvIntensidad = findViewById(R.id.tv_intensidad);
        tvBuzzer = findViewById(R.id.tv_buzzer);
        tvSensores = findViewById(R.id.tv_Sensores);
        tvMusDin = findViewById(R.id.tv_MusicaDin);
        tvAddress = findViewById(R.id.tvAddress);
        tvTipoEntrenamiento = findViewById(R.id.tv_tipoEntrenamiento);
        tvEstado = findViewById(R.id.tv_estado);
        btnRestart = findViewById(R.id.btn_restart);
        btnRestart.setText("Cancelar Entrenamiento");

        Bundle bundle =getIntent().getExtras();
        address = bundle.getString("Direccion_Bluethoot");
        duracion = bundle.getInt("Duracion");
        intensidad = bundle.getString("Intensidad");
        enableBuzzer = bundle.getBoolean("Buzzer");
        enableSensor = bundle.getBoolean("Sensores");
        enableMusDin = bundle.getBoolean("Musica Dinamica");
        forTime = bundle.getBoolean("Por Tiempo");

        tvTipoEntrenamiento.setText("Por Tiempo: " + forTime);
        tvDuracion.setText("Duracion: " + duracion);
        tvIntensidad.setText("Intensidad: " + intensidad);
        tvBuzzer.setText("Buzzer: " + enableBuzzer);
        tvSensores.setText("Sensores: " + enableSensor);
        tvMusDin.setText("Musica Dinamica: " + enableMusDin);
        tvAddress.setText("Address: " + address);

        btnRestart.setOnClickListener(new View.OnClickListener()
        {
          @Override
          public void onClick(View v)
          {
              Intent intent = new Intent(TrainningActivity.this, PreTrainingActivity.class);
              intent.putExtra("Direccion_Bluethoot", address);
              if (!TRAINING_FINISHED){
                  mConnectedThread.write("CANCEL");
              }
              startActivity(intent);
              finish();
          }
        });

        mSocket = SingletonSocket.getInstance("", null);

        //defino el Handler de comunicacion entre el hilo Principal  el secundario.
        //El hilo secundario va a mostrar informacion al layout atraves utilizando indeirectamente a este handler
        if(!mSocket.handleWasCreated())
        {
            bluetoothIn = Handler_Msg_Hilo_Principal();
            mSocket.setHandle(bluetoothIn);
            mSocket.handleCreated();
        }
        else
        {
            bluetoothIn = mSocket.getHandler();
        }

        mConnectedThread = new ConnectedThread(mSocket.getBtSocket());
        mConnectedThread.start();

        if(forTime)
        {
            mConnectedThread.write(duracion + " 0 " + (enableMusDin? 1:0));
        }
        else
        {
            mConnectedThread.write("0 " + duracion + " " + (enableMusDin? 1:0));
        }

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //no hacer nada, desactivar back button
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    //Cada vez que se detecta el evento OnResume se establece la comunicacion con el HC05, creando un
    //socketBluethoot
    protected void onResume()
    {
        super.onResume();
        mConnectedThread = new ConnectedThread(mSocket.getBtSocket());
        mConnectedThread.start();
    }

    private void showToast(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //Handler que sirve que permite mostrar datos en el Layout al hilo secundario
    private Handler Handler_Msg_Hilo_Principal ()
    {
        return  new Handler(Looper.getMainLooper())
        {
            public void handleMessage(@NonNull android.os.Message msg)
            {
                //si se recibio un msj del hilo secundario
                if (msg.what == handlerState)
                {
                    //voy concatenando el msj
                    String readMessage = (String) msg.obj;
                    recDataString.append(readMessage);
                    int endOfLineIndex = recDataString.indexOf("\r\n");
                    //showToast("Pre mensaje: " + readMessage);
                    //cuando recibo toda una linea la muestro en el layout
                    if (endOfLineIndex > 0)
                    {
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);
                        switch(dataInPrint){
                            case "WAITTING":
                                tvEstado.setText("Entrenamiento listo para comenzar");
                                break;
                            case "PAUSED":
                                tvEstado.setText("Entrenamiento pausado");
                                break;
                            case "ENDED":
                                //las estadisticas al final podrian llegar a pisar este texto
                                tvEstado.setText("Entrenamiento Finalizado");
                                btnRestart.setText("Reiniciar");
                                //si esta en true no se manda CANCEL al arduino al volver al preentrenamiento.
                                TRAINING_FINISHED = true;
                                break;
                            case "STARTED":
                            case "RESUMED":
                                //no se si se va a llegar a ver este texto porque los datos del lcd lo pisan
                                tvEstado.setText("Entrenamiento en Curso");
                                break;
                            case "Sad Music":
                            case "Neutral Music":
                            case "Motivational Music":
                            case "NEXT":
                            case "PLAY":
                            case "STOP":
                            default:
                                //case VOL XX
                                if(dataInPrint.matches("VOL [0-9]+")){
                                    int volume = Integer.parseInt(dataInPrint.split(" ")[1]);
                                    //manejar aca el volumen
                                }
                                else {
                                    tvEstado.setText(dataInPrint);
                                }
                                break;
                        }
                        //showToast("Mensaje: " + dataInPrint);
                        recDataString.delete(0, recDataString.length());
                    }
                }
            }
        };

    }

    //******************************************** Hilo secundario del Activity**************************************
    //*************************************** recibe los datos enviados por el HC05**********************************

    private class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //Constructor de la clase del hilo secundario
        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try
            {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e)
            {

            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        //metodo run del hilo, que va a entrar en una espera activa para recibir los msjs del HC05
        public void run()
        {
            byte[] buffer = new byte[256];
            int bytes;

            //el hilo secundario se queda esperando mensajes del HC05
            while (true)
            {
                try
                {
                    //se leen los datos del Bluethoot
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    //se muestran en el layout de la activity, utilizando el handler del hilo
                    // principal antes mencionado
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e)
                {
                    break;
                }
            }
        }


        //write method
        public void write(String input)
        {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try
            {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e)
            {
                //if you cannot write, close the application
                showToast("La conexion fallo");
                finish();
            }
        }
    }
}
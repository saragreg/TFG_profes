package com.example.tfg_profes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class RegistroUSuario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);
    }

    public void onclick_reg(View v){
        TextView usu = findViewById(R.id.usuario);
        String usuInt = usu.getText().toString();
        System.out.println("usuario introducido: "+usuInt);
        TextView contra = findViewById(R.id.contraseña);
        String contraInt = contra.getText().toString();
        System.out.println("contra introducido: "+contraInt);

        TextView nom = findViewById(R.id.nombre);
        String nomInt = nom.getText().toString();

        TextView tel = findViewById(R.id.editTextPhone);
        String telInt = tel.getText().toString();

        Data inputData = new Data.Builder()
                .putString("tipo", "registroUsu")
                .putString("usu",usuInt)
                .putString("contra",contraInt)
                .putString("nom",nomInt)
                .putString("tel",telInt)
                .build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionBDWebService.class).setInputData(inputData).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            Toast.makeText(getApplicationContext(), "Se ha registrado correctamente", Toast.LENGTH_SHORT).show();
                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (!task.isSuccessful()) {
                                        return;
                                    }
                                    String token = task.getResult();
                                    System.out.println("el token:" + token);
                                    subirToken(token,usuInt);
                                    subirProfe(usuInt);

                                }
                            });
                            Intent intent = new Intent(RegistroUSuario.this, Menu.class);
                            intent.putExtra("usuario",usuInt);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(), "Usuario no válido", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);


    }

    private void subirProfe(String usuInt) {

    }

    public void subirToken(String token, String usu){
        Data inputData = new Data.Builder()
                .putString("usuario", usu)
                .putString("token",token)
                .build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(conexionBDinsertToken.class).setInputData(inputData).build();
        WorkManager.getInstance(getApplicationContext()).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {

                        }
                    }
                });
        WorkManager.getInstance(getApplicationContext()).enqueue(otwr);

    }
}
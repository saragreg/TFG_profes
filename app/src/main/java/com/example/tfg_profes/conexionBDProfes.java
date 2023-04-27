package com.example.tfg_profes;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class conexionBDProfes extends Worker {
    private String URL_BASE = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/sgarcia216/WEB/";
    private String usua="";
    private String nombre="";
    private String precio="";
    private String punt="";

    public conexionBDProfes(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String tipo = getInputData().getString("tipo");

        switch (tipo) {
            case "infoLista":
                infoLista();
                Data resultados = new Data.Builder()
                        .putString("usu",usua)
                        .putString("nombre",nombre)
                        .putString("precio",precio)
                        .putString("punt",punt)
                        .build();
                return Result.success(resultados);
            default:
                return Result.failure();
        }

    }


    private void infoLista() {
        String url = URL_BASE + "infoListaProf.php";
        HttpURLConnection urlConnection = null;
        try {
            URL requestUrl = new URL(url);
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.setRequestMethod("GET");

            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                String line, result = "";

                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();

                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {
                    usua = usua+jsonArray.getJSONObject(i).getString("usu")+",";
                    nombre = nombre+jsonArray.getJSONObject(i).getString("nombre")+",";
                    precio = precio+jsonArray.getJSONObject(i).getString("precio")+",";
                    punt = punt+jsonArray.getJSONObject(i).getString("punt")+",";
                }


            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}

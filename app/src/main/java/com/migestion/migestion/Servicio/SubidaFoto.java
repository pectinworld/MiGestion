package com.migestion.migestion.Servicio;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.migestion.migestion.MainActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class SubidaFoto extends AsyncTask<Void, Void, String> {

    public static final String FORM_FILE_UBICACION = "nuevoFotoUbicacion";
    private static final String LOG = "FilesUploadingTask: ";
    String PHP_FILE_UPLOADING = MainActivity.baseUrlFoto;

    private String lineEnd = "\r\n";
    private String twoHyphens = "--";
    private String boundary = "----WebKitFormBoundary9xFB2hiUhzqbBQ4M";
    private int bytesRead, bytesAvailable, bufferSize;
    private byte[] buffer;
    private int maxBufferSize = 1 * 1024 * 1024;
    private String filePath;

    public SubidaFoto(String filePath) {
        this.filePath = filePath;
    }

    public static String readStream(InputStream inputStream) throws IOException {
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }

        return buffer.toString();
    }

    @SuppressLint({"LongLogTag", "WrongThread"})
    @Override
    protected String doInBackground(Void... params) {
        // Результат выполнения запроса, полученный от сервера
        String result = null;
        File directory = new File(MainActivity.rutaGeneral + "/");

        // Получаю список всех файлов из папки.
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                result = subidaFotos(String.valueOf(file)); // Загружаю файл на сервер
                Log.d("!!!!!!!!!!!!!!!!!!!!!!!", "Ответ на загрузку фото: " + result);
            }
        }
        return result;
    }

    protected String subidaFotos(String fotoNombre) {
        String respuesta = "";

        try {
            URL uploadUrl = new URL(PHP_FILE_UPLOADING);
            if (fotoNombre != null) {
                uploadUrl = new URL(PHP_FILE_UPLOADING);
            }

            HttpURLConnection connection = (HttpURLConnection) uploadUrl.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            connection.connect();

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            if (fotoNombre != null) {
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + FORM_FILE_UBICACION + "\"; filename=\"" + fotoNombre + "\"" + lineEnd);

            }

            outputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
            outputStream.writeBytes(lineEnd);
            FileInputStream fileInputStream = new FileInputStream(new File(fotoNombre));
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            int serverResponseCode = connection.getResponseCode();

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();

            if (serverResponseCode == 200) {
                respuesta = readStream(connection.getInputStream());

                File myFile = new File(fotoNombre);

                if (respuesta.equals("success")) { // Если с сервера приходит положительный ответ о записи файла, то удаляю файл с телефона
                    myFile.delete();
                    Log.d(LOG, "Удаляем фото!");
                }

            } else {
                respuesta = readStream(connection.getErrorStream());
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return respuesta;
    }
}

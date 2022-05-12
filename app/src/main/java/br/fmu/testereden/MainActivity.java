package br.fmu.testereden;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private String SERVER = "10.180.197.9";
    private static int PORT_TCP = 9009;
    private String msgHTTP = "";
    private String msgTCP = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
//        msgHTTP = "texto teste";
//
//        ThreadHTTP http = new ThreadHTTP();
//        http.start();

        msgTCP = "texto teste TCP";

        ThreadTCP tcp = new ThreadTCP();
        tcp.start();
    }


    class ThreadHTTP extends Thread {
        private String leitura = "";
        private String error = "";

        public void run() {
            try {
                URL urlObj = new URL("http://" + SERVER + ":8080/TesteRede/test");
                HttpURLConnection httpConn = (HttpURLConnection) urlObj.openConnection();

                httpConn.setDoInput(true);
                httpConn.setDoOutput(true);
                httpConn.setUseCaches(false);
                httpConn.setRequestMethod("POST");
                httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpConn.setRequestProperty("Content-Language", "pt-BR");
                httpConn.setRequestProperty("Accept", "application/octet-stream");
                httpConn.setRequestProperty("Connection", "close");

                OutputStream os = httpConn.getOutputStream();

                String message = "word=" + msgHTTP;
                os.write(message.getBytes());
                os.close();

                String response = "resposta + " + httpConn.getResponseMessage();

                DataInputStream dis = new DataInputStream(httpConn.getInputStream());

                leitura = dis.readUTF();

                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        System.out.println("Http received: " + leitura);
                    }
                });

            } catch (Exception e) {

                error = e.getMessage();

                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        System.out.println("Http Error: " + error);
                    }
                });
            }

        }//end of run method
    }//end of ThreadHTTP

    class ThreadTCP extends Thread{

        private Socket socket;
        private DataInputStream in;
        private DataOutputStream out;
        private String msgResponse = "";
        private String error = "";

        public void run(){
            try {
                msgTCP = "texto teste TCP";

                socket = new Socket(SERVER, PORT_TCP);

                out = new DataOutputStream(socket.getOutputStream());
                in = new DataInputStream(socket.getInputStream());

                out.writeUTF(msgTCP);
                out.flush();

                msgResponse = in.readUTF();

                MainActivity.this.runOnUiThread( new Runnable() {
                    public void run() {
                        System.out.println("TCP Socket received: " + msgResponse);
                    }
                });

                out.close();
                in.close();
                socket.close();

            } catch (Exception e) {
                error = e.getMessage();

                MainActivity.this.runOnUiThread( new Runnable() {
                    public void run() {
                        System.out.println("TCP Socket error: " + error);
                    }
                });
            }
        }//end of run
    }//end of ThreadTCP


}
package homework3.Client;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;


public class ClientReceiver extends Thread {
    private BufferedReader serverReader;
    private Gson gson;
    private Message message;
    private boolean alive=true;
    public ClientReceiver(BufferedReader serverReader,Gson gson) {
        this.serverReader = serverReader;
        this.gson=gson;
    }

    @Override
    public void run() {
        try {
            while (alive){
                //Читаем сообщения с потока
                String inputMessage = serverReader.readLine();
                message = gson.fromJson(inputMessage, Message.class);
                //Выводим сообщение на консоль
                inputMessage = message.getSender()+": "+message.getBody();
                System.out.println(inputMessage);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void stopReceiver(){alive=false;}

}

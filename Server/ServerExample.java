package homework3.Server;
/**
Многопоточный чат клиент c возможностью отправки сообщений в личку
 */
import com.google.gson.Gson;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerExample {
    //Список клиентов
    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
    //Уникальные id клиентов(Временные имена клиентов)
    private int id;
    //Класс который отправляет сообщения всем клиентам в отдельном потоке
    public MultipleSenderWorker multiSendWorker;
    public Gson gson=new Gson();

    public static void main(String[] args) {
        new ServerExample().start();
    }

    private void start() {
        try {
            //Создание сокета
            ServerSocket serverSocket = new ServerSocket(7071);


            multiSendWorker = new MultipleSenderWorker(this,clients);
            multiSendWorker.start();

            String serverMessage="Сервер запущен...";
            //Отправка сообщения всем пользователям, где аргументы класса Message - (Sender,Receiver,Body)
            multiSendWorker.addMessage(new Message("Сервер",null,serverMessage));

            //Принимаем клиентский сокет, отправляем его обрабатываться в отдельный поток, добавляем этот поток
            // в коллекцию. Затем возвращаемся назад и ждем нового клиента.
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(this, clientSocket,id++);
                clientHandler.start();
                clients.add(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Метод отправки личного сообщения для клиента
    public void sendToClient(Message message) {
        //Перебираем коллекцию и ищем сходство поля Получатель(getReceiver()) в объекте message и
        // имени клиента (getNickName())
        for (ClientHandler c : clients) {
            if (c.getNickName().equals(message.getReceiver())) {
                //Добавление сообщения в "приоритетную очередь" для конкретного клиента
                c.sendMessage(message.getSender(),message.getReceiver(),message.getBody());
            }
        }

    }
    //Проведение необходимых процедур для отключения клиента
    public void disconnectClient(ClientHandler clientHandler) {
        //Удаления клиента из коллекции
        clients.remove(clientHandler);
    }
    //Парсинг сообщения из Json-a и отправка его нужным пользователям
    public void parseMessage(String inputMessage, ClientHandler clientHandler){
        //Получение объекта из строки Json-a
        Message message = gson.fromJson(inputMessage, Message.class);
        //Вручную задаем Отправителя (setSender)т.к. объект Client не знает своего собственного имени
        message.setSender(clientHandler.getNickName());

        //Если поле Получателя пустое - отправляем всем, иначе - конкретному клиенту
        if(message.getReceiver()==null){
            multiSendWorker.addMessage(message);
        }
        else{
            sendToClient(message);
        }

    }
    //Проверка уникальности имени. Если имя свободно, то возвращается true
    public boolean isNameFree(String name){
        for (ClientHandler c : clients) {
            //Сравниваем имена ClientHandler-ов с данным именем
            if (c.getNickName().equals(name)) {
                return false;
            }
        }

        return true;
    }
    //Вывод сообщения на консоль
    public void printMessage(String message){
        System.out.println(message);
    }

}

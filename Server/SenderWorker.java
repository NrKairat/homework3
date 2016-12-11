package homework3.Server;
import com.google.gson.Gson;

import java.io.PrintWriter;
import java.util.PriorityQueue;

public class SenderWorker extends Thread {
    private PriorityQueue<Message> queue;
    private PrintWriter writer;
    private boolean alive = true;
    private Gson gson;

    public SenderWorker(PrintWriter writer, Gson gson) {
        this.writer = writer;
        queue = new PriorityQueue<Message>();
        this.gson = gson;
    }

    @Override
    public void run() {

        while (alive) {
            //Проверка очереди на наличие сообщений
            if (!queue.isEmpty()) {
                //Отправка клиенту сообщения
                Message message = queue.poll();
                String inputString = gson.toJson(message);
                writer.println(inputString);
                writer.flush();
            } else {
                Thread.yield();
            }
        }
    }

    public void addMessage(Message message) {
        queue.add(message);
    }

    public void stopWorker() {
        alive = false;
    }
}

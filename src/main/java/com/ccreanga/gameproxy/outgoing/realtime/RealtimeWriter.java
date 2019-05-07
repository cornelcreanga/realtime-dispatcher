package com.ccreanga.gameproxy.outgoing.realtime;

import com.ccreanga.gameproxy.Customer;
import com.ccreanga.gameproxy.outgoing.message.MessageIO;
import com.ccreanga.gameproxy.outgoing.message.server.ServerMsg;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RealtimeWriter implements Runnable {

    private Customer customer;
    private Socket socket;
    private BlockingQueue<ServerMsg> messages;
    private volatile boolean stopped = false;

    public RealtimeWriter(Customer customer, Socket socket, BlockingQueue<ServerMsg> messages) {
        this.customer = customer;
        this.socket = socket;
        this.messages = messages;
    }

    @Override
    public void run() {
        while (!stopped) {
            try {
                ServerMsg message = messages.take();
                log.trace("Consumed the message type {} from the queue", message.getType());
                OutputStream out = socket.getOutputStream();
                MessageIO.serializeServerMsg(message,out);
                out.flush();
                log.trace("Wrote the message type {} to customer {}", message.getType(), customer.getName());
            } catch (Exception e) {
                //todo handle exception
            }

        }
    }

    public void stop(boolean stopped) {
        this.stopped = stopped;
    }
}

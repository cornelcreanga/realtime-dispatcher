package com.ccreanga.gameproxy.outgoing.realtime;

import static com.ccreanga.gameproxy.outgoing.message.client.ClientMsg.LOGIN;
import static com.ccreanga.gameproxy.outgoing.message.client.ClientMsg.LOGOUT;

import com.ccreanga.gameproxy.Customer;
import com.ccreanga.gameproxy.outgoing.handlers.LoginHandler;
import com.ccreanga.gameproxy.outgoing.handlers.LogoutHandler;
import com.ccreanga.gameproxy.outgoing.message.MessageIO;
import com.ccreanga.gameproxy.outgoing.message.client.*;
import com.google.common.util.concurrent.Striped;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class RealtimeConnectionProcessor {

    private static Striped<ReadWriteLock> stripedLock = Striped.lazyWeakReadWriteLock(100);

    private LoginHandler loginHandler;
    private LogoutHandler logoutHandler;


    public void handleConnection(Socket socket) throws Exception {
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();
        Customer customer = null;
        //add it to register section
        while (true) {
            Optional<ClientMsg> optional = MessageIO.deSerializeClientMsg(in);
            if (optional.isEmpty()){
                return;
            }
            ClientMsg msg = optional.get();
            log.trace("message type {}",msg.getType());
            switch (msg.getType()) {
                case LOGIN:{
                    Optional<Customer> optionalCustomer = loginHandler.handle(socket,(LoginMsg) msg);
                    if (optionalCustomer.isPresent())
                        customer = optionalCustomer.get();
                    else{
                        throw new AuthorizationException();//todo
                    }
                    break;
                }
                case LOGOUT:{
                    logoutHandler.handle(socket,customer,(LogoutMsg) msg);
                    break;
                }
                default: {
                    //todo - write something back on the socket
                    throw new MalformedException("invalid message type " + msg.getType(), "BAD_MESSAGE_TYPE");
                }
            }
            out.flush();

        }
    }

}
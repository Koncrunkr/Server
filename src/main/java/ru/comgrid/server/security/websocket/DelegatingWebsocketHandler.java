package ru.comgrid.server.security.websocket;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

public class DelegatingWebsocketHandler extends WebSocketHandlerDecorator{
    public DelegatingWebsocketHandler(WebSocketHandler webSocketHandler){
        super(webSocketHandler);
    }

    @Override
    public boolean supportsPartialMessages(){
        return true;
    }

}

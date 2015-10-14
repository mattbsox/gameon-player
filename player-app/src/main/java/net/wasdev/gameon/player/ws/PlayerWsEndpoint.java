/*******************************************************************************
 * Copyright (c) 2015 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.wasdev.gameon.player.ws;

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Level;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * WebSocket endpoint: This is the bridge between the player's view
 * in the browser, and the rest of the system.
 */
@ServerEndpoint(value = "/ws")
public class PlayerWsEndpoint {

    /**
     * Called when a new connection has been established.
     *
     * @param session
     * @param ec
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig ec) {
        Log.log(Level.FINE, this, "I'm open!");

        //Guessing here..
        session.getUserProperties().put("userName", "player id!");
        session.getUserProperties().put("authToken", "player id!");
    }

    /**
     * Called when the connection is closed (cleanup)
     * @param session
     * @param reason
     */
    @OnClose
    public void onClose(Session session, CloseReason reason) {
        Log.log(Level.FINE, this, "I'm closed!");
    }

    /**
     * Called when a new message is received on this socket: could be going
     * in either direction. We need to do some introspection to understand
     * what message we've received, and in which direction it should go.
     *
     * @param message
     * @param session
     * @throws IOException
     */
    @OnMessage
    public void receiveMessage(String message, Session session) throws IOException {
        // Called when a message is received.
        if ("stop".equals(message)) {
            Log.log(Level.FINE, this, "I was asked to stop, " + this);
            session.close();
        } else {
            Log.log(Level.FINE, this, "I got a message: " + message);
            // Send something back to the client for feedback
            session.getBasicRemote().sendText("server received:  " + message);
        }
    }

    @OnError
    public void onError(Throwable t) {
        // (lifecycle) Called if/when an error occurs and the connection is
        // disrupted
        Log.log(Level.FINE, this, "oops: " + t);
    }

    /**
     * Simple text based broadcast.
     *
     * @param session
     * @param id
     * @param message
     */
    void broadcast(Session session, int id, String message) {
        // Look, Ma! Broadcast!
        // Easy as pie to send the same data around to different sessions.
        for (Session s : session.getOpenSessions()) {
            try {
                if (s.isOpen()) {
                    s.getBasicRemote().sendText(message);
                }
            } catch (IOException e) {
                tryToClose(s);
            }
        }
    }

    /**
     * Try to close a session (usually once an error has already occurred).
     *
     * @param c
     */
    private final void tryToClose(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e1) {
            }
        }
    }
}

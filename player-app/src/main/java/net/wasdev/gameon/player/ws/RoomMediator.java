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

import javax.websocket.CloseReason;
import javax.websocket.Session;

/**
 *
 */
public interface RoomMediator {

	/**
	 * Route to room or to player depending on routing.
	 * @param routing Array of 3 elements: (room|player):(roomId|playerId):content
	 */
	void route(String[] routing);

	/**
	 * @param playerSession
	 * @param lastmessage
	 */
	boolean subscribe(PlayerConnectionMediator playerSession, long lastmessage);

	/**
	 * @param playerSession
	 */
	void unsubscribe(PlayerConnectionMediator playerSession);

	/**
	 * @return
	 */
	String getId();

	/**
	 * Called when the room-side of the connection is closed.
	 * @param reason Why the connection was closed.
	 */
	void disconnect(CloseReason reason);

	/**
	 * @param session
	 * @return
	 */
	static RoomMediator getRoom(Session session) {
		return (RoomMediator) session.getUserProperties().get(RoomMediator.class.getName());
	};

	static void setRoom(Session session, RoomMediator room) {
		session.getUserProperties().put(RoomMediator.class.getName(), room);
	}
}

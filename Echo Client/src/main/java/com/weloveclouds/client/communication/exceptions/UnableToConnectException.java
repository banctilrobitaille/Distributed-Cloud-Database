package main.java.com.weloveclouds.client.communication.exceptions;

import main.java.com.weloveclouds.client.communication.models.RemoteServer;

/**
 * Created by Benoit on 2016-10-21.
 */
public class UnableToConnectException extends ClientSideException {
    public UnableToConnectException(RemoteServer remoteServer) {
        super(String.format("Unable to connect to server %s on port: %d",
                remoteServer.getIpAddress(), remoteServer.getPort()));
    }
}
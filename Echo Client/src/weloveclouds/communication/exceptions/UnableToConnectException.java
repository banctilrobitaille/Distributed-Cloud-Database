package weloveclouds.communication.exceptions;

import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * @author Benoit, Benedek
 */
public class UnableToConnectException extends ClientSideException {

  private static final long serialVersionUID = 9181363300796566827L;

  public UnableToConnectException(ServerConnectionInfo remoteServer) {
    super(String.format("Unable to connect to server %s on port: %d", remoteServer.getIpAddress(),
        remoteServer.getPort()));
  }
  
  public UnableToConnectException(String message){
    super(message);
  }
}

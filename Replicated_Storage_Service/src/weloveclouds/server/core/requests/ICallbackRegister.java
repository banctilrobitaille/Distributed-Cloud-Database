package weloveclouds.server.core.requests;

/**
 * Represents a callback register which stores different callbacks and can execute.
 * 
 * @author Benedek
 */
public interface ICallbackRegister {

    void registerCallback(Runnable callback);

}
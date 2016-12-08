package weloveclouds.kvstore.models.messages;

import weloveclouds.hashing.models.RingMetadata;
import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.server.models.replication.HashRangesWithRoles;

/**
 * Represents an administrative message between the ECS and the KVServer.
 */
public interface IKVAdminMessage {

    public enum StatusType {
        INITKVSERVER, /* Initialize the server with the context - request */
        START, /* StartNode the server for the client - request */
        STOP, /* Stop the server, but keep it alive - request */
        SHUTDOWN, /* Shut down the server - request */
        LOCKWRITE, /* Lock the write operation on the server - request */
        UNLOCKWRITE, /* Unlock the write lock on the server - request */
        MOVEDATA, /* Move range of the data from one server to another - request */
        UPDATE, /* Update the metadata structure - request */
        RESPONSE_SUCCESS, /* Request was executed successfully. */
        RESPONSE_ERROR /* There was an error during the execution. */
    }

    /**
     * @return a status string that is used to identify request types, response types and error
     *         types associated to the message
     */
    public StatusType getStatus();

    /**
     * @return the ring metadata parts (<IP, port, <range, role>>) for each server
     */
    public RingMetadata getRingMetadata();

    /**
     * @return the ip+port+hash range with role in which the hash values of keys of the entries have
     *         to be
     */
    public RingMetadataPart getTargetServerInfo();

    /**
     * @return the hash ranges which are managed by the addressed server, together with the roles
     *         the respective server has for each range
     */
    public HashRangesWithRoles getManagedHashRangesWithRole();

    /**
     * @return if the message is a response then the message text can be obtained here
     */
    public String getResponseMessage();
}

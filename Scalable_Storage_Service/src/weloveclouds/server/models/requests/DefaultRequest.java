package weloveclouds.server.models.requests;

import weloveclouds.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVMessage;

/**
 * An unrecognized request to the data access layer.
 * 
 * @author Benedek
 */
public class DefaultRequest implements IDataServiceRequest {

    private String key;
    private String errorMessage;

    public DefaultRequest(String key, String errorMessage) {
        this.key = key;
        this.errorMessage = errorMessage;
    }

    @Override
    public KVMessage execute() {
        return new KVMessage.KVMessageBuilder().status(StatusType.DELETE_ERROR).key(key)
                .value(errorMessage).build();
    }

}

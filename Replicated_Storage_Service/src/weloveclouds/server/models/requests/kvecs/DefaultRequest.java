package weloveclouds.server.models.requests.kvecs;

import static weloveclouds.server.models.requests.kvecs.utils.KVAdminMessageFactory.createErrorKVAdminMessage;

import weloveclouds.kvstore.models.messages.KVAdminMessage;

/**
 * An unrecognized request to the data access layer.
 * 
 * @author Benedek
 */
public class DefaultRequest implements IKVECSRequest {

    private String errorMessage;

    public DefaultRequest(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public KVAdminMessage execute() {
        return createErrorKVAdminMessage(errorMessage);
    }

    @Override
    public IKVECSRequest validate() throws IllegalArgumentException {
        return this;
    }

}

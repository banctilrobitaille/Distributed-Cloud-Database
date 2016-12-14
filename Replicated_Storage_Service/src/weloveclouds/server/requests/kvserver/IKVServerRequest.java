package weloveclouds.server.requests.kvserver;

import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.networking.models.requests.IExecutable;
import weloveclouds.commons.networking.models.requests.IValidatable;

/**
 * An executable request which produces the response {@link IKVTransferMessage}.
 * 
 * @author Benedek
 */
public interface IKVServerRequest
        extends IExecutable<IKVTransferMessage>, IValidatable<IKVServerRequest> {

}

package weloveclouds.server.requests.kvserver;

import org.apache.log4j.Logger;

import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.commons.networking.models.requests.ICallbackRegister;
import weloveclouds.commons.networking.models.requests.IRequestFactory;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.server.services.datastore.IMovableDataAccessService;

/**
 * CommandFactory design pattern, which gives a common handling mechanism of different requests. It
 * handles several requests (see {@link StatusType} for the possible types) by dispatching the
 * command to its respective handler.
 *
 * @author Benedek
 */
public class KVServerRequestFactory
        implements IRequestFactory<IKVTransferMessage, IKVServerRequest> {

    private static final Logger LOGGER = Logger.getLogger(KVServerRequestFactory.class);

    private IMovableDataAccessService dataAccessService;

    public KVServerRequestFactory(IMovableDataAccessService dataAccessService) {
        this.dataAccessService = dataAccessService;
    }

    @Override
    public IKVServerRequest createRequestFromReceivedMessage(IKVTransferMessage receivedMessage,
            ICallbackRegister callbackRegister) {
        IKVServerRequest request = null;
        StatusType status = receivedMessage.getStatus();

        switch (status) {
            case TRANSFER_ENTRIES:
                request = new Transfer(dataAccessService, receivedMessage.getStorageUnits());
                break;
            case REMOVE_ENTRY_BY_KEY:
                request = new RemoveEntry(dataAccessService, receivedMessage.getRemovableKey());
                break;
            case PUT_ENTRY:
                request = new PutEntry(dataAccessService, receivedMessage.getPutableEntry());
                break;
            default:
                String errorMessage = "Unrecognized command for transfer message";
                LOGGER.error(StringUtils.join(" ", errorMessage, receivedMessage));
                request = new DefaultRequest(errorMessage);
                break;
        }

        return request;
    }
}


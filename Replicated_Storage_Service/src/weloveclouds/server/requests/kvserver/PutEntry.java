package weloveclouds.server.requests.kvserver;

import static weloveclouds.server.requests.kvserver.utils.KVTransferMessageFactory.createErrorKVTransferMessage;
import static weloveclouds.server.requests.kvserver.utils.KVTransferMessageFactory.createSuccessKVTransferMessage;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.KVEntry;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.server.core.requests.exceptions.IllegalRequestException;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * A transfer request to the {@link IMovableDataAccessService}, which means the respective entry
 * shall be put into the data access service.
 * 
 * @author Benedek
 */
public class PutEntry implements IKVServerRequest {

    private static final Logger LOGGER = Logger.getLogger(PutEntry.class);

    private IMovableDataAccessService dataAccessService;
    private KVEntry entry;

    /**
     * @param dataAccessService a reference to the data access service
     * @param entry that shall be put into the data access service
     */
    public PutEntry(IMovableDataAccessService dataAccessService, KVEntry entry) {
        this.dataAccessService = dataAccessService;
        this.entry = entry;
    }

    @Override
    public KVTransferMessage execute() {
        try {
            LOGGER.debug("Executing put entry request.");
            dataAccessService.putEntryWithoutAuthorization(entry);
            LOGGER.debug("Remove entry request finished successfully.");
            return createSuccessKVTransferMessage();
        } catch (StorageException ex) {
            LOGGER.error(ex);
            return createErrorKVTransferMessage(ex.getMessage());
        }
    }


    @Override
    public IKVServerRequest validate() throws IllegalArgumentException {
        if (entry == null) {
            String errorMessage = "Entry cannot be null.";
            LOGGER.error(errorMessage);
            throw new IllegalRequestException(createErrorKVTransferMessage(errorMessage));
        }
        return this;
    }

}

package weloveclouds.server.models.requests.kvecs;

import static weloveclouds.server.models.requests.kvecs.utils.KVAdminMessageFactory.createErrorKVAdminMessage;
import static weloveclouds.server.models.requests.kvecs.utils.KVAdminMessageFactory.createSuccessKVAdminMessage;

import org.apache.log4j.Logger;

import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.services.exceptions.UninitializedServiceException;
import weloveclouds.server.services.models.DataAccessServiceStatus;

/**
 * A lock request to the {@link IMovableDataAccessService}, which forbids PUT and DELETE requests to
 * be processed.
 * 
 * @author Benedek
 */
public class LockWriteAccess implements IKVECSRequest {

    private static final Logger LOGGER = Logger.getLogger(LockWriteAccess.class);

    private IMovableDataAccessService dataAccessService;

    public LockWriteAccess(IMovableDataAccessService dataAccessService) {
        this.dataAccessService = dataAccessService;
    }

    @Override
    public KVAdminMessage execute() {
        try {
            LOGGER.debug("Executing lock write request.");
            dataAccessService.setServiceStatus(DataAccessServiceStatus.WRITELOCK_ACTIVE);
            LOGGER.debug("Lock write request finished successfully.");
            return createSuccessKVAdminMessage();
        } catch (UninitializedServiceException ex) {
            LOGGER.error(ex);
            return createErrorKVAdminMessage(ex.getMessage());
        }
    }

    @Override
    public IKVECSRequest validate() throws IllegalArgumentException {
        return this;
    }

}

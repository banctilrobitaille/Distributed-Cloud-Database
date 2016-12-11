package weloveclouds.server.requests.kvclient;

import static weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType.DELETE_ERROR;
import static weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType.DELETE_SUCCESS;
import static weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType.SERVER_NOT_RESPONSIBLE;
import static weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType.SERVER_STOPPED;
import static weloveclouds.commons.kvstore.models.messages.IKVMessage.StatusType.SERVER_WRITE_LOCK;
import static weloveclouds.server.requests.kvclient.utils.KVMessageFactory.createKVMessage;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.exceptions.IllegalRequestException;
import weloveclouds.commons.hashing.models.RingMetadata;
import weloveclouds.commons.kvstore.models.messages.KVMessage;
import weloveclouds.commons.kvstore.serialization.helper.ISerializer;
import weloveclouds.server.requests.validator.KVServerRequestsValidator;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.services.MovableDataAccessService;
import weloveclouds.server.services.exceptions.KeyIsNotManagedByServiceException;
import weloveclouds.server.services.exceptions.ServiceIsStoppedException;
import weloveclouds.server.services.exceptions.WriteLockIsActiveException;
import weloveclouds.server.store.exceptions.StorageException;

/**
 * A delete request to remove the key and its respective value stored in the
 * {@link MovableDataAccessService}.
 * 
 * @author Benoit
 */
public class Delete implements IKVClientRequest {

    private static final Logger LOGGER = Logger.getLogger(Delete.class);

    private IMovableDataAccessService dataAccessService;
    private String key;

    private ISerializer<String, RingMetadata> ringMetadataSerializer;

    protected Delete(Builder builder) {
        this.dataAccessService = builder.dataAccessService;
        this.key = builder.key;
        this.ringMetadataSerializer = builder.ringMetadataSerializer;
    }

    @Override
    public KVMessage execute() {
        KVMessage response = null;
        try {
            LOGGER.debug(CustomStringJoiner.join(" ", "Trying to remove key", key));
            dataAccessService.removeEntry(key);
            response = createKVMessage(DELETE_SUCCESS, key, null);
        } catch (KeyIsNotManagedByServiceException ex) {
            RingMetadata ringMetadata = dataAccessService.getRingMetadata();
            String ringMetadataStr = ringMetadataSerializer.serialize(ringMetadata);
            response = createKVMessage(SERVER_NOT_RESPONSIBLE, key, ringMetadataStr);
        } catch (ServiceIsStoppedException ex) {
            response = createKVMessage(SERVER_STOPPED, key, null);
        } catch (WriteLockIsActiveException ex) {
            response = createKVMessage(SERVER_WRITE_LOCK, key, null);
        } catch (StorageException e) {
            response = createKVMessage(DELETE_ERROR, key, e.getMessage());
        } finally {
            LOGGER.debug(CustomStringJoiner.join(" ", "Result:", response.toString()));
        }
        return response;
    }

    @Override
    public IKVClientRequest validate() throws IllegalArgumentException {
        try {
            KVServerRequestsValidator.validateValueAsKVKey(key);
        } catch (IllegalArgumentException ex) {
            String errorMessage = "Key is invalid.";
            LOGGER.error(errorMessage);
            throw new IllegalRequestException(createKVMessage(DELETE_ERROR, key, errorMessage));
        }
        return this;
    }

    /**
     * Builder pattern for creating a {@link Delete} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private IMovableDataAccessService dataAccessService;
        private String key;
        private ISerializer<String, RingMetadata> ringMetadataSerializer;

        public Builder dataAccessService(IMovableDataAccessService dataAccessService) {
            this.dataAccessService = dataAccessService;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder ringMetadataSerializer(
                ISerializer<String, RingMetadata> ringMetadataSerializer) {
            this.ringMetadataSerializer = ringMetadataSerializer;
            return this;
        }

        public Delete build() {
            return new Delete(this);
        }
    }
}
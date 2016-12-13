package weloveclouds.server.requests.kvecs;


import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createErrorKVAdminMessage;
import static weloveclouds.server.requests.kvecs.utils.KVAdminMessageFactory.createSuccessKVAdminMessage;

import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.commons.exceptions.IllegalRequestException;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.kvstore.models.messages.IKVTransferMessage;
import weloveclouds.commons.kvstore.models.messages.KVAdminMessage;
import weloveclouds.commons.kvstore.models.messages.KVTransferMessage;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.commons.serialization.models.SerializedMessage;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.server.requests.kvecs.utils.StorageUnitsTransporter;
import weloveclouds.server.requests.kvecs.utils.StorageUnitsTransporterFactory;
import weloveclouds.server.requests.validator.KVServerRequestsValidator;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.services.IReplicableDataAccessService;
import weloveclouds.server.store.models.MovableStorageUnit;

/**
 * A data copy request to the {@link IMovableDataAccessService}, which copies a range of the data
 * stored on the data access service to a remote location.
 * 
 * @author Benedek
 */
public class CopyDataToDestination implements IKVECSRequest {

    private static final Logger LOGGER = Logger.getLogger(CopyDataToDestination.class);

    private IMovableDataAccessService dataAccessService;

    private ICommunicationApi communicationApi;
    private RingMetadataPart targetServerInfo;
    private IMessageSerializer<SerializedMessage, IKVTransferMessage> transferMessageSerializer;
    private IMessageDeserializer<IKVTransferMessage, SerializedMessage> transferMessageDeserializer;
    private StorageUnitsTransporterFactory storageUnitsTransporterFactory;

    protected CopyDataToDestination(Builder builder) {
        this.dataAccessService = builder.dataAccessService;
        this.targetServerInfo = builder.targetServerInfo;
        this.communicationApi = builder.communicationApi;
        this.transferMessageSerializer = builder.transferMessageSerializer;
        this.transferMessageDeserializer = builder.transferMessageDeserializer;
        this.storageUnitsTransporterFactory = builder.storageUnitsTransporterFactory;
    }

    @Override
    public KVAdminMessage execute() {
        try {
            LOGGER.debug("Executing copy data request.");
            HashRange hashRange = targetServerInfo.getWriteRange();
            Set<MovableStorageUnit> filteredEntries = dataAccessService.filterEntries(hashRange);

            if (!filteredEntries.isEmpty()) {
                StorageUnitsTransporter storageUnitsTransporter =
                        storageUnitsTransporterFactory.createStorageUnitsTransporter(
                                communicationApi, targetServerInfo.getConnectionInfo(),
                                transferMessageSerializer, transferMessageDeserializer);
                storageUnitsTransporter.transferStorageUnits(filteredEntries);
                LOGGER.debug("Copy data request finished successfully.");
            }
        } catch (Exception ex) {
            LOGGER.error(ex);
            return createErrorKVAdminMessage(ex.getMessage());
        }

        return createSuccessKVAdminMessage();
    }

    @Override
    public IKVECSRequest validate() throws IllegalArgumentException {
        try {
            KVServerRequestsValidator.validateRingMetadataPart(targetServerInfo);
        } catch (IllegalArgumentException ex) {
            String errorMessage = "Target server information is invalid.";
            LOGGER.error(errorMessage);
            throw new IllegalRequestException(createErrorKVAdminMessage(errorMessage));
        }
        return this;
    }

    /**
     * Builder pattern for creating a {@link CopyDataToDestination} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private IReplicableDataAccessService dataAccessService;
        private RingMetadataPart targetServerInfo;
        private ICommunicationApi communicationApi;
        private StorageUnitsTransporterFactory storageUnitsTransporterFactory;
        private IMessageSerializer<SerializedMessage, IKVTransferMessage> transferMessageSerializer;
        private IMessageDeserializer<IKVTransferMessage, SerializedMessage> transferMessageDeserializer;

        /**
         * @param dataAccessService a reference to the data access service
         */
        public Builder dataAccessService(IReplicableDataAccessService dataAccessService) {
            this.dataAccessService = dataAccessService;
            return this;
        }

        /**
         * @param targetServerInfo which contains the <IP, port> and <hash range> information about
         *        the target server to which those entries shall be transferred whose key's are in
         *        the range defined by this object
         */
        public Builder targetServerInfo(RingMetadataPart targetServerInfo) {
            this.targetServerInfo = targetServerInfo;
            return this;
        }

        /**
         * @param communicationApi to communicate with the target server
         */
        public Builder communicationApi(ICommunicationApi communicationApi) {
            this.communicationApi = communicationApi;
            return this;
        }

        /**
         * @param storageUnitsTransporterFactory a factory to create {@link StorageUnitsTransporter}
         */
        public Builder storageUnitsTransporterFactory(
                StorageUnitsTransporterFactory storageUnitsTransporterFactory) {
            this.storageUnitsTransporterFactory = storageUnitsTransporterFactory;
            return this;
        }

        /**
         * @param transferMessageSerializer to serialize {@link KVTransferMessage} into
         *        {@link SerializedMessage}
         */
        public Builder transferMessageSerializer(
                IMessageSerializer<SerializedMessage, IKVTransferMessage> transferMessageSerializer) {
            this.transferMessageSerializer = transferMessageSerializer;
            return this;
        }

        /**
         * @param transferMessageDeserializer to deserialize {@link KVTransferMessage} from
         *        {@link SerializedMessage}
         */
        public Builder transferMessageDeserializer(
                IMessageDeserializer<IKVTransferMessage, SerializedMessage> transferMessageDeserializer) {
            this.transferMessageDeserializer = transferMessageDeserializer;
            return this;
        }

        public CopyDataToDestination build() {
            return new CopyDataToDestination(this);
        }
    }

}

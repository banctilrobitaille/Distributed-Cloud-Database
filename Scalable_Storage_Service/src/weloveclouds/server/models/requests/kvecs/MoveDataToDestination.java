package weloveclouds.server.models.requests.kvecs;


import org.apache.log4j.Logger;

import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.hashing.models.RingMetadataPart;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.models.messages.IKVTransferMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.core.requests.exceptions.IllegalRequestException;
import weloveclouds.server.models.requests.validator.KVServerRequestsValidator;
import weloveclouds.server.services.IMovableDataAccessService;
import weloveclouds.server.services.exceptions.UninitializedServiceException;
import weloveclouds.server.store.models.MovableStorageUnits;

/**
 * A data move request to the {@link IMovableDataAccessService}, which moves a range of the data
 * stored on the data access service to a remote location.
 * 
 * @author Benedek
 */
public class MoveDataToDestination implements IKVECSRequest {

    private static final Logger LOGGER = Logger.getLogger(MoveDataToDestination.class);

    private IMovableDataAccessService dataAccessService;
    private RingMetadataPart targetServerInfo;

    private IMessageSerializer<SerializedMessage, KVTransferMessage> transferMessageSerializer;
    private IMessageDeserializer<KVTransferMessage, SerializedMessage> transferMessageDeserializer;
    private ICommunicationApi communicationApi;

    /**
     * @param dataAccessService a reference to the data access service
     * @param targetServerInfo which contains the <IP, port> and <hash range> information about the
     *        target server to which those entries shall be transferred whose key's are in the range
     *        defined by this object
     * @param communicationApi to communicate with the target server
     */
    public MoveDataToDestination(IMovableDataAccessService dataAccessService,
            RingMetadataPart targetServerInfo, ICommunicationApi communicationApi) {
        this.dataAccessService = dataAccessService;
        this.targetServerInfo = targetServerInfo;
        this.communicationApi = communicationApi;
    }

    @Override
    public KVAdminMessage execute() {
        try {
            LOGGER.debug("Executing move data request.");

            HashRange hashRange = targetServerInfo.getRange();
            MovableStorageUnits filteredEntries = dataAccessService.filterEntries(hashRange);

            if (!filteredEntries.getStorageUnits().isEmpty()) {
                try {
                    KVTransferMessage transferMessage = new KVTransferMessage.Builder()
                            .status(StatusType.TRANSFER).storageUnits(filteredEntries).build();

                    disconnect();
                    communicationApi.connectTo(targetServerInfo.getConnectionInfo());
                    SerializedMessage serializedMessage =
                            transferMessageSerializer.serialize(transferMessage);
                    communicationApi.send(serializedMessage.getBytes());
                    KVTransferMessage response =
                            transferMessageDeserializer.deserialize(communicationApi.receive());

                    if (response.getStatus() == StatusType.TRANSFER_ERROR) {
                        return createErrorKVAdminMessage(response.getResponseMessage());
                    } else {
                        dataAccessService.removeEntries(hashRange);
                        dataAccessService.defragment();
                        LOGGER.debug("Move data request finished successfully.");
                    }
                } catch (Exception ex) {
                    LOGGER.error(ex);
                    return createErrorKVAdminMessage(ex.getMessage());
                } finally {
                    disconnect();
                }
            }
        } catch (UninitializedServiceException ex) {
            LOGGER.error(ex);
            return createErrorKVAdminMessage(ex.getMessage());
        }

        return new KVAdminMessage.Builder()
                .status(weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType.RESPONSE_SUCCESS)
                .build();
    }

    private void disconnect() {
        try {
            communicationApi.disconnect();
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }

    private KVAdminMessage createErrorKVAdminMessage(String errorMessage) {
        return new KVAdminMessage.Builder()
                .status(weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType.RESPONSE_ERROR)
                .responseMessage(errorMessage).build();
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

}

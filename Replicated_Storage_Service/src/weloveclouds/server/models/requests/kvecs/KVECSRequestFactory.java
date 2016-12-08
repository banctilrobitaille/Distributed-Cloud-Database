package weloveclouds.server.models.requests.kvecs;

import static weloveclouds.client.utils.CustomStringJoiner.join;

import org.apache.log4j.Logger;

import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.kvstore.deserialization.IMessageDeserializer;
import weloveclouds.kvstore.models.messages.IKVAdminMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVAdminMessage;
import weloveclouds.kvstore.models.messages.KVTransferMessage;
import weloveclouds.kvstore.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.core.requests.ICallbackRegister;
import weloveclouds.server.core.requests.IRequestFactory;
import weloveclouds.server.models.requests.kvecs.utils.StorageUnitsTransporterFactory;
import weloveclouds.server.services.IMovableDataAccessService;

/**
 * CommandFactory design pattern, which gives a common handling mechanism of different requests. It
 * handles several requests (see {@link StatusType} for the possible types) by dispatching the
 * command to its respective handler.
 *
 * @author Benedek
 */
public class KVECSRequestFactory implements IRequestFactory<KVAdminMessage, IKVECSRequest> {

    private static final Logger LOGGER = Logger.getLogger(KVECSRequestFactory.class);

    private IMovableDataAccessService dataAccessService;
    private ICommunicationApi communicationApi;
    private StorageUnitsTransporterFactory storageUnitsTransporterFactory;

    private IMessageSerializer<SerializedMessage, KVTransferMessage> transferMessageSerializer;
    private IMessageDeserializer<KVTransferMessage, SerializedMessage> transferMessageDeserializer;

    public KVECSRequestFactory(IMovableDataAccessService dataAccessService,
            CommunicationApiFactory communicationApiFactory,
            IMessageSerializer<SerializedMessage, KVTransferMessage> transferMessageSerializer,
            IMessageDeserializer<KVTransferMessage, SerializedMessage> transferMessageDeserializer,
            StorageUnitsTransporterFactory storageUnitsTransporterFactory) {
        this.dataAccessService = dataAccessService;
        this.communicationApi = communicationApiFactory.createCommunicationApiV1();
        this.transferMessageSerializer = transferMessageSerializer;
        this.transferMessageDeserializer = transferMessageDeserializer;
        this.storageUnitsTransporterFactory = storageUnitsTransporterFactory;
    }

    @Override
    public IKVECSRequest createRequestFromReceivedMessage(KVAdminMessage receivedMessage,
            ICallbackRegister callbackRegister) {
        IKVECSRequest request = null;
        StatusType status = receivedMessage.getStatus();

        switch (status) {
            case INITKVSERVER:
                request =
                        new InitializeKVServer(dataAccessService, receivedMessage.getRingMetadata(),
                                receivedMessage.getManagedHashRangesWithRole());
                break;
            case START:
                request = new StartDataAcessService(dataAccessService);
                break;
            case STOP:
                request = new StopDataAccessService(dataAccessService);
                break;
            case LOCKWRITE:
                request = new LockWriteAccess(dataAccessService);
                break;
            case UNLOCKWRITE:
                request = new UnlockWriteAccess(dataAccessService);
                break;
            case COPYDATA:
                request = new CopyDataToDestination(dataAccessService, communicationApi,
                        receivedMessage.getTargetServerInfo(), transferMessageSerializer,
                        transferMessageDeserializer, storageUnitsTransporterFactory);
                break;
            case MOVEDATA:
                request = new MoveDataToDestination(dataAccessService, communicationApi,
                        receivedMessage.getTargetServerInfo(), transferMessageSerializer,
                        transferMessageDeserializer, storageUnitsTransporterFactory);
                break;
            case UPDATE:
                request =
                        new UpdateRingMetadata(dataAccessService, receivedMessage.getRingMetadata(),
                                receivedMessage.getManagedHashRangesWithRole());
                break;
            case SHUTDOWN:
                request = new ShutdownServer(callbackRegister);
                break;
            default:
                String errorMessage = "Unrecognized command for KVAdmin message";
                LOGGER.error(join(" ", errorMessage, receivedMessage.toString()));
                request = new DefaultRequest(errorMessage);
                break;
        }

        return request;
    }
}


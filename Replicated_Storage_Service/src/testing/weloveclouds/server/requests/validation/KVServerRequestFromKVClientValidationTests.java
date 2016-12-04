package testing.weloveclouds.server.requests.validation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import weloveclouds.communication.CommunicationApiFactory;
import weloveclouds.communication.exceptions.ConnectionClosedException;
import weloveclouds.communication.exceptions.UnableToSendContentToServerException;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.kvstore.deserialization.KVMessageDeserializer;
import weloveclouds.kvstore.models.messages.IKVMessage.StatusType;
import weloveclouds.kvstore.models.messages.KVMessage;
import weloveclouds.commons.serialization.IMessageSerializer;
import weloveclouds.kvstore.serialization.KVMessageSerializer;
import weloveclouds.kvstore.serialization.exceptions.DeserializationException;
import weloveclouds.kvstore.serialization.models.SerializedMessage;
import weloveclouds.server.api.KVCommunicationApiFactory;
import weloveclouds.server.api.v2.IKVCommunicationApiV2;
import weloveclouds.server.models.conf.KVServerPortConstants;

/**
 * Unit tests for validating KVServer, server-side request validation of messages from KVClient.
 * 
 * @author Benedek
 */
public class KVServerRequestFromKVClientValidationTests extends TestCase {

    private static final String SERVER_IP_ADDRESS = "localhost";
    private static final int SERVER_KVCLIENT_REQUEST_ACCEPTING_PORT =
            KVServerPortConstants.KVCLIENT_REQUESTS_PORT;

    IKVCommunicationApiV2 serverCommunication;
    IMessageDeserializer<KVMessage, SerializedMessage> kvmessageDeserializer;
    IMessageSerializer<SerializedMessage, KVMessage> kvmessageSerializer;

    @Before
    public void setUp() throws Exception {
        ServerConnectionInfo bootstrapConnectionInfo = new ServerConnectionInfo.Builder()
                .ipAddress(SERVER_IP_ADDRESS).port(SERVER_KVCLIENT_REQUEST_ACCEPTING_PORT).build();
        serverCommunication =
                new KVCommunicationApiFactory().createKVCommunicationApiV2(bootstrapConnectionInfo);
        serverCommunication.connect();

        kvmessageDeserializer = new KVMessageDeserializer();
        kvmessageSerializer = new KVMessageSerializer();
    }

    @After
    public void tearDown() {
        serverCommunication.disconnect();
    }

    @Test
    public void testSendPutMessageWithoutKey() throws UnableToSendContentToServerException,
            ConnectionClosedException, DeserializationException {
        KVMessage message =
                new KVMessage.Builder().status(StatusType.PUT).key(null).value("default").build();
        serverCommunication.send(kvmessageSerializer.serialize(message).getBytes());

        KVMessage response = kvmessageDeserializer.deserialize(serverCommunication.receive());
        Assert.assertEquals(StatusType.PUT_ERROR, response.getStatus());
    }

    @Test
    public void testSendDeleteMessageWithoutKey() throws UnableToSendContentToServerException,
            ConnectionClosedException, DeserializationException {
        KVMessage message = new KVMessage.Builder().status(StatusType.PUT).key(null).build();
        serverCommunication.send(kvmessageSerializer.serialize(message).getBytes());

        KVMessage response = kvmessageDeserializer.deserialize(serverCommunication.receive());
        Assert.assertEquals(StatusType.DELETE_ERROR, response.getStatus());
    }

    @Test
    public void testSendGetMessageWithoutKey() throws UnableToSendContentToServerException,
            ConnectionClosedException, DeserializationException {
        KVMessage message = new KVMessage.Builder().status(StatusType.GET).key(null).build();
        serverCommunication.send(kvmessageSerializer.serialize(message).getBytes());

        KVMessage response = kvmessageDeserializer.deserialize(serverCommunication.receive());
        Assert.assertEquals(StatusType.GET_ERROR, response.getStatus());
    }

}

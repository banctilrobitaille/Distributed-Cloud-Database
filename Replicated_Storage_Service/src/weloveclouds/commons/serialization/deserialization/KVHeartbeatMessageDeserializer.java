package weloveclouds.commons.serialization.deserialization;

import com.google.inject.Inject;

import java.util.regex.Matcher;

import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.serialization.models.SerializedMessage;
import weloveclouds.loadbalancer.models.KVHearthbeatMessage;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

import static weloveclouds.commons.serialization.models.SerializationConstants.NODE_HEALTH_INFOS_GROUP;
import static weloveclouds.commons.serialization.models.SerializationConstants.NODE_HEALTH_INFOS_REGEX;
import static weloveclouds.commons.kvstore.serialization.models.SerializedMessage.MESSAGE_ENCODING;

/**
 * Created by Benoit on 2016-12-09.
 */
public class KVHeartbeatMessageDeserializer implements IMessageDeserializer<KVHearthbeatMessage, SerializedMessage> {
    private IDeserializer<NodeHealthInfos, String> healthInfosStringDeserializer;

    @Inject
    public KVHeartbeatMessageDeserializer(IDeserializer<NodeHealthInfos, String> healthInfosStringDeserializer) {
        this.healthInfosStringDeserializer = healthInfosStringDeserializer;
    }

    @Override
    public KVHearthbeatMessage deserialize(SerializedMessage serializedMessage) throws DeserializationException {
        Matcher matcher = NODE_HEALTH_INFOS_REGEX.matcher(new String(serializedMessage.getBytes(), MESSAGE_ENCODING));
        NodeHealthInfos nodeHealthInfos = null;

        try {
            if (matcher.find()) {
                nodeHealthInfos = healthInfosStringDeserializer.deserialize(matcher.group
                        (NODE_HEALTH_INFOS_GROUP));
            }
        } catch (Exception e) {
            throw new DeserializationException("Unable to deserialize message: " + new String
                    (serializedMessage.getBytes(), MESSAGE_ENCODING));
        }
        return new KVHearthbeatMessage(nodeHealthInfos);
    }

    @Override
    public KVHearthbeatMessage deserialize(byte[] serializedMessage) throws DeserializationException {
        return null;
    }
}

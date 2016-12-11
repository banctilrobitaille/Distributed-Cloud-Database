package weloveclouds.commons.serialization.deserialization;

import com.google.inject.Inject;

import java.util.regex.Matcher;

import weloveclouds.commons.serialization.IMessageDeserializer;
import weloveclouds.commons.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.serialization.models.SerializedMessage;
import weloveclouds.loadbalancer.models.KVHearthbeatMessage;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

import static weloveclouds.commons.kvstore.serialization.models.SerializedMessage.MESSAGE_ENCODING;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;
import static weloveclouds.commons.serialization.models.XMLTokens.NODE_HEALTH_INFOS;

/**
 * Created by Benoit on 2016-12-09.
 */
public class KVHeartbeatMessageDeserializer implements
        IMessageDeserializer<KVHearthbeatMessage, SerializedMessage> {
    private IDeserializer<NodeHealthInfos, String> healthInfosDeserializer;

    @Inject
    public KVHeartbeatMessageDeserializer(IDeserializer<NodeHealthInfos, String>
                                                  healthInfosDeserializer) {
        this.healthInfosDeserializer = healthInfosDeserializer;
    }

    @Override
    public KVHearthbeatMessage deserialize(SerializedMessage serializedMessage)
            throws DeserializationException {
        Matcher matcher = getRegexFromToken(NODE_HEALTH_INFOS)
                .matcher(new String(serializedMessage.getBytes(), MESSAGE_ENCODING));

        try {
            if (matcher.find()) {
                return new KVHearthbeatMessage(healthInfosDeserializer
                        .deserialize(matcher.group(XML_NODE)));
            } else {
                throw new DeserializationException("Unable to deserialize message: " + new String
                        (serializedMessage.getBytes(), MESSAGE_ENCODING));
            }
        } catch (Exception e) {
            throw new DeserializationException(e.getMessage());
        }
    }

    @Override
    public KVHearthbeatMessage deserialize(byte[] serializedMessage)
            throws DeserializationException {
        return deserialize(new SerializedMessage(new String(serializedMessage, MESSAGE_ENCODING)));
    }
}

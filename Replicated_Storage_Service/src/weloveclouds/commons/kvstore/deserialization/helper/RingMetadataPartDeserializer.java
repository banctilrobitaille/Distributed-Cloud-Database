package weloveclouds.commons.kvstore.deserialization.helper;

import java.util.Set;

import org.apache.log4j.Logger;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.hashing.models.HashRange;
import weloveclouds.commons.hashing.models.RingMetadataPart;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.serialization.helper.RingMetadataPartSerializer;
import weloveclouds.communication.models.ServerConnectionInfo;

/**
 * A deserializer which converts a {@link RingMetadataPart} to a {@link String}.
 * 
 * @author Benedek
 */
public class RingMetadataPartDeserializer implements IDeserializer<RingMetadataPart, String> {

    private static final int NUMBER_OF_RING_METADATA_PART_PARTS = 3;
    private static final int CONNECTION_INFO_INDEX = 0;
    private static final int READ_RANGES_INDEX = 1;
    private static final int WRITE_RANGES_INDEX = 2;

    private static final Logger LOGGER = Logger.getLogger(RingMetadataPartDeserializer.class);

    private IDeserializer<ServerConnectionInfo, String> connectionInfoDeserializer =
            new ServerConnectionInfoDeserializer();
    private IDeserializer<Set<HashRange>, String> hashRangesDeserializer =
            new HashRangesSetDeserializer();
    private IDeserializer<HashRange, String> hashRangeDeserializer = new HashRangeDeserializer();

    @Override
    public RingMetadataPart deserialize(String from) throws DeserializationException {
        RingMetadataPart deserialized = null;

        if (from != null && !"null".equals(from)) {
            LOGGER.debug("Deserializing a RingMetadataPart from String.");
            // raw message split
            String[] parts = from.split(RingMetadataPartSerializer.SEPARATOR);

            // length check
            if (parts.length != NUMBER_OF_RING_METADATA_PART_PARTS) {
                throw new DeserializationException(
                        CustomStringJoiner.join("", "Ring metadata part must consist of exactly ",
                                String.valueOf(NUMBER_OF_RING_METADATA_PART_PARTS), " parts."));
            }

            // raw fields
            String connectionInfoStr = parts[CONNECTION_INFO_INDEX];
            String readRangesStr = parts[READ_RANGES_INDEX];
            String writeRangeStr = parts[WRITE_RANGES_INDEX];

            // deserialized fields
            ServerConnectionInfo connectionInfo =
                    connectionInfoDeserializer.deserialize(connectionInfoStr);
            Set<HashRange> readRanges = hashRangesDeserializer.deserialize(readRangesStr);
            HashRange writeRange = hashRangeDeserializer.deserialize(writeRangeStr);

            // deserialized object
            deserialized = new RingMetadataPart.Builder().connectionInfo(connectionInfo)
                    .readRanges(readRanges).writeRange(writeRange).build();
            LOGGER.debug("Deserializing a RingMetadataPart from String finished.");
        }

        return deserialized;
    }

}

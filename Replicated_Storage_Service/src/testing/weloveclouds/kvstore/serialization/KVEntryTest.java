package testing.weloveclouds.kvstore.serialization;

import java.net.UnknownHostException;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import testing.weloveclouds.kvstore.serialization.utils.OuterTagRemover;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.commons.kvstore.deserialization.helper.KVEntryDeserializer;
import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.commons.kvstore.serialization.helper.ISerializer;
import weloveclouds.commons.kvstore.serialization.helper.KVEntrySerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.XMLTokens;

/**
 * Tests for the {@link KVEntry} to verify its serialization and deserialization processes.
 * 
 * @author Benedek
 */
public class KVEntryTest extends TestCase {

    private static final IDeserializer<KVEntry, String> kvEntryDeserializer =
            new KVEntryDeserializer();
    private static final ISerializer<AbstractXMLNode, KVEntry> kvEntrySerializer =
            new KVEntrySerializer();

    @Test
    public void testHashSerializationAndDeserialization()
            throws DeserializationException, UnknownHostException {
        KVEntry kvEntry = new KVEntry("hello", "world");

        String serializedKVEntry = OuterTagRemover.removeOuterTag(
                kvEntrySerializer.serialize(kvEntry).toString(), XMLTokens.KV_ENTRY);
        KVEntry deserializedKVEntry = kvEntryDeserializer.deserialize(serializedKVEntry);

        Assert.assertEquals(kvEntry.toString(), deserializedKVEntry.toString());
        Assert.assertEquals(kvEntry, deserializedKVEntry);
    }
}

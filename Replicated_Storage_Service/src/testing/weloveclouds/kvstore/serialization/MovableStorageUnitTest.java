package testing.weloveclouds.kvstore.serialization;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import testing.weloveclouds.kvstore.serialization.utils.OuterTagRemover;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.deserialization.helper.IDeserializer;
import weloveclouds.commons.kvstore.deserialization.helper.MovableStorageUnitDeserializer;
import weloveclouds.commons.kvstore.serialization.helper.ISerializer;
import weloveclouds.commons.kvstore.serialization.helper.MovableStorageUnitSerializer;
import weloveclouds.commons.serialization.models.AbstractXMLNode;
import weloveclouds.commons.serialization.models.XMLTokens;
import weloveclouds.server.store.models.MovableStorageUnit;
import weloveclouds.server.utils.FileUtility;

/**
 * Tests for the {@link MovableStorageUnit} to verify its serialization and deserialization
 * processes.
 * 
 * @author Benedek
 */
public class MovableStorageUnitTest extends TestCase {

    private static final IDeserializer<MovableStorageUnit, String> storageUnitDeserializer =
            new MovableStorageUnitDeserializer();
    private static final ISerializer<AbstractXMLNode, MovableStorageUnit> storageUnitSerializer =
            new MovableStorageUnitSerializer();

    @Test
    public void testMovableStorageUnitSerializationAndDeserialization()
            throws DeserializationException, IOException {
        Map<String, String> keyval = new HashMap<>();
        keyval.put("hello", "world");
        keyval.put("apple", "juice");

        MovableStorageUnit storageUnit =
                new MovableStorageUnit(keyval, FileUtility.createDummyPath());

        String serializedStorageUnit = OuterTagRemover.removeOuterTag(
                storageUnitSerializer.serialize(storageUnit).toString(), XMLTokens.STORAGE_UNIT);
        MovableStorageUnit deserializedStorageUnit =
                storageUnitDeserializer.deserialize(serializedStorageUnit);

        Assert.assertEquals(storageUnit.toString(), deserializedStorageUnit.toString());
        Assert.assertEquals(storageUnit, deserializedStorageUnit);
    }
}

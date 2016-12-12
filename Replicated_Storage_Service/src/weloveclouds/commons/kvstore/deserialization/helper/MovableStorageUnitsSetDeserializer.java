package weloveclouds.commons.kvstore.deserialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.STORAGE_UNIT;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.serialization.IDeserializer;
import weloveclouds.server.store.models.MovableStorageUnit;

/**
 * A deserializer which converts a {@link Set<MovableStorageUnit>} to a {@link String}.
 * 
 * @author Benedek
 */
public class MovableStorageUnitsSetDeserializer
        implements IDeserializer<Set<MovableStorageUnit>, String> {

    private IDeserializer<MovableStorageUnit, String> storageUnitDeserializer =
            new MovableStorageUnitDeserializer();

    @Override
    public Set<MovableStorageUnit> deserialize(String from) throws DeserializationException {
        Set<MovableStorageUnit> deserialized = null;

        if (from != null && !"null".equals(from)) {
            try {
                deserialized = new HashSet<>();

                Matcher storageUnitMatcher = getRegexFromToken(STORAGE_UNIT).matcher(from);
                while (storageUnitMatcher.find()) {
                    deserialized.add(storageUnitDeserializer
                            .deserialize(storageUnitMatcher.group(XML_NODE)));
                }

                if (deserialized.isEmpty()) {
                    throw new DeserializationException(CustomStringJoiner.join("",
                            "Unable to extract storage unit from:", from));
                }
            } catch (Exception ex) {
                new DeserializationException(ex.getMessage());
            }
        }

        return deserialized;
    }

}

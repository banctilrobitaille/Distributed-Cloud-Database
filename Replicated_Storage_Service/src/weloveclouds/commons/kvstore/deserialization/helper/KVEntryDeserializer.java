package weloveclouds.commons.kvstore.deserialization.helper;

import static weloveclouds.commons.serialization.models.XMLTokens.KEY;
import static weloveclouds.commons.serialization.models.XMLTokens.VALUE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.XML_NODE;
import static weloveclouds.commons.serialization.utils.XMLPatternUtils.getRegexFromToken;

import java.util.regex.Matcher;

import static weloveclouds.client.utils.CustomStringJoiner.join;
import weloveclouds.commons.kvstore.deserialization.exceptions.DeserializationException;
import weloveclouds.commons.kvstore.models.KVEntry;

/**
 * A deserializer which converts a {@link KVEntry} to a {@link String}.
 * 
 * @author Benedek
 */
public class KVEntryDeserializer implements IDeserializer<KVEntry, String> {

    @Override
    public KVEntry deserialize(String from) throws DeserializationException {
        KVEntry deserialized = null;

        if (from != null && !"null".equals(from)) {
            try {
                deserialized =
                        new KVEntry(deserializeField(from, KEY), deserializeField(from, VALUE));
            } catch (Exception ex) {
                new DeserializationException(ex.getMessage());
            }
        }

        return deserialized;
    }

    private String deserializeField(String from, String token) throws DeserializationException {
        Matcher fieldMatcher = getRegexFromToken(token).matcher(from);
        if (fieldMatcher.find()) {
            return fieldMatcher.group(XML_NODE);
        } else {
            throw new DeserializationException(
                    join("", "Unable to extract ", token, " from:", from));
        }
    }
}

package org.sagebionetworks.bridge.dynamodb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.sagebionetworks.bridge.json.BridgeObjectMapper;
import org.sagebionetworks.bridge.models.ParticipantData;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class DynamoParticipantDataTest {

    private static final BridgeObjectMapper MAPPER = BridgeObjectMapper.get();

    @Test
    public void canSerialize() throws Exception{
        DynamoParticipantData participantData = new DynamoParticipantData();
        String userId = "foo";
        String identifier = "baz";
        Long version = 12345L;

        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put("a", true);
        objectNode.put("b", "string");
        objectNode.put("c", 10);

        participantData.setUserId(userId);
        participantData.setIdentifier(identifier);
        participantData.setData(objectNode);
        participantData.setVersion(version);

        String json = MAPPER.writeValueAsString(participantData);

        JsonNode node = MAPPER.readTree(json);

        assertNull(node.get("userId"));
        assertEquals(node.get("identifier").textValue(), identifier);
        assertEquals(new Long(node.get("version").longValue()), version);
        assertTrue(node.get("data").get("a").booleanValue());
        assertEquals(node.get("data").get("b").textValue(), "string");
        assertEquals(node.get("data").get("c").intValue(), 10);
        assertEquals(node.get("type").textValue(), "ParticipantData");
        assertEquals(node.size(), 4);

        ParticipantData deser = MAPPER.readValue(json, ParticipantData.class);
        assertTrue(deser.getData().get("a").asBoolean());
        assertEquals(deser.getData().get("b").textValue(), "string");
        assertEquals(deser.getData().get("c").intValue(), 10);
        assertEquals(deser.getIdentifier(), identifier);
        assertEquals(deser.getVersion(), version);
    }
}
package com.yschool.ydisk.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.yschool.ydisk.model.ResponseError;
import com.yschool.ydisk.model.SystemItemImport;
import com.yschool.ydisk.model.SystemItemImportRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.Set;

@RestController
@RequestMapping("imports")
public class ImportsController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);

    private final ResponseError badRequestInvalidJson = new ResponseError(400, "Validation Failed");

    private JsonNode getJsonSchemaNode() throws IOException {
        return objectMapper.readTree(new File("src/main/resources/model/SystemItemImportRequest.json"));
    }

    private JsonSchema getJsonSchema(JsonNode jsonSchemaNode) {
        return jsonSchemaFactory.getSchema(jsonSchemaNode);
    }

    @PostMapping()
    public ResponseEntity imports(@RequestBody String json) throws IOException {
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(json);
        } catch (JsonParseException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        Set<ValidationMessage> validationErrors = getJsonSchema(getJsonSchemaNode()).validate(jsonNode);
        SystemItemImportRequest systemItemImportRequest;
        if (validationErrors.size() == 0) {
            systemItemImportRequest = objectMapper.readValue(json, SystemItemImportRequest.class);
        }
        else {
            return new ResponseEntity(badRequestInvalidJson, HttpStatus.BAD_REQUEST);
        }
        System.out.println(systemItemImportRequest);
        //TODO add other validations
        return new ResponseEntity(HttpStatus.OK);
    }
}

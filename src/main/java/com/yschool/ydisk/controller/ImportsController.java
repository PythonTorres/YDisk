package com.yschool.ydisk.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.yschool.ydisk.YdiskApplication;
import com.yschool.ydisk.entity.SystemItem;
import com.yschool.ydisk.model.ResponseError;
import com.yschool.ydisk.model.SystemItemImport;
import com.yschool.ydisk.model.SystemItemImportRequest;
import com.yschool.ydisk.repository.SystemItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.*;

@RestController
@RequestMapping("imports")
public class ImportsController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);
    private final ResponseError badRequestInvalidJson = new ResponseError(400, "Validation Failed");
    @Autowired
    private SystemItemRepository systemItemRepository;

    private HashMap<String, SystemItemImport> importFoldersMap = new HashMap<String, SystemItemImport>();
    private HashMap<String, SystemItemImport> importFilesMap = new HashMap<String, SystemItemImport>();

    private JsonNode getJsonSchemaNode() throws IOException, java.net.URISyntaxException {
        return objectMapper.readTree(new File("src/main/resources/model/SystemItemImportRequest.json"));
    }

    private JsonSchema getJsonSchema(JsonNode jsonSchemaNode) {
        return jsonSchemaFactory.getSchema(jsonSchemaNode);
    }

    private boolean checkInputDataAndCreateImportSets(SystemItemImportRequest systemItemImportRequest) {
        List<SystemItemImport> systemItemsFromRequest = systemItemImportRequest.getItems();
        HashSet<String> inputIds = new HashSet<>();
        //1st loop to perform primary validations
        for (SystemItemImport item : systemItemsFromRequest) {
            String itemId = item.getId();
            String itemType = item.getType();
            //item id is duplicated in input
            if (inputIds.contains(itemId)) {
                return false;
            }
            //item id is duplicated in database and type isn't the same
            if (systemItemRepository.existsById(itemId) && !systemItemRepository.getTypeById(itemId).equals(itemType)) {
                return false;
            }
            //checks for folder
            if (itemType.equals("FOLDER")) {
                //url for folder is not empty
                if (item.getUrl() != null) {
                    return false;
                }
                //item size not null
                if (item.getSize() != null) {
                    return false;
                }
            }
            //checks for files
            if (itemType.equals("FILE")) {
                //file url is null or exceeds length 255
                if (item.getUrl() == null || item.getUrl().length() > 255) {
                    return false;
                }
                //size of file is null or not greater than zero
                if (item.getSize() == null || item.getSize() <= 0) {
                    return false;
                }
            }
            //sets addition
            if (itemType.equals("FOLDER")) {
                importFoldersMap.put(itemId, item);
            }
            else if (itemType.equals("FILE")) {
                importFilesMap.put(itemId, item);
            }
            else {
                return false;
            }
            inputIds.add(itemId);
        }
        //second loop to validate the parents
        for (SystemItemImport item : systemItemsFromRequest) {
            String itemParent = item.getParentId();
            if (itemParent != null && !importFoldersMap.containsKey(itemParent) && (!systemItemRepository.existsById(itemParent) || !systemItemRepository.getTypeById(itemParent).equals("FOLDER"))) {
                return false;
            }
        }

        return true;
    }

    private void updateDataBaseWithImports(SystemItemImportRequest systemItemImportRequest) {
        Date updateDate = systemItemImportRequest.getUpdateDate();
        //update folders
        for (String folderId : importFoldersMap.keySet()) {
            SystemItem systemItem = new SystemItem(importFoldersMap.get(folderId), updateDate);
            if (!systemItemRepository.existsById(folderId)) {
                systemItem.setSize(0L);
                systemItemRepository.save(systemItem);
            }
            else {
                String prevParentId = systemItemRepository.getParentIdById(folderId);
                if (prevParentId == null && systemItem.getParentId() == null) {
                    Long itemSizeInDataBase = systemItemRepository.getSizeById(folderId);
                    systemItem.setSize(itemSizeInDataBase);
                    systemItemRepository.save(systemItem);
                }
                else if (prevParentId != null && systemItem.getParentId() != null && prevParentId.equals(systemItem.getParentId())) {
                    Long itemSizeInDataBase = systemItemRepository.getSizeById(folderId);
                    systemItem.setSize(itemSizeInDataBase);
                    systemItemRepository.save(systemItem);
                }
                else {
                    //decrease old parents size
                    String nextParentId = prevParentId;
                    while (nextParentId != null) {
                        SystemItem nextParentSystemItem = systemItemRepository.findById(nextParentId).get();
                        nextParentSystemItem.setSize(nextParentSystemItem.getSize() - systemItem.getSize());
                        nextParentSystemItem.setDate(updateDate);
                        systemItemRepository.save(nextParentSystemItem);
                        nextParentId = nextParentSystemItem.getParentId();
                    }
                    //increase new parents size
                    nextParentId = systemItem.getParentId();
                    while (nextParentId != null) {
                        SystemItem nextParentSystemItem = systemItemRepository.findById(nextParentId).get();
                        nextParentSystemItem.setSize(nextParentSystemItem.getSize() + systemItem.getSize());
                        nextParentSystemItem.setDate(updateDate);
                        systemItemRepository.save(nextParentSystemItem);
                        nextParentId = nextParentSystemItem.getParentId();
                    }
                    systemItemRepository.save(systemItem);
                }
            }
        }
        //update files
        for (String fileId : importFilesMap.keySet()) {
            SystemItem systemItem = new SystemItem(importFilesMap.get(fileId), updateDate);
            if (!systemItemRepository.existsById(fileId)) {
                //increase new parents size
                String nextParentId = systemItem.getParentId();
                while (nextParentId != null) {
                    SystemItem nextParentSystemItem = systemItemRepository.findById(nextParentId).get();
                    nextParentSystemItem.setSize(nextParentSystemItem.getSize() + systemItem.getSize());
                    nextParentSystemItem.setDate(updateDate);
                    systemItemRepository.save(nextParentSystemItem);
                    nextParentId = nextParentSystemItem.getParentId();
                }
                systemItemRepository.save(systemItem);
            }
            else {
                String prevParentId = systemItemRepository.getParentIdById(fileId);
                if (prevParentId == null && systemItem.getParentId() == null) {
                    systemItemRepository.save(systemItem);
                }
                else if (prevParentId != null && systemItem.getParentId() != null && prevParentId.equals(systemItem.getParentId())) {
                    systemItemRepository.save(systemItem);
                }
                else {
                    //decrease old parents size
                    String nextParentId = prevParentId;
                    while (nextParentId != null) {
                        SystemItem nextParentSystemItem = systemItemRepository.findById(nextParentId).get();
                        nextParentSystemItem.setSize(nextParentSystemItem.getSize() - systemItem.getSize());
                        nextParentSystemItem.setDate(updateDate);
                        systemItemRepository.save(nextParentSystemItem);
                        nextParentId = nextParentSystemItem.getParentId();
                    }
                    //increase new parents size
                    nextParentId = systemItem.getParentId();
                    while (nextParentId != null) {
                        SystemItem nextParentSystemItem = systemItemRepository.findById(nextParentId).get();
                        nextParentSystemItem.setSize(nextParentSystemItem.getSize() + systemItem.getSize());
                        nextParentSystemItem.setDate(updateDate);
                        systemItemRepository.save(nextParentSystemItem);
                        nextParentId = nextParentSystemItem.getParentId();
                    }
                    systemItemRepository.save(systemItem);
                }
            }
        }
    }

    @PostMapping()
    public ResponseEntity imports(@RequestBody String json) throws IOException, java.net.URISyntaxException {
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

        boolean inputDataIsCorrect = checkInputDataAndCreateImportSets(systemItemImportRequest);
        if (!inputDataIsCorrect) {
            return new ResponseEntity(badRequestInvalidJson, HttpStatus.BAD_REQUEST);
        }

        updateDataBaseWithImports(systemItemImportRequest);

        importFoldersMap.clear();
        importFilesMap.clear();
        return new ResponseEntity(HttpStatus.OK);
    }
}

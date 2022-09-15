package com.yschool.ydisk.controller;

import com.yschool.ydisk.entity.SystemItem;
import com.yschool.ydisk.model.ResponseError;
import com.yschool.ydisk.repository.SystemItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("nodes")
public class NodesController {
    private final ResponseError badRequestInvalidParams = new ResponseError(400, "Validation Failed");
    private final ResponseError notFoundItemNotFound = new ResponseError(404, "Item not found");
    @Autowired
    private SystemItemRepository systemItemRepository;

    private void getAndWriteChildrenForSystemItem(SystemItem systemItem) {
        if(systemItem.getType().equals("FILE")) {
            systemItem.setChildren(null);
            return;
        }
        List<SystemItem> childrenSystemItems = systemItemRepository.getSystemItemByParentId(systemItem.getId());
        systemItem.setChildren(childrenSystemItems);
        for (SystemItem childSystemItem : childrenSystemItems) {
            getAndWriteChildrenForSystemItem(childSystemItem);
        }
    }
    private SystemItem getSystemItemNode(String id) {
        SystemItem systemItem = systemItemRepository.findById(id).get();
        getAndWriteChildrenForSystemItem(systemItem);
        return systemItem;
    }
    @GetMapping("{id}")
    public ResponseEntity nodes(@PathVariable String id) {
        if (!systemItemRepository.existsById(id)) {
            return new ResponseEntity(notFoundItemNotFound, HttpStatus.NOT_FOUND);
        }

        SystemItem responseSystemItem = getSystemItemNode(id);

        return new ResponseEntity(responseSystemItem, HttpStatus.OK);
    }
}

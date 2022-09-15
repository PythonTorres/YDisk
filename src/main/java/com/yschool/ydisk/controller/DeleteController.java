package com.yschool.ydisk.controller;

import com.yschool.ydisk.entity.SystemItem;
import com.yschool.ydisk.model.ResponseError;
import com.yschool.ydisk.repository.SystemItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.DatatypeConverter;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("delete")
public class DeleteController {
    private final ResponseError badRequestInvalidParams = new ResponseError(400, "Validation Failed");
    private final ResponseError notFoundItemNotFound = new ResponseError(404, "Item not found");
    @Autowired
    private SystemItemRepository systemItemRepository;

    private Date getValidDateOrNull(String dateString) {
        Date dateTimeToReturn;
        try {
            OffsetDateTime time = OffsetDateTime.parse(dateString);
            dateTimeToReturn = new Date(time.toInstant().toEpochMilli());
        } catch (DateTimeParseException e) {
            return null;
        }
        return dateTimeToReturn;
    }

    @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable String id, @RequestParam String date) {
        Date validDate = getValidDateOrNull(date);
        if (validDate == null) {
            return new ResponseEntity(badRequestInvalidParams, HttpStatus.BAD_REQUEST);
        }
        if (!systemItemRepository.existsById(id)) {
            return new ResponseEntity(notFoundItemNotFound, HttpStatus.NOT_FOUND);
        }
        Long sizeOfDeletedItem = systemItemRepository.getSizeById(id);
        String parentIdOfDeletedItem = systemItemRepository.getParentIdById(id);
        List<String> IdsToDelete = systemItemRepository.getChildrenIdsWithTheTargetIdById(id);
        systemItemRepository.deleteAllById(IdsToDelete);

        //decrease old parents size
        String nextParentId = parentIdOfDeletedItem;
        while (nextParentId != null) {
            SystemItem nextParentSystemItem = systemItemRepository.findById(nextParentId).get();
            nextParentSystemItem.setSize(nextParentSystemItem.getSize() - sizeOfDeletedItem);
            nextParentSystemItem.setDate(validDate);
            systemItemRepository.save(nextParentSystemItem);
            nextParentId = nextParentSystemItem.getParentId();
        }


        return new ResponseEntity(HttpStatus.OK);
    }
}

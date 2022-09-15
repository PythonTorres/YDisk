package com.yschool.ydisk.controller;

import com.yschool.ydisk.entity.SystemItem;
import com.yschool.ydisk.model.ResponseError;
import com.yschool.ydisk.model.SystemItemHistoryResponse;
import com.yschool.ydisk.model.SystemItemHistoryUnit;
import com.yschool.ydisk.repository.SystemItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("updates")
public class UpdatesController {
    private final ResponseError badRequestInvalidParams = new ResponseError(400, "Validation Failed");
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

    private SystemItemHistoryResponse getSystemItemHistoryResponseBetweenDates(Date dateStart, Date dateEnd) {
        List<SystemItem> systemItemList = systemItemRepository.getSystemItemByDateBetween(dateStart, dateEnd);
        List<SystemItemHistoryUnit> systemItemHistoryUnits = new ArrayList<>();
        for (SystemItem item : systemItemList) {
            systemItemHistoryUnits.add(new SystemItemHistoryUnit(item));
        }
        return new SystemItemHistoryResponse(systemItemHistoryUnits);
    }

    @GetMapping()
    public ResponseEntity updates(@RequestParam String date) {
        Date validDate = getValidDateOrNull(date);
        if (validDate == null) {
            return new ResponseEntity(badRequestInvalidParams, HttpStatus.BAD_REQUEST);
        }

        Long timeRange = 86400000L;
        Date dateStart = new Date(validDate.getTime() - timeRange);

        SystemItemHistoryResponse systemItemHistoryResponse = getSystemItemHistoryResponseBetweenDates(dateStart, validDate);

        return new ResponseEntity(systemItemHistoryResponse, HttpStatus.OK);
    }
}

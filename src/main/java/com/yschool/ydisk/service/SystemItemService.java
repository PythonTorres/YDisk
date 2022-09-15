package com.yschool.ydisk.service;

import com.yschool.ydisk.entity.SystemItem;
import com.yschool.ydisk.repository.SystemItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class SystemItemService {
    @Autowired
    private SystemItemRepository systemItemRepository;

    @Transactional
    public void createSystemItem(SystemItem systemItem) {
    }
}

package com.yschool.ydisk.repository;

import com.yschool.ydisk.entity.SystemItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface SystemItemRepository extends JpaRepository<SystemItem, String> {

    @Query(value = "select s.type from systemitems s where s.id = :id", nativeQuery = true)
    String getTypeById(String id);

    @Query(value = "select s.parent_id from systemitems s where s.id = :id", nativeQuery = true)
    String getParentIdById(String id);

    @Query(value = "select s.size from systemitems s where s.id = :id", nativeQuery = true)
    Long getSizeById(String id);

    @Query(value="with recursive rec (id, parent_id) as " +
            "(select sy.id, sy.parent_id from systemitems sy where sy.id = :id " +
            "UNION ALL select s.id, s.parent_id from systemitems s join rec r on r.id = s.parent_id) select id from rec;",
    nativeQuery = true)
    List<String> getChildrenIdsWithTheTargetIdById(String id);

    List<SystemItem> getSystemItemByParentId(String parentId);

    List<SystemItem> getSystemItemByDateBetween(Date dateStart, Date dateEnd);
}

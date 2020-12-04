package com.venesa.gateway.repository;

import com.venesa.gateway.entity.MobioEventErrEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MobioEventRespository extends JpaRepository<MobioEventErrEntity,Long> {

    @Query(value = "select * from mobio_event_err m where cast(m.create_time as Date) = cast(now() as Date) and m.is_active = 1 order by m.create_time", nativeQuery = true)
    List<MobioEventErrEntity> getAllToday();

    @Modifying
    @Transactional
    @Query(value = "update mobio_event_err m set m.is_active = 0 where m.mobio_event_id = :id",nativeQuery = true)
    void updateStatus(@Param("id") long id);

    MobioEventErrEntity findById(long id);
}

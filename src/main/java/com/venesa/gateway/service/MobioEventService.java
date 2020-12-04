package com.venesa.gateway.service;

import com.venesa.gateway.entity.MobioEventErrEntity;

import java.util.List;

public interface MobioEventService  {
     void save(MobioEventErrEntity entity) throws Exception;

     List<MobioEventErrEntity> getAllToday() throws Exception;

     void updateStatus(long id) throws Exception;
}

package com.venesa.gateway.serviceImpl;

import com.venesa.gateway.entity.MobioEventErrEntity;
import com.venesa.gateway.repository.MobioEventRespository;
import com.venesa.gateway.service.MobioEventService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MobioEventServiceImpl implements MobioEventService {
    private final MobioEventRespository mobioEventRespository;

    @Override
    public void save(MobioEventErrEntity entity) {
        mobioEventRespository.save(entity);
    }

    @Override
    public List<MobioEventErrEntity> getAllToday() {
        return mobioEventRespository.getAllToday();
    }

    @Override
    public void updateStatus(long id)  {
        mobioEventRespository.updateStatus(id);
    }
}

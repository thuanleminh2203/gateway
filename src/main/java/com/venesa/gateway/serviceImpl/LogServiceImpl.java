package com.venesa.gateway.serviceImpl;

import com.venesa.gateway.entity.LogEntity;
import com.venesa.gateway.repository.LogRepository;
import com.venesa.gateway.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;

    @Autowired
    public LogServiceImpl(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void save(LogEntity entity) {
        logRepository.save(entity);
    }

    @Override
    @Cacheable(cacheNames = {"longCache"})
    public List<LogEntity> getAll() {
        System.out.println("=====getAlll Log============");
//        logRepository.findAll().stream().forEach(k -> {
//            JSONObject jsonObject = new JSONObject(k.getResponseBody());
//            System.out.println("===== jsonObject ====" + jsonObject.toString().replace("\"",""));
//            String body = k.getResponseBody().replaceAll(".*\".*", "\\\"");
//            System.out.println("======= body ====" + body);
//        });
        return logRepository.findAll();
    }

    @Override
    @CacheEvict(value = "longCache", allEntries = true)
    public void deleteById(long id) {
        System.out.println("=====delete by id =======");
        logRepository.deleteById(id);
    }
}

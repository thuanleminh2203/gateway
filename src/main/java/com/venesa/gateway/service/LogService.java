package com.venesa.gateway.service;


import com.venesa.gateway.entity.LogEntity;

import java.util.List;

public interface LogService {
	void save(LogEntity logEntity);
	List<LogEntity> getAll();
	void deleteById(long id);

}

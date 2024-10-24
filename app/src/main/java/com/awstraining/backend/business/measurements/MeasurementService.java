package com.awstraining.backend.business.measurements;

import com.awstraining.backend.business.measurements.exceptions.CouldNotSaveMeasurementException;
import com.awstraining.backend.business.measurements.repository.MeasurementDBEntity;
import com.awstraining.backend.business.measurements.repository.MeasurementRepositoryDynamoDB;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeasurementService {
    @Value("${backend.measurements.ttlInSeconds:2592000}")
    private Long ttlInSeconds;
    private final MeasurementRepositoryDynamoDB repository;

    public MeasurementService(final MeasurementRepositoryDynamoDB repository) {
        this.repository = repository;
    }

    public void saveMeasurement(final MeasurementDO measurementDO) throws CouldNotSaveMeasurementException {
        final MeasurementDBEntity dbEntity = new MeasurementDBEntity();
        dbEntity.setDeviceId(measurementDO.getDeviceId());
        dbEntity.setType(measurementDO.getType());
        dbEntity.setValue(measurementDO.getValue());
        dbEntity.setCreationTime(measurementDO.getCreationTime());
        dbEntity.setExpiresAt(measurementDO.getCreationTime() / 1000L + ttlInSeconds);

        repository.save(dbEntity);
    }

    public List<MeasurementDO> getMeasurements() {
        return repository
                .getAll()
                .stream()
                .map(this::toMeasurementDO)
                .toList();
    }

    private MeasurementDO toMeasurementDO(final MeasurementDBEntity measurementDBEntity) {
        final MeasurementDO measurementDO = new MeasurementDO();
        measurementDO.setType(measurementDBEntity.getType());
        measurementDO.setCreationTime(measurementDBEntity.getCreationTime());
        measurementDO.setValue(measurementDBEntity.getValue());
        measurementDO.setDeviceId(measurementDBEntity.getDeviceId());
        return measurementDO;
    }
}

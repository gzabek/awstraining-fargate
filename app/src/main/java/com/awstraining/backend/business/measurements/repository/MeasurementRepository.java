package com.awstraining.backend.business.measurements.repository;

import com.awstraining.backend.business.measurements.exceptions.CouldNotSaveMeasurementException;

import java.util.List;

public interface MeasurementRepository {
    void save(final MeasurementDBEntity measurementDbEntity) throws CouldNotSaveMeasurementException;
    List<MeasurementDBEntity> getAll();
}

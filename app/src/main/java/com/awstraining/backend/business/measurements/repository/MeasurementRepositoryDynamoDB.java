package com.awstraining.backend.business.measurements.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.awstraining.backend.business.measurements.exceptions.CouldNotSaveMeasurementException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MeasurementRepositoryDynamoDB implements MeasurementRepository {
    private static final Logger LOGGER = LogManager.getLogger(MeasurementRepositoryDynamoDB.class);
    private final DynamoDBMapper mapper;

    @Autowired
    public MeasurementRepositoryDynamoDB(final DynamoDBMapper mapper) {
        this.mapper = mapper;
    }

    public void save(final MeasurementDBEntity measurementDbEntity) throws CouldNotSaveMeasurementException {
        try {
            mapper.save(measurementDbEntity);
        } catch(final Exception e) {
            LOGGER.error("Could not save measurement in database.", e);
            throw new CouldNotSaveMeasurementException();
        }
    }

    @Override
    public List<MeasurementDBEntity> getAll() {
        final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        return mapper.scan(MeasurementDBEntity.class, scanExpression);
    }
}

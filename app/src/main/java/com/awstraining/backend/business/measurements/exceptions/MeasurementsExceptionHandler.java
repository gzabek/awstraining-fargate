package com.awstraining.backend.business.measurements.exceptions;

import com.awstraining.backend.api.rest.v1.model.ApiBusinessErrorResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Order(HIGHEST_PRECEDENCE)
@ControllerAdvice
public class MeasurementsExceptionHandler {

    @ExceptionHandler(UnknownDeviceException.class)
    public ResponseEntity<ApiBusinessErrorResponse> toResponse(final HttpServletRequest request,
                                                               final UnknownDeviceException unknownDeviceException) {
        return unknownDeviceException.toResponse(request);
    }

    @ExceptionHandler(CouldNotSaveMeasurementException.class)
    public ResponseEntity<ApiBusinessErrorResponse> toResponse(final HttpServletRequest request,
            final CouldNotSaveMeasurementException couldNotSaveMeasurementException) {
        return couldNotSaveMeasurementException.toResponse(request);
    }
}


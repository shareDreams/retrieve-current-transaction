package com.mrbarin.microservicios.retrievecurrenttransaction.api.aop;

import java.io.IOException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrbarin.microservicios.retrievecurrenttransaction.api.dto.request.RequestCurrentTransaction;
import com.mrbarin.microservicios.retrievecurrenttransaction.api.dto.response.ErrorResponse;
import com.mrbarin.microservicios.retrievecurrenttransaction.api.handlerexception.GlobalException;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class ValidationAOP {

	@Autowired
	private ObjectMapper mapper;

	private RequestCurrentTransaction request;

	@Around("@annotation(com.mrbarin.microservicios.retrievecurrenttransaction.api.annotations.Validate)")
	public Object requestRetrieveValidation(ProceedingJoinPoint joinPoint) throws Throwable {

		log.info("Request Validation");

		Object[] args = joinPoint.getArgs();
		GlobalException exception;

		try {
			request = mapper.readValue((String) args[0], RequestCurrentTransaction.class);
		} catch (IOException e) {
			exception = new GlobalException(new ErrorResponse("Internal Server Error", 500,
					"Error en la estrucutura del request",
					"Los campos no concuerdan con la estructura que debe tener el request, no puede ser parseado"));

			log.info("Exception 500", exception);
			log.trace("Exception 500", e);
			log.debug("Exception 500", e);
			throw exception;
		}

		if (request.getCustomerId() == null) {
			exception = new GlobalException(
					new ErrorResponse("Bad Request", 400, "customerId", "El campo customerId no puede estar vacio"));
			log.info("Exception 400", exception);
			throw exception;
		}
		
		if (String.valueOf(request.getCustomerId()).length() < 6  ) {
			exception = new GlobalException(
					new ErrorResponse("Bad Request", 400, "customerId", "El campo customerId no puede ser menor a 6 dígitos"));
			log.info("Exception 400", exception);
			throw exception;
		}

		if (request.getTransactionDateStart() == null || request.getTransactionDateStart().isEmpty()) {
			exception = new GlobalException(new ErrorResponse("Bad Request", 400, "transactionDateStart",
					"El campo transactionDateStart no puede estar vacio"));

			log.info("Exception 400", exception);
			throw exception;
		}

		if (request.getTransactionDateEnd() == null || request.getTransactionDateEnd().isEmpty()) {
			exception = new GlobalException(new ErrorResponse("Bad Request", 400, "transactionDateEnd",
					"El campo transactionDateEndno puede estar vacio"));
			log.info("Exception 500", exception);
			throw exception;
		}

		return joinPoint.proceed();

	}

}

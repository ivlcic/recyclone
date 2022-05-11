package com.dropchop.recyclone.rest.jaxrs.server;

import com.dropchop.recyclone.model.api.attr.AttributeString;
import com.dropchop.recyclone.model.api.invoke.ErrorCode;
import com.dropchop.recyclone.model.api.invoke.StatusMessage;
import com.dropchop.recyclone.model.api.invoke.ServiceException;
import com.dropchop.recyclone.model.dto.rest.Result;
import com.dropchop.recyclone.model.dto.rest.ResultCode;
import com.dropchop.recyclone.model.dto.rest.ResultStatus;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 20. 12. 21.
 */
@Slf4j
@Provider
public class ServiceErrorExceptionMapper implements ExceptionMapper<Exception> {

  private void setErrorCodeResponseStatus(Response.ResponseBuilder builder, StatusMessage statusMessage) {
    if (statusMessage != null && statusMessage.getCode() != null) {
      if (ErrorCode.internal_error == statusMessage.getCode()) {
        builder.status(Response.Status.INTERNAL_SERVER_ERROR);
      }
      if (ErrorCode.process_error == statusMessage.getCode()) {
        builder.status(Response.Status.INTERNAL_SERVER_ERROR);
      }
      if (ErrorCode.authentication_error == statusMessage.getCode()) {
        builder.status(Response.Status.UNAUTHORIZED);
      }
      if (ErrorCode.authorization_error == statusMessage.getCode()) {
        builder.status(Response.Status.UNAUTHORIZED);
      }
      if (ErrorCode.data_error == statusMessage.getCode()) {
        builder.status(Response.Status.BAD_REQUEST);
      }
      if (ErrorCode.not_found_error == statusMessage.getCode()) {
        builder.status(Response.Status.NOT_FOUND);
      }
      if (ErrorCode.data_missing_error == statusMessage.getCode()) {
        builder.status(Response.Status.BAD_REQUEST);
      }
      if (ErrorCode.data_validation_error == statusMessage.getCode()) {
        builder.status(Response.Status.BAD_REQUEST);
      }
      if (ErrorCode.parameter_validation_error == statusMessage.getCode()) {
        builder.status(Response.Status.BAD_REQUEST);
      }
      if (ErrorCode.quota_error == statusMessage.getCode()) {
        builder.status(Response.Status.SERVICE_UNAVAILABLE);
      }
    }
  }

  public Result<?> toResult(ServiceException e) {
    ResultStatus status = new ResultStatus();
    List<StatusMessage> statusMessages = e.getStatusMessages();
    if (statusMessages.size() > 1) {
      status.setDetails(statusMessages);
    }
    if (statusMessages.size() == 1) {
      status.setMessage(statusMessages.get(0));
    }
    status.setCode(ResultCode.error);
    Result<?> result = new Result<>();
    result.setStatus(status);
    return result;
  }

  public Response toResponse(Exception e) {
    log.error("", e);
    Response.ResponseBuilder builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
    Result<?> result;
    if (e instanceof ServiceException) {
      result = this.toResult((ServiceException) e);
      ResultStatus status = result.getStatus();
      StatusMessage statusMessage = status.getMessage();
      if (statusMessage == null) {
        log.warn("Got ServiceErrorException but without status message [{}]!", result);
      }

      if (status.getCode().equals(ResultCode.error)) {
        setErrorCodeResponseStatus(builder, statusMessage);
      }
    } else {
      ResultStatus status = new ResultStatus();
      StatusMessage statusMessage = new StatusMessage();
      if (e instanceof JsonMappingException) {
        statusMessage.setCode(ErrorCode.data_validation_error);
      } else {
        statusMessage.setCode(ErrorCode.internal_error);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        statusMessage.setDetails(Set.of(new AttributeString("trace", sw.toString())));
      }
      statusMessage.setText(e.getMessage());
      setErrorCodeResponseStatus(builder, statusMessage);
      status.setCode(ResultCode.error);
      status.setMessage(statusMessage);
      result = new Result<>();
      result.setStatus(status);
    }

    ObjectMapper objectMapper = new ObjectMapper();
    try {
      builder.entity(objectMapper.writeValueAsString(result));
    } catch (Exception ex) {
      log.warn("Unable to serialize [{}]!", result, ex);
    }

    return builder.build();
  }
}

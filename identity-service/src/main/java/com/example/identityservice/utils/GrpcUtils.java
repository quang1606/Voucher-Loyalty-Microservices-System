package com.example.identityservice.utils;

import com.example.common.BaseErrorCode;
import com.example.common.BaseException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import vn.com.grpc.base.entity.RequestInfo;
import vn.com.grpc.base.entity.ResponseInfo;
import vn.com.grpc.base.entity.ResponseInfo.Builder;

public class GrpcUtils {

  @Value("${spring.application.name}")
  private String serviceName;

  private static Builder buildBaseResponseInfo(RequestInfo requestInfo) {
    return ResponseInfo.newBuilder().setResponseId(requestInfo.getRequestId())
        .setResponseTime(System.currentTimeMillis());
  }

  public static ResponseInfo buildResponseInfoSuccess(RequestInfo requestInfo) {
    return buildBaseResponseInfo(requestInfo)
        .setErrorCode(BaseErrorCode.SUCCESS.getErrorCode())
        .setMessage(BaseErrorCode.SUCCESS.getErrorDescription())
        .build();
  }

  public static ResponseInfo buildResponseFail(RequestInfo requestInfo, Exception exception) {
    return buildBaseResponseInfo(requestInfo)
        .setErrorCode(BaseErrorCode.INTERNAL_ERROR.getErrorCode())
        .setMessage(exception.getMessage())
        .setStatus(BaseErrorCode.INTERNAL_ERROR.getErrorNumCode())
        .build();
  }

  public static ResponseInfo buildResponseFail(RequestInfo requestInfo, BaseException exception) {
    return buildBaseResponseInfo(requestInfo)
        .setErrorCode(exception.getErrorCode())
        .setMessage(exception.getDescription())
        .setStatus(exception.getHttpStatus().value())
        .build();
  }

  public RequestInfo builderRequestInfo() {
    return RequestInfo.newBuilder().setServiceId(serviceName)
        .setRequestTime(System.currentTimeMillis())
        .setRequestId(UUID.randomUUID().toString()).build();
  }

}

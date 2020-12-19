package dougou.web.rest.config;

import dougou.web.rest.dto.BaseResponse;
import dougou.web.rest.enums.ResponseCodeEnum;
import org.apache.commons.lang.enums.EnumUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Iterator;

/**
 * @program: DouGouServer
 * @description: 全局异常处理
 * @author: zihan.wu
 * @create: 2020-12-19 20:28
 **/

@RestControllerAdvice
public class GlobalExceptionConfig {
    /**
     * 参数校验错误异常
     *
     * @param ex
     * @return
     * @RequestBody参数校验失败
     */
    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler({MethodArgumentNotValidException.class, MissingServletRequestParameterException.class})
    public BaseResponse handleMethodArgumentNotValidException(Exception ex) {
        if (ex instanceof MethodArgumentNotValidException) {
            FieldError fieldError = ((MethodArgumentNotValidException) ex).getBindingResult().getFieldError();
            return BaseResponse.error(ResponseCodeEnum.RESPONSE_CODE_PARAM_FAILURE.getCode(), fieldError.getDefaultMessage());
        } else if (ex instanceof MissingServletRequestParameterException) {
            String parameterName = ((MissingServletRequestParameterException) ex).getParameterName();
            return BaseResponse.error(ResponseCodeEnum.RESPONSE_CODE_PARAM_FAILURE.getCode(),  parameterName+"参数异常");
        } else {
            return BaseResponse.error(ResponseCodeEnum.RESPONSE_CODE_PARAM_FAILURE.getCode(), ex.getMessage());
        }
    }

}

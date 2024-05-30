package com.qianlou.im.service.exception;


import com.qianlou.im.common.enums.BaseErrorCode;
import com.qianlou.im.common.execption.ApplicationException;
import com.qianlou.im.common.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseVO unknowException(Exception e) {
        log.error("出现未知异常: {}", e.getMessage());
        ResponseVO resp = new ResponseVO();
        resp.setCode(BaseErrorCode.SYSTEM_ERROR.getCode());
        resp.setMsg(BaseErrorCode.SYSTEM_ERROR.getError());
        /**
         * 未知异常的话，这里写逻辑，发邮件，发短信都可以、、
         */
        return resp;
    }


    /**
     * Validator 参数校验异常处理
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseBody
    public Object handleMethodArgumentNotValidException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        ResponseVO resp = new ResponseVO();
        resp.setCode(BaseErrorCode.PARAMETER_ERROR.getCode());
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            PathImpl pathImpl = (PathImpl) constraintViolation.getPropertyPath();
            // 读取参数字段，constraintViolation.getMessage() 读取验证注解中的message值
            String paramName = pathImpl.getLeafNode().getName();
            String message = "参数{".concat(paramName).concat("}").concat(constraintViolation.getMessage());
            resp.setMsg(message);
            return resp;
        }
        resp.setMsg(BaseErrorCode.PARAMETER_ERROR.getError() + ex.getMessage());
        return resp;
    }

    @ExceptionHandler(ApplicationException.class)
    @ResponseBody
    public Object applicationExceptionHandler(ApplicationException e) {
        // 使用公共的结果类封装返回结果, 这里我指定状态码为
        ResponseVO resultBean = new ResponseVO();
        resultBean.setCode(e.getCode());
        resultBean.setMsg(e.getError());
        return resultBean;
    }

    /**
     * Validator 参数校验异常处理
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public Object handleException2(BindException ex) {
        FieldError err = ex.getFieldError();
        String message = "参数{".concat(err.getField()).concat("}").concat(err.getDefaultMessage());
        ResponseVO resultBean = new ResponseVO();
        resultBean.setCode(BaseErrorCode.PARAMETER_ERROR.getCode());
        resultBean.setMsg(message);
        return resultBean;


    }

    //json格式
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public Object handleException1(MethodArgumentNotValidException ex) {
        StringBuilder errorMsg = new StringBuilder();
        BindingResult re = ex.getBindingResult();
        for (ObjectError error : re.getAllErrors()) {
            errorMsg.append(error.getDefaultMessage()).append(",");
        }
        errorMsg.delete(errorMsg.length() - 1, errorMsg.length());

        ResponseVO resultBean = new ResponseVO();
        resultBean.setCode(BaseErrorCode.PARAMETER_ERROR.getCode());
        resultBean.setMsg(BaseErrorCode.PARAMETER_ERROR.getError() + " : " + errorMsg.toString());
        return resultBean;
    }

}

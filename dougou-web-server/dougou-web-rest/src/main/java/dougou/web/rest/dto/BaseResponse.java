package dougou.web.rest.dto;

import dougou.web.rest.enums.ResponseCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BaseResponse<T> {
    T data;
    private Boolean success;
    private Integer code;
    private String message;
    public BaseResponse(T data){
        this.data = data;
        this.success = true;
        this.code = ResponseCodeEnum.RESPONSE_CODE_SUCCESS.getCode();
    }
    public static BaseResponse error(Integer code,String message){
        return new BaseResponse(null,false,code,message);
    }
}

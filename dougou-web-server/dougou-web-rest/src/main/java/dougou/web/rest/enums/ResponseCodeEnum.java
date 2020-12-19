package dougou.web.rest.enums;

public enum ResponseCodeEnum {
    RESPONSE_CODE_SUCCESS(200,"请求成功"),
    RESPONSE_CODE_BUSSINESS_FAILURE(500,"请求错误"),
    RESPONSE_CODE_NULL_FAILURE(501,"结果为空"),
    RESPONSE_CODE_PARAM_FAILURE(502,"参数异常");
    ResponseCodeEnum(Integer code,String desc){
        this.code = code;
        this.desc = desc;
    }
    private Integer code;
    private String desc;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

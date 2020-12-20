package dougou.web.rest.controller;

import com.mysql.cj.util.StringUtils;
import dougou.web.core.bo.UserWithRoleBo;
import dougou.web.core.ibo.AddUserIbo;
import dougou.web.core.service.IUserService;
import dougou.web.core.service.impl.UserServiceImpl;
import dougou.web.rest.dto.BaseResponse;
import dougou.web.rest.dto.GetUserInfoDto;
import dougou.web.rest.enums.ResponseCodeEnum;
import dougou.web.rest.vo.AddUserVo;
import dougou.web.rest.vo.RetriveUserVo;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Resource
    IUserService userServiceImpl;
    @Autowired
    ModelMapper modelMapper;
    @PostMapping("/retrive")
    public BaseResponse<GetUserInfoDto> retriveUser(@RequestBody @Validated RetriveUserVo retriveUserVo){
        UserWithRoleBo userWithRoleBo = userServiceImpl.getUserWithRoleByUsername(retriveUserVo.getUsername());
        System.out.println("userWithRoleBo.toString() = " + userWithRoleBo.toString());
        if (Objects.isNull(userWithRoleBo) || StringUtils.isNullOrEmpty(userWithRoleBo.getUsername())){
            return BaseResponse.error(ResponseCodeEnum.RESPONSE_CODE_NULL_FAILURE.getCode(),"用户不存在");
        }else{
            GetUserInfoDto dto = modelMapper.map(userWithRoleBo, GetUserInfoDto.class);
            dto.setRoleIds(userWithRoleBo.getUserRoles().stream().map(x->x.getRoleId()).collect(Collectors.toList()));
            return new BaseResponse(dto);
        }
    }
    @PostMapping("/register")
    public BaseResponse<Boolean> registerUser(@RequestBody @Validated AddUserVo addUserVo){
        AddUserIbo ibo = modelMapper.map(addUserVo, AddUserIbo.class);
        if(userServiceImpl.addUser(ibo)){
            return new BaseResponse<>(true);
        }else{
            return BaseResponse.error(ResponseCodeEnum.RESPONSE_CODE_BUSSINESS_FAILURE.getCode(),"用户名已存在");
        }
    }
}

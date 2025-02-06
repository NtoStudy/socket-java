package com.socket.socketjava.mapper;

import com.socket.socketjava.domain.pojo.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.socket.socketjava.domain.vo.LoginVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author 哞哞
 * @since 2025-02-06
 */
public interface UsersMapper extends BaseMapper<Users> {

    @Select("select * from users where number = #{numberCode} and password = #{password}")
    void login(LoginVo loginVo);
}

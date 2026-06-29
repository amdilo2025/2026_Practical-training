package com.diabetes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.diabetes.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

    @Select("SELECT * FROM chat_message WHERE user_id = #{userId} AND session_id = #{sessionId} " +
            "ORDER BY created_time ASC")
    List<ChatMessage> findBySession(@Param("userId") Integer userId, @Param("sessionId") String sessionId);

    @Select("SELECT DISTINCT session_id FROM chat_message WHERE user_id = #{userId} ORDER BY MAX(created_time) DESC")
    List<String> findSessionsByUser(@Param("userId") Integer userId);
}

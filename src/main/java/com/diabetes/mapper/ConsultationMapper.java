package com.diabetes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.diabetes.entity.Consultation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ConsultationMapper extends BaseMapper<Consultation> {

    @Select("SELECT c.*, d.name as doctor_name, d.title as doctor_title, d.department as doctor_department " +
            "FROM consultation c LEFT JOIN doctor d ON c.doctor_id = d.id " +
            "WHERE c.user_id = #{userId} ORDER BY c.created_time DESC")
    List<Map<String, Object>> selectConsultationsWithDoctor(@Param("userId") Integer userId);

    @Select("SELECT c.*, u.name as user_name " +
            "FROM consultation c LEFT JOIN `user` u ON c.user_id = u.id " +
            "ORDER BY c.created_time DESC")
    List<Map<String, Object>> selectAllWithUser();

    @Select("SELECT COUNT(*) FROM consultation WHERE status = #{status}")
    Integer countByStatus(@Param("status") String status);
}

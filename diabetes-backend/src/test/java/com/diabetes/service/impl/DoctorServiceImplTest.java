package com.diabetes.service.impl;

import com.diabetes.entity.Doctor;
import com.diabetes.mapper.DoctorMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * DoctorServiceImpl 单元测试 —— 目标覆盖率 100%
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DoctorServiceImpl 医生服务")
class DoctorServiceImplTest {

    @Mock
    private DoctorMapper doctorMapper;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private Doctor testDoctor;

    @BeforeEach
    void setUp() {
        testDoctor = new Doctor();
        testDoctor.setId(1);
        testDoctor.setName("赵晓峰");
        testDoctor.setDepartment("内分泌科");
        testDoctor.setTitle("主任医师");
        testDoctor.setIsAi(1);
        testDoctor.setStatus("正常");
    }

    @Test
    @DisplayName("listAll → AI医生置顶，其余按id升序")
    void listAll_ShouldOrderByIsAiDescAndIdAsc() {
        Doctor regularDoctor = new Doctor();
        regularDoctor.setId(2);
        regularDoctor.setName("普通医生");
        regularDoctor.setIsAi(0);

        when(doctorMapper.selectList(any())).thenReturn(List.of(testDoctor, regularDoctor));

        List<Doctor> result = doctorService.listAll();

        assertEquals(2, result.size());
        assertEquals("赵晓峰", result.get(0).getName()); // AI 医生应置顶
    }

    @Test
    @DisplayName("listAll 空 → 返回空列表")
    void listAll_Empty_ShouldReturnEmptyList() {
        when(doctorMapper.selectList(any())).thenReturn(List.of());
        List<Doctor> result = doctorService.listAll();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getById 存在 → 返回医生")
    void getById_Exists_ShouldReturnDoctor() {
        when(doctorMapper.selectById(1)).thenReturn(testDoctor);
        Doctor result = doctorService.getById(1);
        assertNotNull(result);
        assertEquals("赵晓峰", result.getName());
    }

    @Test
    @DisplayName("getById 不存在 → 返回 null")
    void getById_NotFound_ShouldReturnNull() {
        when(doctorMapper.selectById(999)).thenReturn(null);
        assertNull(doctorService.getById(999));
    }

    @Test
    @DisplayName("add → 调用 insert")
    void add_ShouldCallInsert() {
        when(doctorMapper.insert(testDoctor)).thenReturn(1);
        int result = doctorService.add(testDoctor);
        assertEquals(1, result);
    }

    @Test
    @DisplayName("update → 调用 updateById")
    void update_ShouldCallUpdateById() {
        when(doctorMapper.updateById(testDoctor)).thenReturn(1);
        int result = doctorService.update(testDoctor);
        assertEquals(1, result);
    }

    @Test
    @DisplayName("delete → 调用 deleteById")
    void delete_ShouldCallDeleteById() {
        when(doctorMapper.deleteById(1)).thenReturn(1);
        int result = doctorService.delete(1);
        assertEquals(1, result);
    }
}

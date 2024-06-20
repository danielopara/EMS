package com.danielopara.EMS.service;

import com.danielopara.EMS.dto.EmployeeDto;
import com.danielopara.EMS.dto.LoginDto;
import com.danielopara.EMS.dto.RegisterDto;
import com.danielopara.EMS.dto.UpdateEmployee;
import com.danielopara.EMS.response.BaseResponse;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.springframework.stereotype.Service;

@Service
public interface EmployeeService {
    BaseResponse addEmployee(RegisterDto registerDto);
    BaseResponse viewAllEmployees();
    BaseResponse loginEmployee(LoginDto loginDto);
    BaseResponse updateEmployeeInfo(Long id, UpdateEmployee updateEmployee);
    EmployeeDto viewEmployeeById(Long id);
}

package com.danielopara.EMS.controller;

import com.danielopara.EMS.dto.LoginDto;
import com.danielopara.EMS.dto.RegisterDto;
import com.danielopara.EMS.dto.UpdateEmployee;
import com.danielopara.EMS.response.BaseResponse;
import com.danielopara.EMS.service.impl.EmployeeServiceImpl;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/employee")
@CrossOrigin(origins = "*")
public class EmployeeController {
    private final EmployeeServiceImpl employeeService;

    public EmployeeController(EmployeeServiceImpl employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody RegisterDto registerDto){

        BaseResponse baseResponse = employeeService.addEmployee(registerDto);
        if(baseResponse.getStatus() == HttpStatus.OK.value()){
            return new ResponseEntity<>(baseResponse, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/update/{id}")
    ResponseEntity<?> updateEmployee(@PathVariable Long id , @RequestBody UpdateEmployee employee){
        BaseResponse response = employeeService.updateEmployeeInfo(id, employee);
        if(response.getStatus() == HttpStatus.OK.value()){
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        }
    }
    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody LoginDto loginDto){
        BaseResponse response = employeeService.loginEmployee(loginDto);
        if(response.getStatus() == HttpStatus.OK.value()){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping(value ="/employees", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getEmployees(){
//        String authorization = request.getHeader("Authorization");
//        if(authorization == null || !authorization.startsWith("Bearer") ){
//            BaseResponse response = new BaseResponse();
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setDescription("No token");
//            response.setData(Optional.empty());
//            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
//        }
        BaseResponse response = employeeService.viewAllEmployees();
        if(response.getStatus() == HttpStatus.OK.value()){
//            return new ResponseEntity<>(response, HttpStatus.OK);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);

        }
    }
}

package com.danielopara.EMS.service.impl;

import com.danielopara.EMS.config.jwt.JwtService;
import com.danielopara.EMS.dto.EmployeeDto;
import com.danielopara.EMS.dto.LoginDto;
import com.danielopara.EMS.dto.RegisterDto;
import com.danielopara.EMS.dto.UpdateEmployee;
import com.danielopara.EMS.entity.Employee;
import com.danielopara.EMS.repository.EmployeeRepository;
import com.danielopara.EMS.response.BaseResponse;
import com.danielopara.EMS.service.EmployeeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public BaseResponse addEmployee(RegisterDto registerDto) {
        try{
            // checks if email exists
            Boolean checksEmailExists = employeeRepository.existsByEmail(registerDto.getEmail());
            if(checksEmailExists){
                return new BaseResponse(
                        HttpServletResponse.SC_OK,
                        "Email already exists",
                        Optional.empty(),
                        "use another email: " + registerDto.getEmail()
                );
            }
            //check phone number exists
            boolean checksPhoneNumberExists = employeeRepository.existsByPhoneNumber(registerDto.getPhoneNumber());
            if(checksPhoneNumberExists){
                return new BaseResponse(
                        HttpServletResponse.SC_OK,
                        "Phone number already exists",
                        Optional.empty(),
                        "use another phone number: " + registerDto.getPhoneNumber()
                );
            }
            //checks for the length of the phone number
            String phoneNumber = registerDto.getPhoneNumber();
            String password = registerDto.getPassword();
            if(phoneNumber.length() != 11){
                return new BaseResponse(
                        HttpServletResponse.SC_CONFLICT,
                        "Phone number should be 11 digits",
                        Optional.empty(),
                        null
                );
            }
            //password length
            if(password.length() < 8){
                return new BaseResponse(
                        HttpServletResponse.SC_CONFLICT,
                        "Password should be 8 characters",
                        Optional.empty(),
                        null
                );
            }

            //validate user dob
            LocalDate employeeDob = registerDto.getDob();
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//            String formattedDob = employeeDob.toString();
//            String expectedFormat = employeeDob.getDayOfMonth() + "-" + employeeDob.getMonthValue() + "-" + employeeDob.getYear();
//            if (!formattedDob.equals(expectedFormat)) {
//                return new BaseResponse(
//                        HttpServletResponse.SC_CONFLICT,
//                        "Invalid date of birth format. Please use dd-MM-yyyy",
//                        Optional.empty(),
//                        Optional.empty()
//                );
//            }

            LocalDate currentDate = LocalDate.now();
            int employeeAge = Period.between(employeeDob, currentDate).getYears();
            Employee employee = Employee
                    .builder()
                    .firstName(registerDto.getFirstName())
                    .middleName(registerDto.getMiddleName())
                    .lastName(registerDto.getLastName())
                    .gender(registerDto.getGender())
                    .email(registerDto.getEmail())
                    .password(passwordEncoder.encode(registerDto.getPassword()))
                    .phoneNumber(registerDto.getPhoneNumber())
                    .dob(registerDto.getDob())
                    .role(registerDto.getRole())
                    .age(employeeAge)
                    .build();
            employeeRepository.save(employee);
            return new BaseResponse(
                    HttpServletResponse.SC_OK,
                    "Employee created",
                    registerDto,
                    null
            );
        } catch (Exception e){
            return new BaseResponse(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "ERROR",
                    Optional.empty(),
                    null
            );
        }
    }

    @Override
    public BaseResponse viewAllEmployees() {
        List<Employee> allEmployees = employeeRepository.findAll();

        if (!allEmployees.isEmpty()) {
            List<EmployeeDto> employeeDtos = new ArrayList<>();

            for (Employee employee : allEmployees) {
                EmployeeDto employeeDto = EmployeeDto.builder()
                        .firstName(employee.getFirstName())
                        .lastName(employee.getLastName())
                        .gender(employee.getGender())
                        .dob(employee.getDob())
                        .phoneNumber(employee.getPhoneNumber())
                        .profilePhoto(employee.getProfilePhoto().getFileName())
                        .email(employee.getEmail())
                        .role(employee.getRole())
                        .build();

                employeeDtos.add(employeeDto);
            }

            return new BaseResponse(
                    HttpServletResponse.SC_OK,
                    "All employees",
                    employeeDtos,
                    null
            );
        } else {
            return new BaseResponse(
                    HttpServletResponse.SC_OK,
                    "No employees",
                    Optional.empty(),
                    null
            );
        }
    }

    @Override
    public BaseResponse loginEmployee(LoginDto loginDto) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getEmail(),
                    loginDto.getPassword()
            ));
            
            Optional<Employee> emailExists = employeeRepository.findByEmail(loginDto.getEmail());
            if(emailExists.isEmpty()){
                return new BaseResponse(
                        HttpServletResponse.SC_OK,
                        "Username or password invalid",
                        Optional.empty(),
                        null
                );
            }
            Employee employee = emailExists.get();
            String token = jwtService.generateToken(employee);
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("firstName", employee.getFirstName());
            userDetails.put("lastName", employee.getLastName());
            userDetails.put("phoneNumber", employee.getPhoneNumber());
            userDetails.put("email", employee.getEmail());

            Map<String, String> userToken = new HashMap<>();
            userToken.put("token", token);

            userDetails.put("jwt", userToken);
            return new BaseResponse(
                    HttpServletResponse.SC_OK,
                    "Logged in successfully",
                    Optional.of(userDetails),
                    null
            );
        } catch (BadCredentialsException e){
            return new BaseResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Wrong Credentials",
                    Optional.empty(),
                    null
            );
        }
        catch (Exception e){
            return new BaseResponse(HttpStatus.BAD_REQUEST.value(),
                    "Authentication Failed",
                    null,
                    e.getMessage());
        }
    }

    @Override
    public BaseResponse updateEmployeeInfo( Long id, UpdateEmployee updateEmployee) {
        try {
            Optional<Employee> checkForEmployee = employeeRepository.findById(id);

            //check if employee exists
            if(checkForEmployee.isEmpty()){
                return BaseResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .description("employee not found")
                        .error(null)
                        .data(null)
                        .build();
            }
            Employee employee = checkForEmployee.get();

            // Check if the current employee is an admin
            if (!jwtService.hasAdminRole(employee)) {
                return BaseResponse.builder()
                        .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                        .description("admins only")
                        .data(Optional.empty())
                        .data(Optional.empty())
                        .build();
            }

            employee.setFirstName(updateEmployee.getFirstName());
            employee.setLastName(updateEmployee.getLastName());
            employee.setMiddleName(updateEmployee.getMiddleName()); // Assuming MiddleName is part of UserUpdateDto
            employee.setPhoneNumber(updateEmployee.getPhoneNumber());

            employeeRepository.save(employee);
            return BaseResponse.builder()
                    .status(HttpStatus.OK.value())
                    .description("employee updated")
                    .data(Optional.empty())
                    .error(null)
                    .build();
    } catch (Exception e) {
            return BaseResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .description("ERROR")
                    .data(Optional.empty())
                    .error(null)
                    .build();
        }
    }
    @Override
    public EmployeeDto viewEmployeeById(Long id) {
        try{
            Optional<Employee> employeeId = employeeRepository.findById(id);
            if(employeeId.isEmpty()){
                throw new NoSuchElementException("Employee does not exist");
            }
            Employee employee = employeeId.get();
            return EmployeeDto.builder()
                    .firstName(employee.getFirstName())
                    .lastName(employee.getLastName())
                    .middleName(employee.getMiddleName())
                    .profilePhoto(employee.getProfilePhoto().getFileName())
                    .phoneNumber(employee.getPhoneNumber())
                    .build();
        }catch (Exception e){
            throw new RuntimeException("Error");
        }
    }
}   

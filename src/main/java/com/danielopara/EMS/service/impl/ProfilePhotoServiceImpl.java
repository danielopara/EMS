package com.danielopara.EMS.service.impl;

import com.danielopara.EMS.entity.Employee;
import com.danielopara.EMS.entity.ProfilePhoto;
import com.danielopara.EMS.repository.EmployeeRepository;
import com.danielopara.EMS.repository.ProfilePhotoRepository;
import com.danielopara.EMS.service.ProfilePhotoService;
import com.danielopara.EMS.utils.CompressionUtils;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
public class ProfilePhotoServiceImpl implements ProfilePhotoService {
    private final ProfilePhotoRepository profilePhotoRepository;
    private final EmployeeRepository employeeRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProfilePhotoServiceImpl.class);
    private static final String UPLOAD_DIR = "uploads/";

    public ProfilePhotoServiceImpl(ProfilePhotoRepository profilePhotoRepository, EmployeeRepository employeeRepository) {
        this.profilePhotoRepository = profilePhotoRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    @Override
    public String AddProfilePhoto(Long id, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file to upload.");
        }

        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isEmpty()) {
            throw new IllegalArgumentException("Employee not found.");
        }
        Employee employee = employeeOpt.get();

        byte[] fileBytes = file.getBytes();
        byte[] compressedData = CompressionUtils.compressImage(fileBytes); // Use compression

//        Path path = Paths.get(UPLOAD_DIR, file.getOriginalFilename());
//        Files.createDirectories(path.getParent()); // Ensure the upload directory exists
//        Files.write(path, compressedData); // Save compressed data

        ProfilePhoto profilePhoto = employee.getProfilePhoto();
        if (profilePhoto == null) {
            profilePhoto = new ProfilePhoto();
            profilePhoto.setEmployee(employee);
        }

        // Update the profile photo with compressed data and filename
        profilePhoto.setData(compressedData);
        profilePhoto.setImageData(compressedData);
        profilePhoto.setFileName(file.getOriginalFilename());

        profilePhotoRepository.save(profilePhoto);
        employee.setProfilePhoto(profilePhoto); // Update employee with new photo if necessary

        // Uncomment if logging is needed
         logger.info("Uploaded profile photo for employee ID " + id);

        return "You successfully uploaded '" + file.getOriginalFilename() + "'";
    }


    @Override
    public String deleteProfilePhoto(Long id) {
        Optional<Employee> employeeOptional = employeeRepository.findById(id);
        if (employeeOptional.isPresent()) {
            Employee employee = employeeOptional.get();
            ProfilePhoto profilePhoto = employee.getProfilePhoto();

            if (profilePhoto != null) {
                profilePhotoRepository.delete(profilePhoto);
                employee.setProfilePhoto(null);
                employeeRepository.save(employee);
                return "Profile photo deleted";
            } else {
                return "No profile photo found for the employee";
            }
        } else {
            return "Employee not found with the provided ID";
        }
    }

    @Override
    public byte[] getProfilePhoto(Long id) {
        Optional<Employee> byId = employeeRepository.findById(id);
        if(byId.isEmpty()){
            return null;
        }
        Employee employee = byId.get();
        byte[] data = employee.getProfilePhoto().getData();
        return CompressionUtils.decompressImage(data);
    }
}

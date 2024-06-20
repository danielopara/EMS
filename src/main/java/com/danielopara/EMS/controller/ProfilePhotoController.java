package com.danielopara.EMS.controller;

import com.danielopara.EMS.service.impl.ProfilePhotoServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("api/v1/profile-photo")
public class ProfilePhotoController {
    private final ProfilePhotoServiceImpl profilePhotoService;

    public ProfilePhotoController(ProfilePhotoServiceImpl profilePhotoService) {
        this.profilePhotoService = profilePhotoService;
    }


    @PostMapping(value = "/upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@PathVariable("id") Long id,
                                             @RequestParam("file") MultipartFile file) {
        try {
            String message = profilePhotoService.AddProfilePhoto(id, file);
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file.");
        }
    }

    @DeleteMapping("/delete-photo/{id}")
    public ResponseEntity<String> deleteProfilePhoto(@PathVariable("id") Long id){
    try{
        String response = profilePhotoService.deleteProfilePhoto(id);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file.");
    }
    }

    @GetMapping("/get-profilephoto/{id}")
    public ResponseEntity<?> getProfilePhoto(@PathVariable("id")Long id){
        try{
            byte[] profilePhoto = profilePhotoService.getProfilePhoto(id);
//            String contentType = Files.probeContentType(Paths.get(profilePhoto.));

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("image/png"))
                    .body(profilePhoto);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file.");
        }
    }

}

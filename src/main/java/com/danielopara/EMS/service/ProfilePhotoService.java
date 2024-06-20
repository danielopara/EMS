package com.danielopara.EMS.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface ProfilePhotoService {
    String AddProfilePhoto(Long id, MultipartFile file) throws IOException;
    String deleteProfilePhoto(Long id);
//    byte viewProfilePhoto
    byte[] getProfilePhoto(Long id);
}

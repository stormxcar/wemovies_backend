package com.example.demo.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UpdateProfileRequest {
    @Size(max = 100, message = "Tên không được dài quá 100 ký tự")
    private String fullName;

    @Size(max = 50, message = "Tên người dùng không được dài quá 50 ký tự")
    private String userName;

    @Size(max = 15, message = "Số điện thoại không được dài quá 15 ký tự")
    private String phoneNumber;

    @Size(max = 255, message = "Địa chỉ không được dài quá 255 ký tự")
    private String address;

    private String gender; // Có thể dùng String hoặc enum

    private Date dateOfBirth;

    private String avatar; // URL từ Cloudinary
}
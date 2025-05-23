
package com.example.demo.services;


import com.example.demo.models.Admin;

public interface AdminService {
    Admin findByUserName(String userName);

}


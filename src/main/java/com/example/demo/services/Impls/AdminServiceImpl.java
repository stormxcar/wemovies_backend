

package com.example.demo.services.Impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.models.Admin;
import com.example.demo.repositories.AdminRepository;
import com.example.demo.services.AdminService;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Override
    public Admin findByUserName(String userName) {
        return adminRepository.findByUsername(userName);
    }
}

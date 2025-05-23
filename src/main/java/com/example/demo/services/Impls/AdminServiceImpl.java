/*
 * @ (#) AdminServiceImpl.java 1.0 12/23/2024
 *
 * Copyright (c) 2024 IUH.All rights reserved
 */

package com.example.demo.services.Impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.models.Admin;
import com.example.demo.repositories.AdminRepository;
import com.example.demo.services.AdminService;

/*
 * @description
 * @author : Nguyen Truong An
 * @date : 12/23/2024
 * @version 1.0
 */
@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Override
    public Admin findByUserName(String userName) {
        return adminRepository.findByUsername(userName);
    }
}

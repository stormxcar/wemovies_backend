/*
 * @ (#) AdminRepository.java 1.0 12/23/2024
 *
 * Copyright (c) 2024 IUH.All rights reserved
 */
package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.models.Admin;

/*
 * @description
 * @author : Nguyen Truong An
 * @date : 12/23/2024
 * @version 1.0
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Admin findByUsername(String username);
}

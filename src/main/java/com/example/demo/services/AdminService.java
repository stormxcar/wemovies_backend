/*
 * @ (#) AdminService.java 1.0 12/23/2024
 *
 * Copyright (c) 2024 IUH.All rights reserved
 */
package com.example.demo.services;

/*
 * @description
 * @author : Nguyen Truong An
 * @date : 12/23/2024
 * @version 1.0
 */

import com.example.demo.models.Admin;

public interface AdminService {
    Admin findByUserName(String userName);

}


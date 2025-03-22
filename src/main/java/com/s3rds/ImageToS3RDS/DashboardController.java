package com.s3rds.ImageToS3RDS;

import org.springframework.stereotype.Controller;

@Controller
public class DashboardController {
    public String dashboard() {
        return "dashboard";
    }
}

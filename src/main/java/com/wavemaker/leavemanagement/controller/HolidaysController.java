package com.wavemaker.leavemanagement.controller;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.Holiday;
import com.wavemaker.leavemanagement.service.HolidayService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/holidays")
public class HolidaysServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(HolidaysServlet.class);
    @Autowired
    private HolidayService holidayService;

    @GetMapping("/getHolidays")
    private List<Holiday> getHolidays(HttpServletRequest request, HttpServletResponse response) throws ServerUnavailableException {
        String jsonResponse = null;
        List<Holiday> holidays = holidayService.getUpcomingHolidays();
        return holidays;
    }
}



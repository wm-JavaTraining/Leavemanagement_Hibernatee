package com.wavemaker.leavemanagement.service.impl;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.Holiday;
import com.wavemaker.leavemanagement.repository.HolidayRepository;
import com.wavemaker.leavemanagement.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HolidayServiceImpl implements HolidayService {
    @Autowired
    private HolidayRepository holidayRepository;

    @Override
    public List<Holiday> getUpcomingHolidays() throws ServerUnavailableException {
        return holidayRepository.getUpcomingHolidays();

    }
}

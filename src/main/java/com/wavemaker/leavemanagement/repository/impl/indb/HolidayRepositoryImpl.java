package com.wavemaker.leavemanagement.repository.impl.indb;

import com.wavemaker.leavemanagement.exception.ServerUnavailableException;
import com.wavemaker.leavemanagement.model.Holiday;
import com.wavemaker.leavemanagement.repository.HolidayRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class HolidayRepositoryImpl implements HolidayRepository {

    @Autowired
    private HibernateTemplate hibernateTemplate;
    private static final String SELECT_HOLIDAYS_QUERY =
            "select * from Holiday";
    private static Logger logger = LoggerFactory.getLogger(HolidayRepositoryImpl.class);

    @Override
    @Transactional
    public List<Holiday> getUpcomingHolidays() throws ServerUnavailableException {
        List<Holiday> holidays = new ArrayList<>();
        Session session = hibernateTemplate.getSessionFactory().openSession();
        try {
            Query<Holiday> query = session.createQuery(SELECT_HOLIDAYS_QUERY, Holiday.class);
            logger.debug("Fetching upcoming holidays.");
            holidays = query.list();

            logger.debug("Retrieved {} upcoming holidays.", holidays.size());
        } catch (Exception e) {
            logger.error("Error fetching upcoming holidays", e);
            throw new ServerUnavailableException("Server is unavailable to fetch upcoming holidays", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return holidays;

    }
}

package com.example.studyE.configuaration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class HibernateFilterAspect {

    @PersistenceContext
    private EntityManager entityManager;

    @Before("execution(* com.example.studyE.repository..*(..))")
    public void enableFilter() {

        Session session = entityManager.unwrap(Session.class);

        try {
            session.enableFilter("deletedFilter")
                    .setParameter("isDeleted", false);
        } catch (IllegalStateException e) {
        }
    }
}

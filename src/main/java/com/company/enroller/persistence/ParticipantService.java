package com.company.enroller.persistence;

import com.company.enroller.model.Participant;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;


@Component("participantService")
public class ParticipantService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    DatabaseConnector connector;

    public ParticipantService() {
        connector = DatabaseConnector.getInstance();
    }


    public Collection<Participant> getAll(String sortBy, String sortOrder, String key) {
        String hql = buildHqlQuery(sortBy, sortOrder, key);
        Query<Participant> query = connector.getSession().createQuery(hql, Participant.class);
        setQueryParameters(query, key);
        Collection<Participant> participants = query.getResultList();

        if (participants.isEmpty()) {
            throw new EntityNotFoundException("No participants found.");
        }
        return participants;
    }


    public Participant findByLogin(String login) {
        return connector.getSession().get(Participant.class, login);
    }

    public Participant add(Participant participant) {
        Transaction transaction = connector.getSession().beginTransaction();
        String hashedPassword = passwordEncoder.encode(participant.getPassword());
        participant.setPassword(hashedPassword);
        connector.getSession().save(participant);
        transaction.commit();
        return participant;
    }

    public void update(Participant participant) {
        Transaction transaction = connector.getSession().beginTransaction();
        connector.getSession().merge(participant);
        transaction.commit();
    }

    public void delete(Participant participant) {
        Transaction transaction = connector.getSession().beginTransaction();
        connector.getSession().delete(participant);
        transaction.commit();
    }


    private String buildHqlQuery(String sortBy, String sortOrder, String key) {
        StringBuilder hql = new StringBuilder("FROM Participant p");

        appendWhereClause(hql, key);
        appendOrderByClause(hql, sortBy, sortOrder);

        return hql.toString();
    }

    private void appendWhereClause(StringBuilder hql, String key) {
        if (hasText(key)) {
            hql.append(" WHERE p.login LIKE :key");
        }
    }

    private void appendOrderByClause(StringBuilder hql, String sortBy, String sortOrder) {
        if (hasText(sortBy)) {
            hql.append(" ORDER BY p.").append(sortBy);
            hql.append(" ").append(validateSortOrder(sortOrder));
        }
    }

    private void setQueryParameters(Query<Participant> query, String key) {
        if (hasText(key)) {
            query.setParameter("key", "%" + key + "%");
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String validateSortOrder(String sortOrder) {
        if (!hasText(sortOrder)) {
            return "ASC"; // default
        }
        if (sortOrder.equalsIgnoreCase("ASC") || sortOrder.equalsIgnoreCase("DESC")) {
            return sortOrder.toUpperCase();
        }
        throw new IllegalArgumentException("Invalid sortOrder value. Use 'ASC' or 'DESC'.");
    }


}

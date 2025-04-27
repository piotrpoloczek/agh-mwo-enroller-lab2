package com.company.enroller.persistence;

import com.company.enroller.model.Participant;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component("participantService")
public class ParticipantService {

    DatabaseConnector connector;

    public ParticipantService() {
        connector = DatabaseConnector.getInstance();
    }


    public Collection<Participant> getAll(String sortBy, String sortOrder, String key) {
        String hql = buildHqlQuery(sortBy, sortOrder, key);
        Query<Participant> query = connector.getSession().createQuery(hql, Participant.class);
        setQueryParameters(query, key);
        return query.getResultList();
    }

    public Participant findByLogin(String login) {
        return connector.getSession().get(Participant.class, login);
    }

    public Participant add(Participant participant) {
        Transaction transaction = connector.getSession().beginTransaction();
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

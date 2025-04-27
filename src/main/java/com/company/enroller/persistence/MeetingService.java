package com.company.enroller.persistence;

import com.company.enroller.model.Meeting;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("meetingService")
public class MeetingService {

	Session session;

	public MeetingService() {
		session = DatabaseConnector.getInstance().getSession();
	}

	public Collection<Meeting> getAll() {
		String hql = "FROM Meeting";
		Query query = this.session.createQuery(hql);
		return query.list();
	}

	public Meeting findById(long id) {
		return session.get(Meeting.class, id);
	}

	public void add(Meeting meeting) {
		session.beginTransaction();
		session.save(meeting);
		session.getTransaction().commit();
	}

	public void delete(Meeting meeting) {
		session.beginTransaction();
		session.delete(meeting);
		session.getTransaction().commit();
	}

	public void update(Meeting meeting) {
		session.beginTransaction();
		session.update(meeting);
		session.getTransaction().commit();
	}
}

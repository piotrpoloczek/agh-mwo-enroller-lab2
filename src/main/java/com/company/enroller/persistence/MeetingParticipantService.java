package com.company.enroller.persistence;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class MeetingParticipantService {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private ParticipantService participantService;

    Session session;

    public MeetingParticipantService() {
        session = DatabaseConnector.getInstance().getSession();
    }

    public Collection<Participant> getParticipants(long meetingId) {
        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            throw new IllegalArgumentException("Meeting not found");
        }
        return meeting.getParticipants();
    }

    public void addParticipant(long meetingId, String participantLogin) {
        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            throw new IllegalArgumentException("Meeting not found");
        }

        Participant participant = participantService.findByLogin(participantLogin);
        if (participant == null) {
            throw new IllegalArgumentException("Participant not found");
        }

        meeting.addParticipant(participant);
        meetingService.update(meeting);
    }

    public void removeParticipant(long meetingId, String participantLogin) {
        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            throw new IllegalArgumentException("Meeting not found");
        }

        Participant participant = participantService.findByLogin(participantLogin);
        if (participant == null) {
            throw new IllegalArgumentException("Participant not found");
        }

        meeting.removeParticipant(participant);
        meetingService.update(meeting);
    }
}

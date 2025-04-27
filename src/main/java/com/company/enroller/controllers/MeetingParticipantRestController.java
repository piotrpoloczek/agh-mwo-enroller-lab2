package com.company.enroller.controllers;

import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/meetings/{meetingId}/participants")
public class MeetingParticipantRestController {

    @Autowired
    private MeetingParticipantService meetingParticipantService;

    // GET meetings/{id}/participants
    @GetMapping
    public ResponseEntity<?> getParticipants(@PathVariable("meetingId") long meetingId) {
        try {
            Collection<Participant> participants = meetingParticipantService.getParticipants(meetingId);
            return ResponseEntity.ok(participants);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST meetings/{id}/participants
    @PostMapping
    public ResponseEntity<?> addParticipant(@PathVariable("meetingId") long meetingId,
                                            @RequestBody Map<String, String> body) {
        String participantLogin = body.get("login");
        if (participantLogin == null) {
            return ResponseEntity.badRequest().body("Login is required");
        }
        try {
            meetingParticipantService.addParticipant(meetingId, participantLogin);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE meetings/{id}/participants/{login}
    @DeleteMapping("/{login}")
    public ResponseEntity<?> removeParticipant(@PathVariable("meetingId") long meetingId,
                                               @PathVariable("login") String participantLogin) {
        try {
            meetingParticipantService.removeParticipant(meetingId, participantLogin);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

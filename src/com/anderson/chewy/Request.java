package com.anderson.chewy;

import com.anderson.chewy.api.Payload;
import com.anderson.chewy.api.Slack;

import java.util.ArrayList;

public class Request {

    public static final int MIN_TICKET_LENGTH = 10;
    public static final int MAX_TICKET_LENGTH = 11;
    public static final int MIN_DESCRIPTION_LENGTH = 4;
    public static final int MAX_DESCRIPTION_LENGTH = 100;
    public static final int MIN_EMAIL_LENGTH = 2;

    private Reason reason;
    private String description;
    private String email;
    private String name;
    private String ticket;
    private ArrayList<String> equipment;
    private Slack slack = new Slack();

    public Request(Reason reason) {
       this.reason = reason;

       if(reason == Reason.PICKUP) {
           equipment = new ArrayList<>();
       }
    }

    public Reason getReason() {
        return reason;
    }

    public String getTicket() {
        return ticket;
    }

    public boolean setTicket(String ticket) {
        if (ticket == null) {
            this.ticket = null;
            return true;
        }
        else if (isValidTicket(ticket)) {
            this.ticket = ticket;
            return true;
        }
        return false;
    }

    public String getEmail() {
        return email;
    }

    public boolean setEmail(String email) {
        if (email == null) {
            this.email = null;
            setName(null);
            return true;
        }

        var result = slack.lookupByEmail(email);
        if (result != null) {
            this.email = email;
            setName(result);
            return true;
        }
        return false;
    }

    public String getName() {
        return this.name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public void clearUserInfo() {
        email = null;
        name = null;
    }

    public String getDescription() {
       return description;
    }

    public void addDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getEquipment() {
        return equipment;
    }

    public void addEquipment(String equipment) {
        if (this.equipment == null) {
            this.equipment = new ArrayList<>();
        }
        this.equipment.add(equipment);
    }

    public void removeEquipment(String equipment) {
        this.equipment.remove(equipment);
    }

    private boolean isValidTicket(String ticket) {
        return (ticket.length() >= MIN_TICKET_LENGTH && ticket.length() <= MAX_TICKET_LENGTH)
                && (ticket.contains("INC") || ticket.contains("REQ") || ticket.contains("RITM"))
                && (ticket.length() != MAX_TICKET_LENGTH || ticket.contains("RITM")
                && !ticket.contains("1234567"));
    }

    public void submit() {
        slack.chatPostMessage(buildPayload());
    }

    private Payload buildPayload() {
        String reasonFormatted =  reason.name().charAt(0) + reason.name().substring(1).toLowerCase();
        boolean isAppointmentOrReturn = reason == Reason.APPOINTMENT || reason == Reason.RETURN;

        String header = isAppointmentOrReturn ? reasonFormatted : reasonFormatted + " Request";

        StringBuilder body = new StringBuilder();
        body.append("\n>*Name:*  ").append(name).append("  |  ").append(email);
        body.append(ticket != null ? "\n>*Ticket#*  `" + ticket + "`" : "");
        body.append(isAppointmentOrReturn ? "\n>*Reason:*  `" + reason + "`" : "");
        body.append(reason == Reason.SUPPORT && description != null ? "\n>*Issue:*  ```" + description + "```" : "");

        if (reason == Reason.PICKUP && equipment != null) {
            if (equipment.size() == 1) {
                body.append("\n>*Item:*     `").append(equipment.getFirst()).append("`");
            }
            else {
                body.append("\n>*Items:*");
                for (String item : equipment) {
                    body.append("\n>`").append(item).append("`");
                }
            }
        }

        return Slack.payload()
                .header(header)
                .text(body.toString())
                .build();
    }

    public void clear() {
        reason = null;
        description = null;
        email = null;
        name = null;
        ticket = null;
        equipment = null;
    }
}

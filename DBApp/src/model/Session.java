package model;

/**
/**
 * Session.java
 * 
 * This class stores information about the currently logged-in user or officer.
 * It acts like a mini "session manager" shared across all menus and services.
 * 
 * When someone logs in, their ID and role are stored here.
 * When they log out, the session resets.
 */
public class Session {
    public static int loggedInOwnerId = -1;
    public static int loggedInOfficerId = -1;
    public static String loggedInRole = null; // "owner" or "officer"

    public static void clear() {
        loggedInOwnerId = -1;
        loggedInOfficerId = -1;
        loggedInRole = null;
    }
}

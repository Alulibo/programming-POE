package com.mycompany.newmain;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Message class for PROG5121 POE - Parts 2 and 3.
 * Handles creating, validating, sending, storing (JSON), searching,
 * deleting and reporting on messages.
 */
public class Message {

    private static final int MAX_LENGTH = 250;
    private static final Random RANDOM = new Random();

    // ---- Part 3 arrays (all populated by the user / from the JSON file, never hard-coded) ----
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> disregardedMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();
    private static final List<String> messageHashes = new ArrayList<>();
    private static final List<String> messageIDs = new ArrayList<>();

    private static int totalSent = 0;
    private static String jsonFile = "messages.json";
    private static String sender = "You";

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String message;
    private final String messageHash;

    // Normal constructor: message ID is randomly generated (10 digits)
    public Message(String recipient, String message) {
        this(generateMessageID(), recipient, message);
    }

    // Constructor that accepts an ID (used by unit tests)
    public Message(String messageID, String recipient, String message) {
        this.messageID = messageID;
        this.messageNumber = totalSent; // 0 for the first message sent
        this.recipient = recipient;
        this.message = message;
        this.messageHash = createMessageHash();
    }

    // Constructor used when a message is read back from the JSON file
    private Message(String messageID, String messageHash, String recipient, String message, boolean fromFile) {
        this.messageID = messageID;
        this.messageNumber = 0;
        this.recipient = recipient;
        this.message = message;
        this.messageHash = messageHash;
    }

    // ---------- Message ID ----------

    private static String generateMessageID() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    /** Message ID must be no more than ten characters.
     * @return  */
    public boolean checkMessageID() {
        return messageID != null && !messageID.isEmpty() && messageID.length() <= 10;
    }

    public String describeMessageID() {
        return "Message ID generated: " + messageID;
    }

    // ---------- Recipient ----------

    /** Recipient must start with an international code and have a number portion of up to ten digits.
     * @return  */
    public String checkRecipientCell() {
        if (recipient != null && Pattern.matches("^\\+[0-9]{1,3}[0-9]{7,10}$", recipient)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an "
                + "international code, please correct the number and try again.";
    }

    // ---------- Message length ----------

    public static String checkMessageLength(String text) {
        if (text != null && text.length() > MAX_LENGTH) {
            int over = text.length() - MAX_LENGTH;
            return "Message exceeds 250 characters by " + over + "; please reduce the size.";
        }
        return "Message ready to send.";
    }

    // ---------- Message hash ----------

    /** First two digits of ID : message number : FIRSTWORDLASTWORD (all caps).
     * @return  */
    public final String createMessageHash() {
        String[] words = message.trim().split("\\s+");
        String first = clean(words[0]);
        String last = clean(words[words.length - 1]);
        String idStart = messageID.substring(0, Math.min(2, messageID.length()));
        return (idStart + ":" + messageNumber + ":" + first + last).toUpperCase();
    }

    private static String clean(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    // ---------- Send / disregard / store ----------

    /**
     * @param choice 1 = send, 2 = disregard, 3 = store to send later
     * @return the message the system should display
     */
    public String sentMessage(int choice) {
        switch (choice) {
            case 1 -> {
                sentMessages.add(this);
                trackHashAndID();
                totalSent++;
                return "Message successfully sent.";
            }
            case 2 -> {
                disregardedMessages.add(this);
                trackHashAndID();
                return "Press 0 to delete the message.";
            }
            case 3 -> {
                storedMessages.add(this);
                trackHashAndID();
                storeMessage();
                return "Message successfully stored.";
            }
            default -> {
                return "Invalid option.";
            }
        }
    }

    private void trackHashAndID() {
        messageHashes.add(messageHash);
        messageIDs.add(messageID);
    }

    // ---------- JSON storage ----------

    /** Writes every stored message to a JSON file. */
    public static void storeMessage() {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            json.append("  {\n")
                .append("    \"messageID\": \"").append(escape(m.messageID)).append("\",\n")
                .append("    \"messageHash\": \"").append(escape(m.messageHash)).append("\",\n")
                .append("    \"recipient\": \"").append(escape(m.recipient)).append("\",\n")
                .append("    \"message\": \"").append(escape(m.message)).append("\"\n")
                .append("  }");
            if (i < storedMessages.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("]");
        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(json.toString());
        } catch (IOException e) {
            System.out.println("Could not write JSON file: " + e.getMessage());
        }
    }

    /** Reads the JSON file back into the Stored Messages array. */
    public static void loadStoredMessages() {
        File file = new File(jsonFile);
        if (!file.exists()) {
            return;
        }
        try {
            String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            List<String> tokens = jsonStrings(content);
            storedMessages.clear();
            // Each object is written as 4 "key": "value" pairs = 8 string tokens
            for (int i = 0; i + 7 < tokens.size(); i += 8) {
                storedMessages.add(new Message(
                        tokens.get(i + 1), tokens.get(i + 3), tokens.get(i + 5), tokens.get(i + 7), true));
            }
        } catch (IOException e) {
            System.out.println("Could not read JSON file: " + e.getMessage());
        }
    }

    /** Pulls every quoted string out of the JSON text (handles escaped characters). */
    private static List<String> jsonStrings(String text) {
        List<String> out = new ArrayList<>();
        int i = 0;
        while (i < text.length()) {
            if (text.charAt(i) == '"') {
                StringBuilder sb = new StringBuilder();
                i++;
                while (i < text.length() && text.charAt(i) != '"') {
                    char c = text.charAt(i);
                    if (c == '\\' && i + 1 < text.length()) {
                        i++;
                        char next = text.charAt(i);
                        switch (next) {
                            case 'n' -> sb.append('\n');
                            case 'r' -> sb.append('\r');
                            case 't' -> sb.append('\t');
                            default -> sb.append(next);
                        }
                    } else {
                        sb.append(c);
                    }
                    i++;
                }
                out.add(sb.toString());
            }
            i++;
        }
        return out;
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    // ---------- Display (Part 2) ----------

    /** Full details in the required order: ID, Hash, Recipient, Message.
     * @return  */
    public String getDetails() {
        return "Message ID: " + messageID + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + message;
    }

    /** Returns all messages sent while the program is running.
     * @return  */
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append(m.getDetails()).append("\n\n");
        }
        return sb.toString().trim();
    }

    public static int returnTotalMessages() {
        return totalSent;
    }

    // ---------- Part 3: array contents ----------

    public static List<String> getSentMessages() { return texts(sentMessages); }
    public static List<String> getDisregardedMessages() { return texts(disregardedMessages); }
    public static List<String> getStoredMessages() { return texts(storedMessages); }
    public static List<String> getMessageHashes() { return new ArrayList<>(messageHashes); }
    public static List<String> getMessageIDs() { return new ArrayList<>(messageIDs); }

    private static List<String> texts(List<Message> list) {
        List<String> out = new ArrayList<>();
        for (Message m : list) {
            out.add(m.message);
        }
        return out;
    }

    // ---------- Part 3: stored message features ----------

    /** a. Display the sender and recipient of all stored messages.
     * @return  */
    public static String displaySenderAndRecipient() {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : storedMessages) {
            sb.append("Sender: ").append(sender)
              .append(" | Recipient: ").append(m.recipient).append("\n");
        }
        return sb.toString().trim();
    }

    /** b. Display the longest stored message.
     * @return  */
    public static String longestStoredMessage() {
        Message longest = null;
        for (Message m : storedMessages) {
            if (longest == null || m.message.length() > longest.message.length()) {
                longest = m;
            }
        }
        return longest == null ? "No stored messages." : longest.message;
    }

    private static Message findByID(String id) {
        for (List<Message> list : List.of(sentMessages, storedMessages, disregardedMessages)) {
            for (Message m : list) {
                if (m.messageID.equals(id)) {
                    return m;
                }
            }
        }
        return null;
    }

    /** c. Search for a message ID; returns the message text.
     * @param id
     * @return  */
    public static String searchByMessageID(String id) {
        Message m = findByID(id);
        return m == null ? "Message ID not found." : m.message;
    }

    /** c. Same search, but with the recipient and message for display.
     * @param id
     * @return  */
    public static String searchByMessageIDDetails(String id) {
        Message m = findByID(id);
        return m == null ? "Message ID not found."
                : "Recipient: " + m.recipient + "\nMessage: " + m.message;
    }

    /** d. All messages sent or stored for a particular recipient.
     * @param recipientNumber
     * @return  */
    public static List<String> searchByRecipient(String recipientNumber) {
        List<String> found = new ArrayList<>();
        for (Message m : sentMessages) {
            if (m.recipient.equals(recipientNumber)) {
                found.add(m.message);
            }
        }
        for (Message m : storedMessages) {
            if (m.recipient.equals(recipientNumber)) {
                found.add(m.message);
            }
        }
        return found;
    }

    /** e. Delete a message using its message hash.
     * @param hash
     * @return  */
    public static String deleteByHash(String hash) {
        Message target = removeByHash(storedMessages, hash);
        boolean wasStored = target != null;
        if (target == null) {
            target = removeByHash(sentMessages, hash);
        }
        if (target == null) {
            return "Message hash not found.";
        }
        messageHashes.remove(target.messageHash);
        messageIDs.remove(target.messageID);
        if (wasStored) {
            storeMessage(); // keep the JSON file in step with the array
        }
        return "Message \"" + target.message + "\" successfully deleted.";
    }

    private static Message removeByHash(List<Message> list, String hash) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).messageHash.equals(hash)) {
                return list.remove(i);
            }
        }
        return null;
    }

    /** Report of all sent messages: hash, recipient and message.
     * @return  */
    public static String displayReport() {
        return report(sentMessages, "No messages have been sent yet.");
    }

    /** f. Report with the full details of all stored messages.
     * @return  */
    public static String displayStoredReport() {
        return report(storedMessages, "No stored messages.");
    }

    private static String report(List<Message> list, String emptyText) {
        if (list.isEmpty()) {
            return emptyText;
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : list) {
            sb.append("Message Hash: ").append(m.messageHash).append("\n")
              .append("Recipient: ").append(m.recipient).append("\n")
              .append("Message: ").append(m.message).append("\n\n");
        }
        return sb.toString().trim();
    }

    // ---------- Getters / helpers ----------

    public String getMessageID() { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }

    public static void setJsonFile(String path) { jsonFile = path; }
    public static void setSender(String name) { sender = name; }

    /** Clears all arrays and counters (used by unit tests). */
    public static void resetState() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        totalSent = 0;
    }

    // ---------- Console flow ----------

    /** Lets Message.java run on its own.
     * @param args */
    public static void main(String[] args) {
        startMessaging(new Scanner(System.in));
    }

    public static void startMessaging(Scanner in, String senderName) {
        setSender(senderName);
        startMessaging(in);
    }

    public static void startMessaging(Scanner in) {
        System.out.println("\nWelcome to QuickChat.");
        boolean running = true;
        while (running) {
            System.out.println("\nMenu:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            int option = readInt(in, "Choose an option: ");
            switch (option) {
                case 1 -> sendMessagesFlow(in);
                case 2 -> System.out.println(printMessages());
                case 3 -> {
                    running = false;
                    System.out.println("Goodbye.");
                }
                case 4 -> storedMessagesMenu(in);
                default -> System.out.println("Please choose 1, 2, 3 or 4.");
            }
        }
    }

    private static void storedMessagesMenu(Scanner in) {
        loadStoredMessages(); // read the JSON file into the Stored Messages array
        boolean back = false;
        while (!back) {
            System.out.println("\nStored Messages:");
            System.out.println("a) Display the sender and recipient of all stored messages");
            System.out.println("b) Display the longest stored message");
            System.out.println("c) Search for a message ID");
            System.out.println("d) Search for all messages stored for a recipient");
            System.out.println("e) Delete a message using the message hash");
            System.out.println("f) Display a report of all stored messages");
            System.out.println("0) Back to main menu");
            System.out.print("Choose an option: ");
            String choice = in.nextLine().trim().toLowerCase();
            switch (choice) {
                case "a" -> System.out.println(displaySenderAndRecipient());
                case "b" -> System.out.println(longestStoredMessage());
                case "c" -> {
                    System.out.print("Enter the message ID: ");
                    System.out.println(searchByMessageIDDetails(in.nextLine().trim()));
                }
                case "d" -> {
                    System.out.print("Enter the recipient number: ");
                    List<String> found = searchByRecipient(in.nextLine().trim());
                    if (found.isEmpty()) {
                        System.out.println("No messages found for that recipient.");
                    } else {
                        for (String text : found) {
                            System.out.println("\"" + text + "\"");
                        }
                    }
                }
                case "e" -> {
                    System.out.print("Enter the message hash: ");
                    System.out.println(deleteByHash(in.nextLine().trim().toUpperCase()));
                }
                case "f" -> System.out.println(displayStoredReport());
                case "0" -> back = true;
                default -> System.out.println("Please choose a, b, c, d, e, f or 0.");
            }
        }
    }

    private static void sendMessagesFlow(Scanner in) {
        int numMessages = readInt(in, "How many messages do you want to enter? ");
        for (int i = 1; i <= numMessages; i++) {
            System.out.println("\n--- Message " + i + " of " + numMessages + " ---");

            String recipient;
            while (true) {
                System.out.print("Recipient cell number (e.g. +27718693002): ");
                recipient = in.nextLine().trim();
                Message check = new Message(recipient, "check");
                String result = check.checkRecipientCell();
                System.out.println(result);
                if (result.startsWith("Cell phone number successfully")) {
                    break;
                }
            }

            String text;
            while (true) {
                System.out.print("Message (max 250 characters): ");
                text = in.nextLine();
                if (text.trim().isEmpty()) {
                    System.out.println("Message cannot be empty.");
                } else if (text.length() > MAX_LENGTH) {
                    System.out.println("Please enter a message of less than 250 characters.");
                    System.out.println(checkMessageLength(text));
                } else {
                    System.out.println(checkMessageLength(text));
                    break;
                }
            }

            Message msg = new Message(recipient, text);
            System.out.println(msg.describeMessageID());

            int choice;
            while (true) {
                System.out.println("1) Send Message");
                System.out.println("2) Disregard Message");
                System.out.println("3) Store Message to send later");
                choice = readInt(in, "Choose an option: ");
                if (choice >= 1 && choice <= 3) {
                    break;
                }
                System.out.println("Please choose 1, 2 or 3.");
            }

            System.out.println(msg.sentMessage(choice));
            if (choice == 1) {
                System.out.println(msg.getDetails());
            } else if (choice == 2) {
                String answer = in.nextLine().trim();
                if (answer.equals("0")) {
                    System.out.println("Message deleted.");
                }
            }
        }
        System.out.println("\nTotal number of messages sent: " + returnTotalMessages());
    }

    private static int readInt(Scanner in, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(in.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }
}
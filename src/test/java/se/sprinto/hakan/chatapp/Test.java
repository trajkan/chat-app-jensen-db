package se.sprinto.hakan.chatapp;

import org.junit.jupiter.api.BeforeEach;
import se.sprinto.hakan.chatapp.dao.MessageDatabaseDAO;
import se.sprinto.hakan.chatapp.dao.UserDatabaseDAO;
import se.sprinto.hakan.chatapp.model.Message;
import se.sprinto.hakan.chatapp.model.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Test {

    @BeforeEach
    void setup() throws SQLException {
        Connection conn = DatabaseUtil.getInstance().getConnection();
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE IF EXISTS messages");
        stmt.execute("DROP TABLE IF EXISTS user");

        stmt.execute("""
                CREATE TABLE user (
                    user_id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) NOT NULL,
                    password VARCHAR(100) NOT NULL
                )
                """
        );

        stmt.execute("""
                CREATE TABLE message (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT NOT NULL,
                    text TEXT NOT NULL,
                    timestamp DATETIME NOT NULL,
                    CONSTRAINT fk_message_user
                        FOREIGN KEY (user_id)
                        REFERENCES user(user_id)
                )"""
        );
    }

    @org.junit.jupiter.api.Test //TODO: check why entire url is written out!!!
    void userAndMessagesTest() throws SQLException{
        UserDatabaseDAO userDAO = new UserDatabaseDAO();
        MessageDatabaseDAO messageDao = new MessageDatabaseDAO();
        DatabaseUtil dbUtil = DatabaseUtil.getInstance();

        User user = new User("Kalle", "Anka");
        userDAO.register(user);
        assertTrue(user.getId() > 0, "User ID should be generated and set");
        int userId = user.getId();

        Message message1 = new Message(userId, "HelloWorld1", LocalDateTime.now());
        Message message2 = new Message(userId, "HelloWorld2", LocalDateTime.now());

        messageDao.saveMessage(message1);
        messageDao.saveMessage(message2);

        List<Message> messageList = messageDao.getMessagesByUserId(userId);
        assertEquals(2, messageList.size(), "Expected 2 messages for this user");
        printTable("user");
        printTable("message");
    }


    private void printTable(String tableName) throws SQLException {
        String sql = "SELECT * FROM " + tableName;
        Connection conn = DatabaseUtil.getInstance().getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        System.out.println("\n--- CONTENT OF TABLE: " + tableName.toUpperCase() + " ---");

        int columnCount = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(rs.getString(i) + "  |  ");
            }
            System.out.println();
        }
        System.out.println("--------------------------------------------\n");
    }



}

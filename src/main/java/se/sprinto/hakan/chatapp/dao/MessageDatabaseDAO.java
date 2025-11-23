package se.sprinto.hakan.chatapp.dao;

import se.sprinto.hakan.chatapp.DatabaseUtil;
import se.sprinto.hakan.chatapp.model.Message;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDatabaseDAO implements MessageDAO{
    DatabaseUtil dbUtil = DatabaseUtil.getInstance();
    @Override
    public void saveMessage(Message message) {
        String sql = """
                INSERT INTO message (user_id, text, timestamp)
                VALUES (?, ?, ?)
                """;
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);){
            ps.setInt(1, message.getUserId());
            ps.setString(2, message.getText());
            ps.setTimestamp(3, Timestamp.valueOf(message.getTimestamp()));

            ps.executeUpdate();
            System.out.println("Message inserted!");
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public List<Message> getMessagesByUserId(int userId) {
        List<Message> messageList = new ArrayList<>();
        String sql = """
                SELECT * FROM message
                WHERE user_id = ?
                """;
        try (Connection conn = dbUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
//                int uid = rs.getInt("user_id");
                String text = rs.getString("text");
                LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();

                Message message = new Message(userId, text, timestamp);
                messageList.add(message);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return messageList;
    }
}

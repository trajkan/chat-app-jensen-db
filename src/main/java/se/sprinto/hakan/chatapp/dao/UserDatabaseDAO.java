package se.sprinto.hakan.chatapp.dao;

import se.sprinto.hakan.chatapp.DatabaseUtil;
import se.sprinto.hakan.chatapp.model.User;

import java.sql.*;

public class UserDatabaseDAO implements UserDAO{
    DatabaseUtil dbUtil = DatabaseUtil.getInstance();

    @Override
    public User login(String username, String password) {
        String sql = """
                SELECT * FROM user
                WHERE username = ? AND password = ?
                """;
        try (Connection conn = dbUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, username);
            ps.setString(2, password);
            System.out.println("Connected to: " + conn.getCatalog());


            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                int id = rs.getInt("user_id");
                String name = rs.getString("username");
                String pass = rs.getString("password");
                return new User(id, name, pass);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User register(User user) {
        String sql = """
                INSERT INTO user (username, password)
                VALUES (?, ?)
                """;
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());

            ps.executeUpdate();
            System.out.println("User inserted!");

            try (ResultSet rs = ps.getGeneratedKeys()){
                if  (rs.next()){
                    int uid = rs.getInt(1);
                    user.setId(uid);
                }
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return user;
    }
}

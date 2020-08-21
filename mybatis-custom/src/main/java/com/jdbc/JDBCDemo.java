package com.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JDBCDemo {
    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/learn?useSSL=false", "root", "123456");

            String sql = "SELECT * FROM user where id = ? ";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, 1);
            result = preparedStatement.executeQuery();
            if (null != result) {
                while (result.next()) {
                    System.out.println(result);
                    System.out.println(result.getInt("id"));
                    System.out.println(result.getString("username"));
                    System.out.println(result.getString("birthday"));
                    System.out.println(result.getString("address"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
            }
        }
    }
}

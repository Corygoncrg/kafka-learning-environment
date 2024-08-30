package br.com.alura.ecommerce.database;

import java.sql.*;

public class LocalDatabase {
    private final Connection connection;



    public LocalDatabase(String name) throws SQLException {
        String url= "jdbc:sqlite:target/" + name + ".db";
        connection = DriverManager.getConnection(url);

    }

    // very generic, avoid injection according to your database tool.
    public void createIfNotExists(String sql) {
        try {
            connection.createStatement().execute(sql);}
        catch (SQLException e) {
            // be careful, the sql could be wrong
            e.printStackTrace();
        }
    }

    public boolean update(String statement, String ... params) throws SQLException {
        return getPreparedStatement(statement, params).execute();
    }

    public ResultSet query(String query, String ... params) throws SQLException {
        return getPreparedStatement(query, params).executeQuery();
    }

    private PreparedStatement getPreparedStatement(String statement, String[] params) throws SQLException {
        var preparedStatement = connection.prepareStatement(statement);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setString(i+1, params[i]);
        }
        return preparedStatement;
    }

    public void close() throws SQLException {
        connection.close();
    }
}

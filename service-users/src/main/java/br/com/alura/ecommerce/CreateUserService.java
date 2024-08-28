package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateUserService {

    private final Connection connection;

    CreateUserService() throws SQLException {
        String url= "jdbc:sqlite:target/users_database.db";
        connection = DriverManager.getConnection(url);
        try {
        connection.createStatement().execute("""
                create table Users (
                uuid varchar(200),
                email varchar(200),
                primary key(uuid)
                );""");}
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
        var userService = new CreateUserService();
        try (var service = new KafkaService<>(CreateUserService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER", userService::parse, Map.of())){
            service.run();
        }
    }
    private void parse(ConsumerRecord<String, Message<Order>> record) throws Exception{
        System.out.println("---------------------------");
        System.out.println("Processing new order, checking for user");
        System.out.println(record.value());
        var order = record.value().getPayload();
        if (isNewUser(order.getEmail())){
            insertNewUser(order.getUserId(), order.getEmail());
        }
    }

    private void insertNewUser(String userId, String email) throws SQLException {
        var insert = connection.prepareStatement("""
                INSERT INTO Users
                (uuid, email)
                values (?, ?);""");
        insert.setString(1, UUID.randomUUID().toString());
        insert.setString(2, email);
        insert.execute();
        System.out.println("User uuid and " + email + "has been added");
    }

    private boolean isNewUser(String email) throws SQLException {
        var exists = connection.prepareStatement("""
                SELECT uuid FROM Users
                WHERE email = ? LIMIT 1""");
        exists.setString(1, email);
        var results = exists.executeQuery();
        return !results.next();
    }


}

package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.ServiceRunner;
import br.com.alura.ecommerce.database.LocalDatabase;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.SQLException;
import java.util.UUID;

public class CreateUserService implements ConsumerService<Order> {

    private final LocalDatabase database;

    CreateUserService() throws SQLException {
        this.database = new LocalDatabase("users_database");
        this.database.createIfNotExists("""
                create table Users (
                uuid varchar(200),
                email varchar(200),
                primary key(uuid)
                );""");
    }

    public static void main(String[] args) {
        new ServiceRunner<>(CreateUserService::new).start(1);

    }
    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return CreateUserService.class.getSimpleName();
    }

    @Override
    public void parse(ConsumerRecord<String, Message<Order>> record) throws Exception {
        System.out.println("---------------------------");
        System.out.println("Processing new order, checking for user");
        System.out.println(record.value());
        var order = record.value().getPayload();
        if (isNewUser(order.getEmail())){
            insertNewUser(order.getEmail());
        }
    }

    private void insertNewUser(String email) throws SQLException {
        String uuid = UUID.randomUUID().toString();
        database.update("""
                INSERT INTO Users
                (uuid, email)
                values (?, ?);""", uuid, email);
        System.out.println("User " + uuid + " and " + email + " has been added");
    }

    private boolean isNewUser(String email) throws SQLException {
        var results = database.query("""
                SELECT uuid FROM Users
                WHERE email = ? LIMIT 1""", email);
        return !results.next();
    }
}

package com.blu.cdp;

import io.quarkus.logging.Log;
import org.postgresql.PGConnection;
import org.postgresql.PGProperty;
import org.postgresql.replication.PGReplicationStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;

/**
 * to run the app, execute the following command
 * mvn compile exec:java -Dexec.mainClass="com.blu.cdp.PostgreSQLConsumer"
 * */
public class PostgreSQLConsumer{
    //private static final String CONN_URL="jdbc:postgresql://localhost:5432/postgres";
    //@Override
    public static void main(String... args) throws Exception {
        System.out.println("PostgreSQL CDP consumer starts...");
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        //System.out.println("path;"+ rootPath);
        InputStream is = new FileInputStream(rootPath+"application.properties");//PostgreSQLConsumer.class.getResourceAsStream(rootPath+"application.properties");

        if(is == null){
            throw new IOException("application.properties file not found");
        }
        Properties config = new Properties();
        config.load(is);

        Properties props = new Properties();
        PGProperty.USER.set(props, config.getProperty("db.user"));
        PGProperty.PASSWORD.set(props, config.getProperty("db.password"));
        PGProperty.ASSUME_MIN_SERVER_VERSION.set(props, "9.4");
        PGProperty.REPLICATION.set(props, "database");
        PGProperty.PREFER_QUERY_MODE.set(props, "simple");

        Connection con = DriverManager.getConnection(config.getProperty("db.url"), props);
        PGConnection replConnection = con.unwrap(PGConnection.class);
        // add replication slot
        replConnection.getReplicationAPI()
                .createReplicationSlot()
                .logical()
                .withSlotName("demo_logical_slot_"+ config.getProperty("repl.logical.slot"))
                .withOutputPlugin("test_decoding")
                .make();

        PGReplicationStream stream =
                replConnection.getReplicationAPI()
                        .replicationStream()
                        .logical()
                        .withSlotName("demo_logical_slot_"+ config.getProperty("repl.logical.slot"))
                        .withSlotOption("include-xids", true)
                        .withSlotOption("skip-empty-xacts", true)
                        .withStatusInterval(20, TimeUnit.SECONDS)
                        .start();


        while (true) {
            //nonblocking receive message
            ByteBuffer msg = stream.readPending();

            if (msg == null) {
                TimeUnit.MILLISECONDS.sleep(10L);
                continue;
            }

            int offset = msg.arrayOffset();
            byte[] source = msg.array();
            int length = source.length - offset;
            System.out.println(new String(source, offset, length));

            //feedback by LOG sequence Number
            stream.setAppliedLSN(stream.getLastReceiveLSN());
            stream.setFlushedLSN(stream.getLastReceiveLSN());

        }

    }
}

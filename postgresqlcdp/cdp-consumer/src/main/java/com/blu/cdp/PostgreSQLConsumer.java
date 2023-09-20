package com.blu.cdp;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.quarkus.logging.Log;
import org.postgresql.PGConnection;
import org.postgresql.PGProperty;
import org.postgresql.replication.PGReplicationStream;

import java.nio.ByteBuffer;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;

/**
 * to run the app, execute the following
 * mvn compile exec:java -Dexec.mainClass="com.blu.cdp.PostgreSQLConsumer"
 * */
public class PostgreSQLConsumer{
    private static final String CONN_URL="jdbc:postgresql://localhost:5432/postgres";
    //@Override
    public static void main(String... args) throws Exception {
        Log.info("PostgreSQL CDP consumer starts...");

        Properties props = new Properties();
        PGProperty.USER.set(props, "postgres");
        PGProperty.PASSWORD.set(props, "postgres");
        PGProperty.ASSUME_MIN_SERVER_VERSION.set(props, "9.4");
        PGProperty.REPLICATION.set(props, "database");
        PGProperty.PREFER_QUERY_MODE.set(props, "simple");

        Connection con = DriverManager.getConnection(CONN_URL, props);
        PGConnection replConnection = con.unwrap(PGConnection.class);
        // add replication slot
        replConnection.getReplicationAPI()
                .createReplicationSlot()
                .logical()
                .withSlotName("demo_logical_slot_9")
                .withOutputPlugin("test_decoding")
                .make();

        PGReplicationStream stream =
                replConnection.getReplicationAPI()
                        .replicationStream()
                        .logical()
                        .withSlotName("demo_logical_slot_9")
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

package com.blu.cdp;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.logging.Log;
import org.json.JSONObject;
import org.postgresql.PGConnection;
import org.postgresql.PGProperty;
import org.postgresql.replication.PGReplicationStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Properties;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;

/**
 * to run the app, execute the following command
 * mvn compile exec:java -Dexec.mainClass="com.blu.cdp.PostgreSQLConsumer"
 * */
public class PostgreSQLConsumer{
    private static final Logger logger = LoggerFactory.getLogger(PostgreSQLConsumer.class);
    //@Override
    public static void main(String... args) throws Exception {
        logger.info("PostgreSQL CDP consumer starts...");
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        //System.out.println("path;"+ rootPath);
        InputStream is = new FileInputStream(rootPath+"application.properties");//PostgreSQLConsumer.class.getResourceAsStream(rootPath+"application.properties");

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
        // delete the previously created replication slot

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
            logger.info(new String(source, offset, length));


            //feedback by LOG sequence Number
            stream.setAppliedLSN(stream.getLastReceiveLSN());
            stream.setFlushedLSN(stream.getLastReceiveLSN());

        }

    }
}

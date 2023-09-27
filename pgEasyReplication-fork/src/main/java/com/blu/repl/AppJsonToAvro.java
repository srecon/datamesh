package com.blu.repl;

import tech.allegro.schema.json2avro.converter.JsonAvroConverter;

public class AppJsonToAvro {
    public static void main(String[] args) {
        System.out.println("JSON to Avro convert!");
//        String schema =
//                "{" +
//                        "   \"type\" : \"record\"," +
//                        "   \"name\" : \"Acme\"," +
//                        "   \"fields\" : [{ \"name\" : \"username\", \"type\" : \"string\" }]" +
//                        "}";
//
//        String json = "{ \"username\": \"mike\" }";

        String schema = "{\n" +
                "\n" +
                "  \"type\": \"record\",\n" +
                "\n" +
                "  \"name\": \"record0\",\n" +
                "\n" +
                "  \"fields\": [\n" +
                "\n" +
                "    {\n" +
                "\n" +
                "      \"name\": \"relationName\",\n" +
                "\n" +
                "      \"type\": \"string\"\n" +
                "\n" +
                "    },\n" +
                "\n" +
                "    {\n" +
                "\n" +
                "      \"name\": \"type\",\n" +
                "\n" +
                "      \"type\": \"string\"\n" +
                "\n" +
                "    },\n" +
                "\n" +
                "    {\n" +
                "\n" +
                "      \"name\": \"tupleData\",\n" +
                "\n" +
                "      \"type\": {\n" +
                "\n" +
                "        \"type\": \"record\",\n" +
                "\n" +
                "        \"name\": \"record\",\n" +
                "\n" +
                "        \"fields\": [\n" +
                "\n" +
                "          {\n" +
                "\n" +
                "            \"name\": \"codigo\",\n" +
                "\n" +
                "            \"type\": \"int\"\n" +
                "\n" +
                "          },\n" +
                "\n" +
                "          {\n" +
                "\n" +
                "            \"name\": \"nome\",\n" +
                "\n" +
                "            \"type\": \"string\"\n" +
                "\n" +
                "          },\n" +
                "          {\n" +
                "\n" +
                "            \"name\": \"data_fund\",\n" +
                "\n" +
                "            \"type\": \"string\"\n" +
                "\n" +
                "          }\n" +
                "\n" +
                "        ]\n" +
                "\n" +
                "      }\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "  ]\n" +
                "\n" +
                "}";

        String json = "{\n" +
                "  \"tupleData\": {\n" +
                "    \"codigo\": 7,\n" +
                "    \"nome\": \"SAO PAULO1\",\n" +
                "    \"data_fund\": \"1554-01-25\"\n" +
                "  },\n" +
                "  \"relationName\": \"public.cidade\",\n" +
                "  \"type\": \"insert\"\n" +
                "  \n" +
                "}  ";


        JsonAvroConverter jsonAvroConverter = new JsonAvroConverter();
        byte[] avro = jsonAvroConverter.convertToAvro(json.getBytes(), schema);
        System.out.println("[AVRO]: "+ new String(avro));
    }
}

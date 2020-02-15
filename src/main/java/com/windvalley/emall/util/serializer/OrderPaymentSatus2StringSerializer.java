package com.windvalley.emall.util.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.windvalley.emall.enums.PayPlatform;

import java.io.IOException;

public class OrderPaymentSatus2StringSerializer extends JsonSerializer<Integer> {
    @Override
    public void serialize(Integer status, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(PayPlatform.getDescriptByCode(status));
    }
}

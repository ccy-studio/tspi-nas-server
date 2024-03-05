package com.saisaiwa.tspi.nas.common.util;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.NumberSerializers;

import java.io.IOException;
import java.math.BigDecimal;

public class DoubleJsonSerializer extends NumberSerializers.DoubleSerializer {
    public DoubleJsonSerializer(Class<?> cls) {
        super(cls);
    }

    @Override
    public void serialize(Object value, JsonGenerator gen,
                          SerializerProvider provider) throws IOException {
        gen.writeNumber(BigDecimal.valueOf((Double) value));
    }
}

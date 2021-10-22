package com.kmschr.brs;

import com.kmschr.brs.io.BitReader;

import java.io.IOException;
import java.util.List;

public class Component {
    String name;
    byte[] data;
    int version;
    List<Integer> brickIndices;
    List<String[]> properties;

    public Component() { }

    public String toString() {
        return name + ": " + data.length + " bytes";
    }

    public void testRead() throws IOException {
        BitReader r = new BitReader(data);
        int start = r.readInt();
        int length = r.readInt();
    }

}

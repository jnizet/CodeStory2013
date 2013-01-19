package com.ninja_squad.jb.codestory;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.google.common.io.LineProcessor;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.fest.assertions.Assertions.*;

/**
 * Tests for HttpLineReader
 * @author JB
 */
public class HttpLineReaderTest {

    private LineProcessor<List<String>> processor;

    @Before
    public void setUp() {
        processor = new LineProcessor<List<String>>() {
            private List<String> lines = Lists.newArrayList();

            @Override
            public boolean processLine(String line) throws IOException {
                lines.add(line);
                return lines.size() != 3;
            }

            @Override
            public List<String> getResult() {
                return lines;
            }
        };
    }

    @Test
    public void readLinesShouldWorkWhenEndsWithLineEnding() throws Exception {
        String text = "Hello\r\nWorld\r\n";
        List<String> lines = new HttpLineReader(new ByteArrayInputStream(text.getBytes(StandardCharsets.US_ASCII)),
                                                StandardCharsets.US_ASCII).readLines(processor);
        assertThat(lines).containsExactly("Hello", "World");
    }

    @Test
    public void readLinesShouldWorkWhenDoesntEndWithLineEnding() throws Exception {
        String text = "Hello\r\nWorld";
        List<String> lines = new HttpLineReader(new ByteArrayInputStream(text.getBytes(StandardCharsets.US_ASCII)),
                                                StandardCharsets.US_ASCII).readLines(processor);
        assertThat(lines).containsExactly("Hello", "World");
    }

    @Test
    public void readLinesShouldWorkWhenLineProcessorWantsToStop() throws Exception {
        String text = "Hello\r\nWorld\r\nGoodbye\r\nWorld\r\n";
        ByteArrayInputStream in = new ByteArrayInputStream(text.getBytes(StandardCharsets.US_ASCII));
        List<String> lines = new HttpLineReader(in,
                                                StandardCharsets.US_ASCII).readLines(processor);
        assertThat(lines).containsExactly("Hello", "World", "Goodbye");
        byte[] restOfStream = ByteStreams.toByteArray(in);
        assertThat(restOfStream).isEqualTo("World\r\n".getBytes(StandardCharsets.US_ASCII));
    }
}

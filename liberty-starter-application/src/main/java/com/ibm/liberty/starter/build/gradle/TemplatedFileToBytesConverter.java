package com.ibm.liberty.starter.build.gradle;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TemplatedFileToBytesConverter {

    private final Pattern tagPattern = Pattern.compile("(%(.*)%)");
    private final InputStream inputStream;
    private final Map<String, String> tags;

    public TemplatedFileToBytesConverter(InputStream inputStream, Map<String, String> tags) {
        this.inputStream = inputStream;
        this.tags = tags;
    }

    public byte[] getBytes() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return reader.lines()
                .map(this::replaceTags)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n")).getBytes();
    }

    private String replaceTags(String line) {
        Matcher matcher = tagPattern.matcher(line);
        if (matcher.find()) {
            String lineWithTagsReplaced = replaceFoundTag(matcher);
            return nullIfEmpty(lineWithTagsReplaced);
        }
        return line;
    }

    private String nullIfEmpty(String lineWithTagsReplaced) {
        if (lineWithTagsReplaced.trim().isEmpty()) {
            return null;
        }
        return lineWithTagsReplaced;
    }

    private String replaceFoundTag(Matcher matcher) {
        String key = matcher.group(2);
        String replacement;
        if (tags.containsKey(key)) {
            replacement = tags.get(key);
        } else {
            replacement = "";
        }
        return matcher.replaceAll(replacement);
    }
}

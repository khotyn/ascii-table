package com.github.freva.asciitable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LineUtils {

    public static Stream<String> lines(String str) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(new LineIterator(str), Spliterator.NONNULL),
                false);
    }

    public static int maxLineLength(String str) {
        int max = 0;
        LineIterator lineIterator = new LineIterator(str);
        while (lineIterator.hasNext()) {
            int start = lineIterator.getPosition();
            max = Math.max(max, calculateStringLength(str.substring(start, lineIterator.getLineEndPositionAndAdvanceToNextLine())));
        }
        return max;
    }

    public static int calculateStringLength(String str) {
        String noColorString = str;
        for (String consoleColor : CONSOLE_COLORS) {
            noColorString = noColorString.replace(consoleColor, "");
        }
        int valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        for (int i = 0; i < noColorString.length(); i++) {
            String temp = noColorString.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }

        return valueLength;
    }

    private static final String[] CONSOLE_COLORS = new String[]{
            "\033[0m",
            "\033[0;30m",
            "\033[0;31m",
            "\033[0;32m",
            "\033[0;33m",
            "\033[0;34m",
            "\033[0;35m",
            "\033[0;36m",
            "\033[0;37m",
            "\033[1;30m",
            "\033[1;31m",
            "\033[1;32m",
            "\033[1;33m",
            "\033[1;34m",
            "\033[1;35m",
            "\033[1;36m",
            "\033[1;37m",
            "\033[4;30m",
            "\033[4;31m",
            "\033[4;32m",
            "\033[4;33m",
            "\033[4;34m",
            "\033[4;35m",
            "\033[4;36m",
            "\033[4;37m",
            "\033[40m",
            "\033[41m",
            "\033[42m",
            "\033[43m",
            "\033[44m",
            "\033[45m",
            "\033[46m",
            "\033[47m",
            "\033[0;90m",
            "\033[0;91m",
            "\033[0;92m",
            "\033[0;93m",
            "\033[0;94m",
            "\033[0;95m",
            "\033[0;96m",
            "\033[0;97m",
            "\033[1;90m",
            "\033[1;91m",
            "\033[1;92m",
            "\033[1;93m",
            "\033[1;94m",
            "\033[1;95m",
            "\033[1;96m",
            "\033[1;97m",
            "\033[0;100m",
            "\033[0;101m",
            "\033[0;102m",
            "\033[0;103m",
            "\033[0;104m",
            "\033[0;105m",
            "\033[0;106m",
            "\033[0;107m"};

    /**
     * Splits a string into multiple strings each of which length is <= maxCharInLine. The splitting is done by
     * space character if possible, otherwise a word is broken at exactly maxCharInLine.
     * This method preserves all spaces except the one it splits on, for example:
     * string "here is    a  strange string" split on length 8 gives: ["here is ", "  a ", "strange", "string"]
     *
     * @param str           String to split
     * @param maxCharInLine Max length of each split
     * @return List of string that form original string, but each string is as-short-or-shorter than maxCharInLine
     */
    static List<String> splitTextIntoLinesOfMaxLength(String str, int maxCharInLine) {
        List<String> lines = new LinkedList<>();
        StringBuilder line = new StringBuilder(maxCharInLine);
        int offset = 0;

        while (offset < str.length() && maxCharInLine < str.length() - offset) {
            int spaceToWrapAt = str.lastIndexOf(' ', offset + maxCharInLine);

            if (offset < spaceToWrapAt) {
                line.append(str, offset, spaceToWrapAt);
                offset = spaceToWrapAt + 1;
            } else {
                line.append(str, offset, offset + maxCharInLine);
                offset += maxCharInLine;
            }

            lines.add(line.toString());
            line.setLength(0);
        }

        line.append(str.substring(offset));
        lines.add(line.toString());

        return lines;
    }

    private static class LineIterator implements Iterator<String> {
        private final String str;
        private int position = 0;
        private boolean newlineLast = true;

        private LineIterator(String str) {
            this.str = str;
        }

        @Override
        public boolean hasNext() {
            return newlineLast || position < str.length();
        }

        @Override
        public String next() {
            int start = position;
            return str.substring(start, getLineEndPositionAndAdvanceToNextLine());
        }

        public int getLineEndPositionAndAdvanceToNextLine() {
            newlineLast = false;
            for (; position < str.length(); position++) {
                char ch = str.charAt(position);
                if (ch == '\n') {
                    newlineLast = true;
                    return position++;
                }
                if (ch == '\r') {
                    newlineLast = true;
                    if (position + 1 == str.length() || str.charAt(position + 1) != '\n') return position++;
                    position += 2;
                    return position - 2;
                }
            }
            return position;
        }

        public int getPosition() {
            return position;
        }
    }

}

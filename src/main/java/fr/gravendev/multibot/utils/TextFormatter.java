package fr.gravendev.multibot.utils;

/*
    Class written by Antoine James Tournepiche
    From JASC :
        https://github.com/AntoineJT/jasc

    Original file :
        https://github.com/AntoineJT/jasc/blob/0b854e6d265c88ceb49f3a556f2b01a80b87007f/src/com/github/antoinejt/jasc/util/TextFormat.java
*/
public class TextFormatter {

    public static final class FormattedText {
        private final String formattedText;

        private FormattedText(String formattedText) {
            this.formattedText = formattedText;
        }

        @Override
        public String toString() {
            return formattedText;
        } // It's not needed to call that, implicit call will be done when you print the object (?)

        public void print() {
            System.out.print(formattedText);
        }
    }

    public static FormattedText formatLines(String prefix, String separator, String[] lines) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : lines) {
            stringBuilder
                    .append(prefix)
                    .append(line)
                    .append(separator);
        }
        String text = stringBuilder.toString();
        return new FormattedText(text);
    }

    public static FormattedText formatLines(String prefix, String[] lines) {
        return formatLines(prefix, "\n", lines);
    }

    // TODO Find a better name for that I think
    public static FormattedText formatLines(String... lines) {
        return formatLines("", lines);
    }

    public static void printLines(String... lines) {
        FormattedText formattedLines = formatLines(lines);
        System.out.print(formattedLines);
    }

}

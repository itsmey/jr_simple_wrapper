import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.UUID.randomUUID;

class OutputParser {

    private static class ParsingText {
        private StringBuilder text;

        ParsingText() {
            text = new StringBuilder();
        }

        ParsingText(String filename) {
            text = LoadFromFile(filename);
        }

        ParsingText pasteSection(String sectionName, String sectionText) {
            String sectionPattern = "{{{" + sectionName + "}}}";

            int start = text.indexOf(sectionPattern);

            if (start != -1) {
                int end = start + sectionPattern.length();
                text.replace(start, end, sectionText);
            } else {
                System.out.println("section pattern not found!");

            }

            return this;
        }

        ParsingText pasteSection(String sectionName, StringBuilder sectionText) {
            return pasteSection(sectionName, sectionText.toString());
        }

        ParsingText pasteSection(String sectionName, ParsingText sectionText) {
            return pasteSection(sectionName, sectionText.toString());
        }

        ParsingText pasteUUID() {
            return pasteSection("UUID", randomUUID().toString());
        }

        public String toString() {
            return text.toString();
        }

        StringBuilder getText() {
            return text;
        }

        ParsingText append(ParsingText additionalText) {
            text.append(additionalText.toString());

            return this;
        }

    }

    private static String templatesFolder;

    private static StringBuilder LoadFromFile(String filename) {
        File file = new File(templatesFolder + "/" + filename);
        BufferedReader reader = null;

        StringBuilder data = new StringBuilder();

        try {
            reader = new BufferedReader(new FileReader(file));

            String content;
            while ((content = reader.readLine()) != null) {
                data.append(content + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return data;
    }

    private static void WriteToFile(String filename, StringBuilder stub) {
        File file = new File(filename);
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(stub.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static StringBuilder pasteAll(StubParameters stubParameters) {

        List<StubParameters.Column> columns = new ArrayList<StubParameters.Column>();
        columns.add(new StubParameters.Column("№ п/п", "!COUNTER", 50));
        columns.addAll(stubParameters.getColumns());

        int currentWidth = 0;

        ParsingText headers = new ParsingText();
        ParsingText data = new ParsingText();
        ParsingText fields = new ParsingText();

        for (StubParameters.Column column: columns) {
            String width = String.valueOf(column.width);

            headers.append(
                new ParsingText(Constants.STATIC_TEXT_TEMPLATE)
                   .pasteSection("X", String.valueOf(currentWidth))
                   .pasteSection("WIDTH", width)
                   .pasteSection("TITLE", column.title)
                   .pasteUUID()
            );

            String fieldRef = column.fieldName.equals("!COUNTER") ?
                    "$V{REPORT_COUNT}" :
                    "$F{" + column.fieldName + "}";

            data.append(
                new ParsingText(Constants.FIELD_TEMPLATE)
                   .pasteSection("X", String.valueOf(currentWidth))
                   .pasteSection("WIDTH", width)
                   .pasteSection("FIELD_REF", fieldRef)
                   .pasteUUID()
            );

            if (!column.fieldName.equals("!COUNTER")) {
                fields.append(
                        new ParsingText(Constants.FIELD_DECL_TEMPLATE)
                            .pasteSection("FIELD_NAME", column.fieldName)
                );
            }

            currentWidth += column.width;
        }

        ParsingText titleTemplate = new ParsingText(Constants.TITLE_TEMPLATE)
            .pasteSection("TITLE", stubParameters.getTitle())
            .pasteSection("TOTAL_WIDTH", String.valueOf(currentWidth));

        ParsingText commonTemplate = new ParsingText(Constants.JR_TEMPLATE)
            .pasteSection("NAME", stubParameters.getTitle())
            .pasteSection("TITLE", titleTemplate)
            .pasteSection("FIELDS", fields)
            .pasteSection("HEADER", headers)
            .pasteSection("DATA", data)
            .pasteUUID();

        return commonTemplate.getText();
    }

    static void GenerateStub(String templatesFolderName,
                             String stubFileName,
                             StubParameters stubParameters) {
        setTemplatesFolder(templatesFolderName);

        StringBuilder template = pasteAll(stubParameters);

        WriteToFile(stubFileName, template);
    }

    private static void setTemplatesFolder(String templatesFolderName) {
        templatesFolder = templatesFolderName;
    }

}

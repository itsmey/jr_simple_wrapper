import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.UUID.randomUUID;

class OutputParser {
    private static StringBuilder LoadFromFile(String filename) {
        File file = new File(filename);
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

    private static StringBuilder pasteSection(StringBuilder template,
                                      String sectionName,
                                      String sectionText) {
        String sectionPattern = "{{{" + sectionName + "}}}";

        int start = template.indexOf(sectionPattern);

        if (start != -1) {
            int end = start + sectionPattern.length();
            return template.replace(start, end, sectionText);
        } else {
            System.out.println("section pattern not found!");
            return template;
        }
    }

    private static StringBuilder pasteSection(StringBuilder template,
                String sectionName,
                StringBuilder sectionText) {
        return pasteSection(template, sectionName, sectionText.toString());
    }

    private static StringBuilder pasteUUID(StringBuilder template) {
        return pasteSection(template, "UUID", randomUUID().toString());
    }

    private static StringBuilder pasteAll(StringBuilder commonTemplate, StubParameters stubParameters) {

        List<StubParameters.Column> columns = new ArrayList<StubParameters.Column>();
        columns.add(new StubParameters.Column("№ п/п", "!COUNTER", 50));
        columns.addAll(stubParameters.getColumns());

        int currentWidth = 0;

        StringBuilder titleTemplate = LoadFromFile(Constants.TITLE_TEMPLATE);
        StringBuilder headers = new StringBuilder();
        StringBuilder data = new StringBuilder();
        StringBuilder fields = new StringBuilder();

        StringBuilder templ;

        for (StubParameters.Column column: columns) {
            String width = String.valueOf(column.width);

            templ = LoadFromFile(Constants.STATIC_TEXT_TEMPLATE);
            templ = pasteSection(templ, "X", String.valueOf(currentWidth));
            templ = pasteSection(templ, "WIDTH", width);
            templ = pasteSection(templ, "TITLE", column.title);
            templ = pasteUUID(templ);
            headers.append(templ.toString());

            templ = LoadFromFile(Constants.FIELD_TEMPLATE);
            templ = pasteSection(templ, "X", String.valueOf(currentWidth));
            templ = pasteSection(templ, "WIDTH", width);
            if (!column.fieldName.equals("!COUNTER"))
                templ = pasteSection(templ, "FIELD_REF", "$F{" + column.fieldName + "}");
            else
                templ = pasteSection(templ, "FIELD_REF", "$V{REPORT_COUNT}");
            templ = pasteUUID(templ);
            data.append(templ.toString());

            if (!column.fieldName.equals("!COUNTER")) {
                templ = LoadFromFile(Constants.FIELD_DECL_TEMPLATE);
                templ = pasteSection(templ, "FIELD_NAME", column.fieldName);
                fields.append(templ.toString());
            }

            currentWidth += column.width;
        }

        titleTemplate = pasteSection(titleTemplate, "TITLE", stubParameters.getTitle());
        titleTemplate = pasteSection(titleTemplate, "TOTAL_WIDTH", String.valueOf(currentWidth));

        commonTemplate = pasteSection(commonTemplate, "NAME", stubParameters.getTitle());
        commonTemplate = pasteSection(commonTemplate, "TITLE", titleTemplate);
        commonTemplate = pasteSection(commonTemplate, "FIELDS", fields);
        commonTemplate = pasteSection(commonTemplate, "HEADER", headers);
        commonTemplate = pasteSection(commonTemplate, "DATA", data);
        commonTemplate = pasteUUID(commonTemplate);

        return commonTemplate;
    }

    static void GenerateStub(String templateFileName,
                             String stubFileName,
                             StubParameters stubParameters) {
        StringBuilder template = LoadFromFile(templateFileName);

        template = pasteAll(template, stubParameters);

        WriteToFile(stubFileName, template);
    }
}

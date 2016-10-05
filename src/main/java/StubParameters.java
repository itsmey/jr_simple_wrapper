import java.util.ArrayList;
import java.util.List;

class StubParameters {

    static class Column {
        String title;
        String fieldName;
        int width;

        Column(String title, String fieldName, int width) {
            this.title = title;
            this.fieldName = fieldName;
            this.width = width;
        }

        public String toString() {
            return "column title " + title + " field " + fieldName + " width " + width;
        }
    }

    private String title;

    private List<Column> columns;

    StubParameters() {
        columns = new ArrayList<Column>();
    }

    void setTitle(String t) {
        title = t;
    }

    String getTitle() {
        return title;
    }

    void addColumn(String title, String fieldName, int width) {
        Column c = new Column(title, fieldName, width);
        columns.add(c);
    }

    List<Column> getColumns() {
        return columns;
    }

    public String toString() {
        String s = "StubParameters\nTitle " + title + "\n";

        for(Column c: columns)
            s += c.toString() + "\n";

        return s;
    }
}

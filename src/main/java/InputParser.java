
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

class InputParser {
    static StubParameters ProcessXml(String fileName) {
        StubParameters stubParameters = new StubParameters();

        try {

            File inputFile = new File(fileName);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);

            doc.getDocumentElement().normalize();

            Node titleNode = doc.getElementsByTagName("title").item(0);
            stubParameters.setTitle(titleNode.getTextContent());

            NodeList columnNodeList = doc.getElementsByTagName("column");

            for (int i = 0; i < columnNodeList.getLength(); i++) {
                Node columnNode = columnNodeList.item(i);

                String title = ((Element)columnNode).getAttribute("title");
                String fieldName = ((Element)columnNode).getAttribute("field");
                String widthString = ((Element)columnNode).getAttribute("width");

                stubParameters.addColumn(title, fieldName, Integer.parseInt(widthString));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stubParameters;
    }
}

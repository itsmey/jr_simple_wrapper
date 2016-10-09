public class Main {
    public static void main(String[] args) {

        OutputParser.GenerateStub(
                Constants.TEMPLATES_FOLDER,
                Constants.OUTPUT_PATH,
                InputParser.ProcessXml(Constants.INPUT_PATH));
    }
}

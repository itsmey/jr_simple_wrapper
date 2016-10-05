public class Main {
    public static void main(String[] args) {

        OutputParser.GenerateStub(
                Constants.JR_TEMPLATE,
                Constants.OUTPUT_PATH,
                InputParser.ProcessXml(Constants.INPUT_PATH));
    }
}

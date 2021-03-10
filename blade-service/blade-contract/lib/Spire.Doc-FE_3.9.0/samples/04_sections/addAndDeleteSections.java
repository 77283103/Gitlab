public class addAndDeleteSections {
    public static void main(String[] args) {
        //Create word document.
        Document doc = new Document();

        // Load the document from disk.
        doc.loadFromFile("data/SectionTemplate.docx");

        //Add a section
        AddSection(doc);

        //Delete the last section
        DeleteSection(doc);

        String output = "output/addAndDeleteSections.docx";

        //Save to file
        doc.saveToFile(output, FileFormat.Docx);
    }
    private static void AddSection(Document doc)
    {
        //Add a section
        doc.addSection();
    }
    private static void DeleteSection(Document doc)
    {
        //Delete the last section
        doc.getSections().removeAt(doc.getSections().getCount() - 1);
    }
}

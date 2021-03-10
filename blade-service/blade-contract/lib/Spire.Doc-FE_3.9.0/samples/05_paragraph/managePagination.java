public class managePagination {
    public static void main(String[] args) {
        //Create Word document.
        Document document = new Document();

        //Load the file from disk.
        document.loadFromFile("data/Template_Docx_1.docx");

        //Get the first section and the paragraph we want to manage the pagination.
        Section sec = document.getSections().get(0);
        Paragraph para = sec.getParagraphs().get(4);

        //Set the pagination format as Format.PageBreakBefore for the checked paragraph.
        para.getFormat().setPageBreakBefore(true);

        String result = "output/managePagination.docx";

        //Save the file.
        document.saveToFile(result, FileFormat.Docx_2013);
    }
}
